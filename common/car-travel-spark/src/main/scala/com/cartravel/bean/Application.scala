package com.cartravel.bean

/**
  * Created by zxc
  */
class Application{
  private var appId:String  = _
  private var appName:String  = _
  private var driverLogs:String  = _
  private var sparkUser:String = _
  private var startTime:String  = _
  private var endTime:String = _

  def setAppId(appId:String) = this.appId = appId
  def setAppName(appName:String) = this.appName = appName
  def setDriverLogs(driverLogs:String) = this.driverLogs = driverLogs
  def setSparkUser(sparkUser:String) = this.sparkUser = sparkUser
  def setStartTime(startTime:String) = this.startTime = startTime
  def setEndTime(endTime:String) = this.endTime = endTime

  def getAppId = appId
  def getAppName = appName
  def getDriverLogs = appName
  def getSparkUser = sparkUser
  def getStartTime = startTime
  def getEndTime = endTime
}



