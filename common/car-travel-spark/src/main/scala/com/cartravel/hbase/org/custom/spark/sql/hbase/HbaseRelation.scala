package com.cartravel.hbase.org.custom.spark.sql.hbase

import com.cartravel.utils.GlobalConfigUtils
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by zxc
  * mysql(binlog) -->maxwell/cancel --> kafka --> ss(offset) ---> Hbase---> 自定义数据源 ---DataFrame-->    T+1
  */
private[hbase] case class HbaseRelation(@transient val hbaseProps: Map[String, String])
                                       (@transient val sqlContext: SQLContext) extends BaseRelation with TableScan {

  /**
    * 1、你要查那张表
    * 2、你查询表的rowkey范围是什么
    * 3、schema：spark的schema和hbase的schema是是什么样
    **/

  //1 你要查那张表
  val hbaseTableName = hbaseProps.getOrElse("hbase_table_name", sys.error("无法拿到查询的表名称"))

  //2你查询表的rowkey范围是什么
  private val rowRange: String = hbaseProps.getOrElse("hbase.table.rowkey", "->")
  private val range: Array[String] = rowRange.split("->", -1)
  val startRowkey = range(0).trim
  val endRowkey = range(1).trim

  //3:获取 hbase要查询的列
  val hbaseTableSchema = hbaseProps.getOrElse("hbase_table_schema", sys.error(" 获取不到查询的hbase列"))
  //4:获取sprk的
  val registerTableSchema = hbaseProps.getOrElse("sparksql_table_schema", sys.error(" 获取不到查询的hbase列"))


  //7:
  private val tmpHbaseSchemaField: Array[HbaseSchemaField] = extractHbaseSchema(hbaseTableSchema)
  //8
  private val registerTableFields: Array[RegisterSchemaField] = extractRegisterSchema(registerTableSchema)

  //10
  private val zipSchema: mutable.LinkedHashMap[HbaseSchemaField, RegisterSchemaField] = tableSchemaFieldMapping(tmpHbaseSchemaField, registerTableFields)

  //12
  private val finalHbaseSchema: Array[HbaseSchemaField] = feedType(zipSchema)

  //15
  private val searchColumns: String = getHbaseSearchSchema(finalHbaseSchema)

  //16  在拉链 ， 把有类型的habse和spark做拉链====schema
  private val fieldStructFieds: mutable.LinkedHashMap[HbaseSchemaField, RegisterSchemaField] = tableSchemaFieldMapping(finalHbaseSchema, registerTableFields)

  /**
    * val hbaseConf = HBaseConfiguration.create()
    *hbaseConf.set("hbase.zookeeper.quorum", GlobalConfigUtils.getProp("hbase.zookeeper.quorum"))//指定zookeeper的地址
    *hbaseConf.set(TableInputFormat.INPUT_TABLE, hbaseTableName)//指定要查询表名
    *hbaseConf.set(TableInputFormat.SCAN_COLUMNS, queryColumns)//指定要查询的列
    *hbaseConf.set(TableInputFormat.SCAN_ROW_START, startRowKey)//指定查询的rowkey范围
    *hbaseConf.set(TableInputFormat.SCAN_ROW_STOP, endRowKey)
    *hbaseConf.set(TableInputFormat.SCAN_CACHEDROWS , "10000")//指定查询时候，缓存多少数据
    *hbaseConf.set(TableInputFormat.SHUFFLE_MAPS , "1000")
    * *
    */
  / //通过newAPIHadoopRDD查询
  *//
  val hbaseRdd: RDD[(ImmutableBytesWritable, Result)]
  = sqlContext.sparkContext.newAPIHadoopRDD(
    *// hbaseConf,
    *// classOf[TableInputFormat]
  ,
  *// classOf
  [ImmutableBytesWritable
  ],
  *// classOf[Result]
    *//
  )
  *//
  *//
  *// TableInputFormat
  .SCAN_COLUMNS MM: field1 MM
  : field2 MM: field3.
  ..
  *// * */


  //5 (MM:id , MM:create_time) --> HbaseSchemaField
  private def extractHbaseSchema[T <: String](hbaseTableSchema: String): Array[HbaseSchemaField] = {
    //5.1:去掉括号 MM:id , MM:create_time
    val fieldStr: String = hbaseTableSchema.trim.drop(1).dropRight(1)
    //MM:id , MM:create_time ---> [MM:id] , [MM:create_time]
    val fieldArray: Array[String] = fieldStr.split(",").map(_.trim)
    val hbaseSchemaFields = fieldArray.map { field =>
      HbaseSchemaField(field, "")
    }
    hbaseSchemaFields
  }

  //6:(driver_id String , create_time String ) --- > RegisterSchemaField[fieldName , String]
  private def extractRegisterSchema[T <: String](registerTableSchema: String): Array[RegisterSchemaField] = {
    //driver_id String , create_time String
    val fieldStr: String = registerTableSchema.trim.drop(1).dropRight(1)
    val fieldArr: Array[String] = fieldStr.split(",").map(_.trim)
    fieldArr.map { field =>
      val strings: Array[String] = field.split("\\s+", -1)
      RegisterSchemaField(strings(0), strings(1))
    }
  }

  /**
    * 把 hbase 的 schema  和 spark 的 schema 拉链起来(顺序)
    *
    * @param tmpHbaseSchemaField
    * @param registerTableFields
    * @tparam T
    * @tparam S
    * @return
    */
  private def tableSchemaFieldMapping[T <: Array[HbaseSchemaField], S <: Array[RegisterSchemaField]](tmpHbaseSchemaField: Array[HbaseSchemaField], registerTableFields: Array[RegisterSchemaField]): mutable.LinkedHashMap[HbaseSchemaField, RegisterSchemaField] = {


    if (tmpHbaseSchemaField.length != registerTableFields.length) {
      sys.error("两个 schema 不一致")
    }
    //把两个表拉链起来
    val zip: Array[(HbaseSchemaField, RegisterSchemaField)] = tmpHbaseSchemaField.zip(registerTableFields)
    //封装到有序的map里面
    val map = new mutable.LinkedHashMap[HbaseSchemaField, RegisterSchemaField]
    for (arr <- zip) {
      map.put(arr._1, arr._2)
    }
    map
  }

  /**
    * 给 hbase 的 schema 补充类型
    *
    * @param zipSchema
    * @tparam T
    * @return
    */
  private def feedType[T <: mutable.LinkedHashMap[HbaseSchemaField, RegisterSchemaField]](zipSchema: mutable.LinkedHashMap[HbaseSchemaField, RegisterSchemaField]): Array[HbaseSchemaField] = {

    val finalHbaseSchema: mutable.Iterable[HbaseSchemaField] = zipSchema.map {
      case (hbaseSchema, registerSchema) =>
        hbaseSchema.copy(fieldType = registerSchema.fieldType)
    }
    finalHbaseSchema.toArray

  }

  /**
    * 构建 hbase 要查询的 schema ---> MM:col1 MM:col2
    *
    * @param finalHbaseSchema
    * @tparam T
    * @return
    */
  private def getHbaseSearchSchema[T <: Array[HbaseSchemaField]](finalHbaseSchema: Array[HbaseSchemaField]): String = {
    val str = ArrayBuffer[String]()
    finalHbaseSchema.foreach { field =>
      if (!isRowkey(field)) {
        str.append(field.fieldName)
      }
    }
    str.mkString(" ")
  }

  //14：识别rowkey   MM:col1
  private def isRowkey[T <: HbaseSchemaField](hbaseSchemaField: HbaseSchemaField): Boolean = {
    val split: Array[String] = hbaseSchemaField.fieldName.split(":", -1)
    val cf = split(0)
    val col = split(1)
    if (cf == null && col == "key") true else false
  }


  override def schema: StructType = {
    val fields: Array[StructField] = finalHbaseSchema.map { field =>
      val name: RegisterSchemaField = fieldStructFieds.getOrElse(field, sys.error(s"这个${field}拿不到!!"))
      val relationType = field.fieldType match {
        case "String" => SchemaType(StringType, nullable = false)
        case "Int" => SchemaType(IntegerType, nullable = false)
        case "Long" => SchemaType(LongType, nullable = false)
        case "Double" => SchemaType(DoubleType, nullable = false)
      }

      StructField(name.fieldName, relationType.dataType, relationType.nullable)
    }
    StructType(fields)
  }

  override def buildScan(): RDD[Row] = {
    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.set("hbase.zookeeper.quorum", GlobalConfigUtils.getProp("hbase.zookeeper.quorum")) //指定zookeeper的地址
    hbaseConf.set(TableInputFormat.INPUT_TABLE, hbaseTableName) //指定要查询表名
    hbaseConf.set(TableInputFormat.SCAN_COLUMNS, searchColumns) //指定要查询的列
    hbaseConf.set(TableInputFormat.SCAN_ROW_START, startRowkey) //指定查询的rowkey范围
    hbaseConf.set(TableInputFormat.SCAN_ROW_STOP, endRowkey)
    hbaseConf.set(TableInputFormat.SCAN_CACHEDROWS, "10000") //指定查询时候，缓存多少数据
    hbaseConf.set(TableInputFormat.SHUFFLE_MAPS, "1000")

    //通过newAPIHadoopRDD查询
    val hbaseRdd: RDD[(ImmutableBytesWritable, Result)] = sqlContext.sparkContext.newAPIHadoopRDD(
      hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    //Result -->RDD[Row] --->DataFrame

    val finalResult: RDD[Row] = hbaseRdd.map(line => line._2).map { result =>
      // 构建一个buffer ， 用来接收hbase的结果
      val values = new ArrayBuffer[Any]()
      finalHbaseSchema.foreach { field =>
        values += Resolver.resolve(field, result)
      }
      Row.fromSeq(values)
    }
    finalResult
  }
}

private case class SchemaType(dataType: DataType, nullable: Boolean)
