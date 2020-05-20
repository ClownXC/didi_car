package com.cartravel.hbase.org.custom.spark.sql

import org.apache.spark.sql.DataFrame

/**
  * Created by zxc
  */
package object hbase {

  //类型的封装（spark和hbase）
  abstract class SchemaField extends Serializable

  //spark的schema封装
  case class RegisterSchemaField(fieldName:String , fieldType:String) extends SchemaField with Serializable

  //hbase的schema封装
  case class HbaseSchemaField(fieldName:String , fieldType:String) extends SchemaField with Serializable

  /**
    * sparkContext.newAPIHadoopRDD -->RDD[xxxxx , Result]
    *
    *
    *
    * val hbaseConf = HBaseConfiguration.create()
    hbaseConf.set("hbase.zookeeper.quorum", GlobalConfigUtils.getProp("hbase.zookeeper.quorum"))//指定zookeeper的地址
    hbaseConf.set(TableInputFormat.INPUT_TABLE, hbaseTableName)//指定要查询表名
    hbaseConf.set(TableInputFormat.SCAN_COLUMNS, queryColumns)//指定要查询的列
    hbaseConf.set(TableInputFormat.SCAN_ROW_START, startRowKey)//指定查询的rowkey范围
    hbaseConf.set(TableInputFormat.SCAN_ROW_STOP, endRowKey)
    hbaseConf.set(TableInputFormat.SCAN_CACHEDROWS , "10000")//指定查询时候，缓存多少数据
    hbaseConf.set(TableInputFormat.SHUFFLE_MAPS , "1000")

    //通过newAPIHadoopRDD查询
    val hbaseRdd: RDD[(ImmutableBytesWritable, Result)] = sqlContext.sparkContext.newAPIHadoopRDD(
      hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    * */


  implicit def DataFrameToHbase(data:DataFrame):DataFrame2Hbase = {
    new DataFrame2Hbase(data)
  }
}
