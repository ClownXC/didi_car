package com.cartravel.tools

import com.cartravel.bean.{DriverInfo, Opt_alliance_business, OrderInfo, RegisterUsers}
import com.cartravel.hbase.hutils.HbaseTools
import com.cartravel.loggings.Logging
import com.cartravel.reflect.ReflectBean
import com.cartravel.utils.GlobalConfigUtils
import org.apache.hadoop.hbase.client.Connection

/**
  * Created by zxc
  */
object StructInterpreter extends Logging {


  val columnFamily = GlobalConfigUtils.heartColumnFamily
  val order_info_table = GlobalConfigUtils.getProp("syn.table.order_info")
  val renter_info_table = GlobalConfigUtils.getProp("syn.table.renter_info")
  val driver_info_table = GlobalConfigUtils.getProp("syn.table.driver_info")
  val opt_alliance_business = GlobalConfigUtils.getProp("syn.table.opt_alliance_business")


  def interpreter(interpreter: String, parse: (String, Any), conn: Connection): Unit = {
    interpreter match {
      //订单表
      case tableName if (tableName.startsWith(order_info_table)) =>
        val data = parse._2.asInstanceOf[OrderInfo]
        //落地Hbase表明 = database_table
        val tableName = order_info_table
        //TODO
        val type_ = data.operatorType
        if (data.id != null) {
          //数据落地操作
          val mapData: Map[String, String] = ReflectBean.reflect(data, tableName)
          //订单时间
          val create_time = data.create_time
          val tmpTime = TimeUtils.formatYYYYmmdd(create_time).get
          //TODO 确保时间对上
          val times = tmpTime.substring(0, tmpTime.length - 2)
          val rowkey = data.id + "_" + tmpTime
          //TODO 暂时只处理新增数据
          type_ match {
            case "insert" =>
              HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
            case "update" =>
              HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
            case _ => Nil
          }
        }

      //用户表
      case tableName if (tableName.equals(renter_info_table) || tableName == renter_info_table) =>
        //注册表(用户表)
        val data = parse._2.asInstanceOf[RegisterUsers]
        //落地Hbase表明 = database_table
        val tableName = renter_info_table
        val type_ = data.operatorType
        //数据落地操作
        val mapData: Map[String, String] = ReflectBean.reflect(data, tableName)
        //订单时间
        val create_time = Some(data.create_time).getOrElse(data.last_logon_time)
        val tmpTime = TimeUtils.formatYYYYmmdd(create_time).get
        //          val times = tmpTime.substring(0, tmpTime.length - 2)
        //TODO 确保时间对上
        val rowkey = data.id + "_" + tmpTime
        type_ match {
          case "insert" =>
            HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
          case "update" =>
            HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
          case _ => Nil
        }
      //司机表
      case tableName if (tableName.equals(driver_info_table) || tableName == driver_info_table) =>
        //注册表（司机）
        val data = parse._2.asInstanceOf[DriverInfo]
        val tableName = driver_info_table
        val type_ = data.operatorType
        val mapData: Map[String, String] = ReflectBean.reflect(data, tableName)
        //rowkey : 司机Id_年月日
        val create_time = Some(data.create_time).getOrElse(data.id)
        val tmpTime = TimeUtils.formatYYYYmmdd(create_time).get
        //        val rowkey = data.id + "_" + times
        val rowkey = data.id + "_" + tmpTime
        type_ match {
          case "insert" =>
            HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
          case "update" =>
            HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
          case _ => Nil
        }
      //加盟表
      case tableName if (tableName.equals(opt_alliance_business) || tableName == opt_alliance_business) =>

        val data = parse._2.asInstanceOf[Opt_alliance_business]
        val tableName = opt_alliance_business
        val type_ = data.operatorType
        val mapData: Map[String, String] = ReflectBean.reflect(data, tableName)
        val create_time = Some(data.create_time).getOrElse(data.update_time)
        val tmpTime = TimeUtils.formatYYYYmmdd(create_time).get
        val rowkey = data.id_ + "_" + tmpTime
        type_ match {
          case "insert" =>
            HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
          case "update" =>
            HbaseTools.putMapData(conn, tableName, 8, columnFamily, rowkey, mapData)
          case _ => Nil
        }
      case _ =>
        info("######其他表##### : {}", interpreter)


    }

  }
}
