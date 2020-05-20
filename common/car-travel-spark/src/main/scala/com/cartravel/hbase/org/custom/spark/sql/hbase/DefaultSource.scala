package com.cartravel.hbase.org.custom.spark.sql.hbase

import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider, RelationProvider}

/**
  * Created by zxc
  */
class DefaultSource extends RelationProvider with CreatableRelationProvider{


  /**
    *
    * @param sqlContext
    * @param parameters
    * @return
    */
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    HbaseRelation(parameters)(sqlContext)
  }

  /**
    *
    * @param sqlContext
    * @param mode
    * @param parameters
    * @param data
    * @return
    */
  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    val insertHbaseRelation = new InsertHbaseRelation(data,parameters)(sqlContext)
    insertHbaseRelation.insert(data , false)
    insertHbaseRelation
  }
}
