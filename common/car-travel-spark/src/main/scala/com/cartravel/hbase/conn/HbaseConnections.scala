package com.cartravel.hbase.conn

import com.cartravel.loggings.Logging
import com.cartravel.utils.GlobalConfigUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}

/**
  * Created by zxc
  */
object HbaseConnections extends Serializable with Logging{


  def getHbaseConn: Connection = {
    try{
      lazy val config: Configuration = HBaseConfiguration.create()
      config.set("hbase.zookeeper.quorum" , GlobalConfigUtils.getProp("hbase.zookeeper.quorum"))
      config.set("hbase.master" , GlobalConfigUtils.getProp("hbase.master"))
      config.set("hbase.zookeeper.property.clientPort" , GlobalConfigUtils.getProp("hbase.zookeeper.property.clientPort"))
//      config.set("hbase.rpc.timeout" , GlobalConfigUtils.rpcTimeout)
//      config.set("hbase.client.operator.timeout" , GlobalConfigUtils.operatorTimeout)
//      config.set("hbase.client.scanner.timeout.period" , GlobalConfigUtils.scannTimeout)
//      config.set("hbase.client.ipc.pool.size","200");
      val connection = ConnectionFactory.createConnection(config)
      connection

    }catch{
      case exception: Exception =>
        error("HBase获取连接失败")
        null
    }
  }

  def closeConn(conn : Connection) = {
    try {
      if (!conn.isClosed){
        conn.close()
      }
    }catch {
      case exception: Exception =>
        error("HBase 连接关闭失败")
        exception.printStackTrace()
    }
  }


}
