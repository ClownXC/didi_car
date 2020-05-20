package com.cartravel.hbase.org.custom.spark.sql.hbase

import com.cartravel.utils.GlobalConfigUtils
import org.apache.spark.sql.DataFrame

/**
  * Created by zxc
  */
object DataFrameToHbase {

  lazy val save = (result: DataFrame,
                   tableName: String,
                   rowkey: String,
                   regionNum: Int,
                   bulkload: Boolean) => {


    result.write.format(GlobalConfigUtils.getProp("custom.hbase.path"))
      .options(Map(
        "hbase.table.name" -> tableName,
        "hbase.table.rowkey" -> rowkey,
        "hbase.table.columnFamily" -> "MM",
        "hbase.table.regionNum" -> s"${regionNum}",
        "hbase.enable.bulkload" -> s"${bulkload}"
      )).save()
  }

  lazy val save2 = (result: DataFrame,
                   tableName: String,
                   columnFamily: String,
                   rowkey: String,
                   regionNum: Int,
                   bulkload: Boolean) => {


    result.write.format(GlobalConfigUtils.getProp("custom.hbase.path"))
      .options(Map(
        "hbase.table.name" -> tableName,
        "hbase.table.rowkey" -> rowkey,
        "hbase.table.columnFamily" -> columnFamily,
        "hbase.table.regionNum" -> s"${regionNum}",
        "hbase.enable.bulkload" -> s"${bulkload}"
      )).save()
  }
}
