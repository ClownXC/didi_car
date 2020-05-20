package com.cartravel.hbase.org.custom.spark.sql.hbase

import com.cartravel.hbase.hutils.HbaseTools
import com.cartravel.utils.GlobalConfigUtils
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, HTable, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{HFileOutputFormat2, LoadIncrementalHFiles, TableOutputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.execution.datasources.hbase.SerializableConfiguration
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.StructField

import scala.collection.immutable.HashMap

/**
  * Created by zxc
  */
class DataFrame2Hbase(data: DataFrame) extends Serializable {

  def writeToHbase(tableName: String,
                   options: Map[String, String] = new HashMap[String, String]) = {

    //1：获取 rowkey
    val rowkey = options.getOrElse("hbase.table.rowkey", sys.error("没有填写rowkey"))

    //2:  校验 rowkey
    require(data.schema.fields.map(_.name).contains(rowkey), s"当前传的rowkey不合法:${rowkey}")

    //3：准备要插入的列和 rowkey
    //3.1 ： 拿到 dataFrame 的 schema
    val fields: Array[StructField] = data.schema.toArray
    val zipData: Array[(StructField, Int)] = fields.zipWithIndex

    //3,2:从dataFrame里面拿rowkey
    val rowkeyField: (StructField, Int) = zipData.filter(line => line._1.name == rowkey).head

    //3.3:拿到列
    //    val columnFields: Array[(StructField, Int)] = zipData.filter(line => line._1.name != rowkey)

    //    val columnFields: Array[(StructField, Int)] = fields.sortBy(_.name).zipWithIndex.filter(line => line._1.name != rowkey)

    var columnFields: Array[(StructField, Int)] = null
    if (options.getOrElse("hbase.enable.bulkload", sys.error("必须制定是否bulkload")).toBoolean) {
      columnFields = fields.sortBy(_.name).zipWithIndex.filter(line => line._1.name != rowkey)
    } else {
      columnFields = zipData.filter(line => line._1.name != rowkey)
    }

    //4.1:吧rowkey和列转hbase的格式
    val squareRowkey: (Row) => Any = Resolver.squareRowkey2(rowkeyField)

    //4.2:列
    //4.2.1: 普通
    val squareColumns: Array[(Put, Row, String) => Unit] = columnFields.map(line => Resolver.squareColumns(line))

    //4,2,2:bulkload
    val squareColumns2bulk: Array[(Array[Byte], Row, String) => (ImmutableBytesWritable, KeyValue)] = columnFields.map(line => Resolver.squareColumns2bulk(line))
    //5:构建一个hbase的conf
    val conf: SerializableConfiguration = {
      val config = HBaseConfiguration.create()
      config.set("hbase.zookeeper.quorum", GlobalConfigUtils.getProp("hbase.zookeeper.quorum"))
      new SerializableConfiguration(config)
    }

    val hbaseConfig = conf.value

    //列族
    val columnFamily = options.getOrElse("hbase.table.columnFamily", "MM")
    val regionNum = options.getOrElse("hbase.table.regionNum", -1).toString.toInt

    //bulkload   rowkey+列族+列必须   是有序的

    val connection = ConnectionFactory.createConnection(hbaseConfig)
    val admin = connection.getAdmin
    if (!admin.tableExists(TableName.valueOf(tableName)) || !admin.isTableAvailable(TableName.valueOf(tableName))) {

      HbaseTools.createTableBySelfBuilt(connection, TableName.valueOf(tableName), regionNum, Array(columnFamily))
      println(s"建表:${tableName}")
    }
    connection.close()


    //path null
    if (StringUtils.isBlank(hbaseConfig.get("mapreduce.output.fileoutputformat.outputdir"))) {
      hbaseConfig.set("mapreduce.output.fileoutputformat.outputdir", "/tmp/hbase")
    }


    //6：正式写 hFile 操作

    val jobConf = new JobConf(hbaseConfig, this.getClass)
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, tableName)

    //7:指定输出格式
    val job = Job.getInstance(jobConf)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Result])
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])


    //数据 -- 》dataframe
    /**
      * 单条：saveAsNewAPIHadoopDataset ---> RDD[(ImmutableBytesWritable , Put)]
      * bulkload :saveAsNewApiHadoopHfile  --->RDD[(ImmutableBytesWritable , KeyValue)]
      *
      **/

    val rdd: RDD[Row] = data.rdd
    options.getOrElse("hbase.enable.bulkload", "true") match {

      case "true" =>
        val tmpPath = s"/tmp/hbase/bulkload/${tableName}/${System.currentTimeMillis()}"

        def readyBulkload(row: Row): Array[(ImmutableBytesWritable, KeyValue)] = {
          val key = squareRowkey(row).toString
          val rk = Bytes.toBytes(key)
          //Array[(Array[Byte], Row, String)
          squareColumns2bulk.map(line => line.apply(rk, row, columnFamily))
        }
        //将 rdd 成 hfile 文件 -->HDFS
        rdd.flatMap(readyBulkload).sortBy(_._1, true).saveAsNewAPIHadoopFile(
          tmpPath,
          classOf[ImmutableBytesWritable],
          classOf[KeyValue],
          classOf[HFileOutputFormat2],
          job.getConfiguration
        )


        //do bulkload
        val loadIncrementalHFiles = new LoadIncrementalHFiles(hbaseConfig)
        loadIncrementalHFiles.doBulkLoad(new Path(tmpPath), new HTable(hbaseConfig, tableName))


      case "false" => //普通插入
        def readyPut(row: Row) = {
          val key = squareRowkey(row).toString
          val rk = Bytes.toBytes(key)
          //put
          val put = new Put(rk)
          //Array[(Put, Row, String)
          squareColumns.foreach(line => line.apply(put, row, columnFamily))
          (new ImmutableBytesWritable, put)
        }

        rdd.map(readyPut).saveAsNewAPIHadoopDataset(job.getConfiguration)
    }
  }
}
