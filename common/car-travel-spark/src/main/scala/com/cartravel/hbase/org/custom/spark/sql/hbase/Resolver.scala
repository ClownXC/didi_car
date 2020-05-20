package com.cartravel.hbase.org.custom.spark.sql.hbase

import org.apache.hadoop.hbase.KeyValue
import org.apache.hadoop.hbase.client.{Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.Row
import org.apache.spark.sql.sources.TableScan
import org.apache.spark.sql.types._
import org.joda.time.DateTime

import scala.reflect.internal.util.TableDef.Column

/**
  * Created by zxc
  */
object Resolver extends Serializable {

  /**
    * 解析 rowkey
    * @param result
    * @param resultType
    * @tparam T
    * @tparam S
    * @return
    */
  private def resolveRowkey[T <: Result, S <: String](result: Result, resultType: String): Any = {
    val rowkey = resultType match {
      case "String" => result.getRow.map(_.toChar).mkString.toString
      case "Int" => result.getRow.map(_.toChar).mkString.toInt
      case "Long" => result.getRow.map(_.toChar).mkString.toLong
      case "Double" => result.getRow.map(_.toChar).mkString.toDouble
      case "Float" => result.getRow.map(_.toChar).mkString.toFloat
    }
    rowkey
  }



  /**
    * 解析列和列族
    * @param result
    * @param columnFamily
    * @param columnName
    * @param resultType
    * @tparam T
    * @tparam S
    * @tparam A
    * @tparam B
    * @return
    */
  private def resolveColumn[T <: Result, S <: String, A <: String, B <: String](result: Result,
                                                                                columnFamily: String,
                                                                                columnName: String,
                                                                                resultType: String): Any = {
    // 确保传入的 columnFamily ， 应该在 result 结果集里面
    val column = result.containsColumn(columnFamily.getBytes, columnName.getBytes) match {
      case true =>
        resultType match {
          case "String" => Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName)))
          case "Int" => Bytes.toInt(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName)))
          case "Double" => Bytes.toDouble(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName)))
          case "Long" => Bytes.toLong(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName)))
          case "Float" => Bytes.toFloat(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName)))
        }
      case _ =>
        resultType match {
          case "String" => ""
          case "Int" => 0
          case "Long" => 0l
          case "Double" => 0.0
        }
    }

    column
  }


  /**
    * 对外提供服务
    * @param hbaseSchemaField
    * @param result
    * @return
    */
  def resolve(hbaseSchemaField: HbaseSchemaField, result: Result): Any = {
    //cf:field1 cf:field2
    val split: Array[String] = hbaseSchemaField.fieldName.split(":", -1)
    val cfName = split(0)
    val colName = split(1)

    //把 rowkey 或者列 返回
    var resolveResult: Any = null
    if (cfName == "" && colName == "key") {
      resolveResult = resolveRowkey(result, hbaseSchemaField.fieldType)
    } else {
      resolveResult = resolveColumn(result, cfName, colName, hbaseSchemaField.fieldType)
    }
    resolveResult

  }


  //封装rowkey
  def squareRowkey2(dataType: (StructField, Int)): (Row) => Any = {
    val (structField, index) = dataType
    structField.dataType match {
      case StringType =>
        (row: Row) => row.getString(index)
      case LongType =>
        (row: Row) => row.getLong(index)
      case FloatType =>
        (row: Row) => row.getFloat(index)
      case DoubleType =>
        (row: Row) => row.getDouble(index)
      case IntegerType =>
        (row: Row) => row.getInt(index)
      case BooleanType =>
        (row: Row) => row.getBoolean(index)
      case DateType =>
        (row: Row) => row.getDate(index)
      case TimestampType =>
        (row: Row) => row.getTimestamp(index)
      case BinaryType =>
        (row: Row) => row.getAs[Array[Byte]](index)
      case _ =>
        (row: Row) => row.getString(index)
    }
  }

  //封装普通插入的列
  def squareColumns(dataType: (StructField, Int)): (Put, Row, String) => Unit = {
    val (structField, index) = dataType
    structField.dataType match {
      case StringType =>
        (put: Put, row: Row, cm: String) =>
          if (row.getString(index) == null) put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(""))
          else put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(row.getString(index)))
      case LongType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(row.getLong(index)))
      case FloatType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(row.getFloat(index)))
      case DoubleType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(row.getDouble(index)))
      case IntegerType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(row.getInt(index)))
      case BooleanType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(row.getBoolean(index)))
      case DateType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(new DateTime(row.getDate(index)).getMillis))
      case TimestampType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(new DateTime(row.getTimestamp(index)).getMillis))
      case BinaryType =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), row.getAs[Array[Byte]](index))
      case _ =>
        (put: Put, row: Row, cm: String) =>
          put.addColumn(Bytes.toBytes(cm), Bytes.toBytes(structField.name), Bytes.toBytes(row.getString(index)))
    }
  }

  //封装bulkload
  def squareColumns2bulk(dataType: (StructField, Int)): (Array[Byte], Row, String) => (ImmutableBytesWritable, KeyValue) = {
    val (structField, index) = dataType
    structField.dataType match {
      case StringType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          if (row.getString(index) != null) (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(row.getString(index))))
          else (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes()))
      case LongType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(row.getLong(index))))
      case FloatType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(row.getFloat(index))))
      case DoubleType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(row.getDouble(index))))
      case IntegerType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(row.getInt(index))))
      case BooleanType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(row.getBoolean(index))))
      case DateType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(new DateTime(row.getDate(index)).getMillis)))
      case TimestampType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(new DateTime(row.getTimestamp(index)).getMillis)))
      case BinaryType =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), row.getAs[Array[Byte]](index)))
      case _ =>
        (rk: Array[Byte], row: Row, cm: String) =>
          (new ImmutableBytesWritable(rk), new KeyValue(rk, cm.getBytes(), structField.name.getBytes(), Bytes.toBytes(row.getString(index))))
    }
  }

}


