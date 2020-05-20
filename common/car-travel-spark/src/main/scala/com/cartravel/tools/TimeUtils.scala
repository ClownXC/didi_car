package com.cartravel.tools

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.apache.commons.lang.time.FastDateFormat
import org.apache.commons.lang3.StringUtils

/**
  * Created by zxc
  */
object TimeUtils {
  def getData(timeStamp:String , format:String):String = {
    val time = new Date()
    val instance = FastDateFormat.getInstance(format)
    val format1 = instance.format(time)
    format1
  }

  def formatYYYYmmdd(time:String):Option[String] = {
    //yyyy-MM-dd HH-mm-ss  -->yyyyMMddHHmmss
    if(StringUtils.isNotBlank(time) && time != ""){
      val fields: Array[String] = time.split(" ")
      if(fields.length > 1){
        Some(fields(0).replace("-","") + fields(1).replace(":" , ""))
      }else{
        None
      }
    }else{
      None
    }
  }
  def YYYYmmdd(time:String):Option[String] = {
    //yyyy-MM-dd HH-mm-ss  -->yyyyMMddHHmmss
    if(StringUtils.isNotBlank(time) && time != ""){
        Some(time.replace("-",""))
    }else{
      None
    }
  }
  //获取当前日志天
  def getNowData():String = {
    val time = new Date()
    val instance = FastDateFormat.getInstance("yyyyMMdd")
    val format1 = instance.format(time)
    format1
  }
  //获取当前日期分钟
  def getNowDataMin():String = {
    val time = new Date()
    val instance = FastDateFormat.getInstance("yyyyMMddHHmm")
    val format1 = instance.format(time)
    format1
  }
  def getNowDataSS():String = {
    val time = new Date()
    val instance = FastDateFormat.getInstance("yyyyMMddHHmmss")
    val format1 = instance.format(time)
    format1
  }

  //给定日期，返回15天之前时间
  def getHalfMonthdate(time:String): String ={
    val  c = Calendar.getInstance()
    c.setTime(new SimpleDateFormat("yyyyMMddHHmm").parse(time))
    val day1 = c.get(Calendar.DATE)
    c.set(Calendar.DATE, day1 - 15)
    val dayAfter = new SimpleDateFormat("yyyyMMddHHmm").format(c.getTime())
    dayAfter

  }
  //返回推送订单和有效订单的表名称结尾
  def getTableDate(): Any ={
    val cal = Calendar.getInstance
    val year = cal.get(Calendar.YEAR)
    var month = cal.get(Calendar.MONTH)
    if(month < 10 ) {
      cal.get(Calendar.YEAR).toString + 0+(cal.get(Calendar.MONTH) + 1).toString
    }else if(month > 10){
      cal.get(Calendar.YEAR).toString + (cal.get(Calendar.MONTH) + 1).toString
    }

  }
  //时间戳转换成时间
  def tranTimeToString(tm:String) :String={
    val fdm = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
    val time = fdm.format(new Date(tm.toLong))
    time
  }






  def main(args: Array[String]): Unit = {

    println(tranTimeToString("1568025609176"))
  }
}
