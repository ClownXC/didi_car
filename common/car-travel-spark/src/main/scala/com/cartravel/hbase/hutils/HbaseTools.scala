package com.cartravel.hbase.hutils

import com.cartravel.loggings.Logging
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client.{Connection, Put, Table}
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm
import org.apache.hadoop.hbase.regionserver.BloomType
import org.apache.hadoop.hbase.util.Bytes

/**
  * Created by zxc
  */
object HbaseTools extends Logging{
  //建表
  def createTableBySelfBuilt(conn:Connection , tableName: TableName , retionNum:Int , column:Array[String]) = {
    this.synchronized{
      val admin = conn.getAdmin
      try{
        if(!admin.tableExists(tableName)){
          //表描述器
          val tbDesc = new HTableDescriptor(tableName)
          if(column !=  null){//如果列族不为空，则迭代
            column.foreach(c  => {
              val hcd = new HColumnDescriptor(c.getBytes())
              hcd.setBlockCacheEnabled(false)
              hcd.setMaxVersions(1)
              hcd.setBloomFilterType(BloomType.ROW)
//              hcd.setCompressionType(Algorithm.SNAPPY)
              tbDesc.addFamily(hcd)
            })
          }

          val splitKeysBuilder = new SplitKeysBuilder()
          val splitKeys = splitKeysBuilder.getSplitKeys(retionNum)
          admin.createTable(tbDesc , splitKeys)
        }
      }catch {
        case e: Exception => // 邮件报警
        e.getMessage
      }
    }
  }

  //插入数据
  def putMapData(
                connection: Connection ,
                tableName:String ,
                regionNum:Int ,
                columnFamily:String ,
                key:String ,
                mapData:Map[String , String]
                ) = {
    val admin = connection.getAdmin

    val tb = TableName.valueOf(tableName)
    val table: Table = connection.getTable(tb)
    try{
      val rk = RowkeyUtils.getRowkey(key)
      val rowkey = Bytes.toBytes(rk)
      val put = new Put(rowkey)
      if(mapData.size > 0){
        for((k,v) <- mapData){
          put.addColumn(Bytes.toBytes(columnFamily) , Bytes.toBytes(k) , Bytes.toBytes(v.toString))
        }
      }
      table.put(put)
    }catch{
      case e:Exception =>
      //邮件
    }finally {
      table.close()
    }


  }
}
