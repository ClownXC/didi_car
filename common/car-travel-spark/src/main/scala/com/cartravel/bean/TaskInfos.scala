package com.cartravel.bean

/**
  * Created by zxc
  */
class TaskInfos {

  private var executorId:String = _
  private var host:String = _
  private var index:String = _
  private var killed:String = _
  private var speculative:String = _
  private var taskLocality:String = _
  private var duration:String = _
  private var finished:String = _
  private var taskId:String = _
  private var id:String = _
  private var running:String = _


  def getexecutorId = executorId
  def gethost = host
  def getindex = index
  def getkilled = killed
  def getspeculative = speculative
  def gettaskLocality = taskLocality
  def getduration = duration
  def getfinished = finished
  def gettaskId = taskId
  def getid = id
  def getrunning = running


  def setexecutorId(values:String) = this.executorId =  values
  def sethost(values:String) = this.host =  values
  def setindex(values:String) = this.index =  values
  def setkilled(values:String) = this.killed =  values
  def setspeculative(values:String) = this.speculative =  values
  def settaskLocality(values:String) = this.taskLocality =  values
  def setduration(values:String) = this.duration =  values
  def setfinished(values:String) = this.finished =  values
  def settaskId(values:String) = this.taskId =  values
  def setid(values:String) = this.id =  values
  def setrunning(values:String) = this.running =  values


}
