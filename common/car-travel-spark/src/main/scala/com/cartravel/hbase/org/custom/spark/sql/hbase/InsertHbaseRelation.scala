package com.cartravel.hbase.org.custom.spark.sql.hbase

import org.apache.spark.sql.sources.{BaseRelation, InsertableRelation}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by zxc
  */
private[hbase] case class InsertHbaseRelation(dataFrame: DataFrame,
                                              parameters: Map[String, String])(@transient val sqlContext: SQLContext)
  extends BaseRelation with InsertableRelation {


  override def schema: StructType = dataFrame.schema

  override def insert(data: DataFrame, overwrite: Boolean): Unit = {
    val tableName = parameters.getOrElse("hbase.table.name", sys.error("找不到表"))
    data.writeToHbase(tableName, parameters)
  }
}



