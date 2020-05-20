package com.cartravel.bean

/**
  * Created by zxc
  */
class TaskResult {
  private var taskId:String = _
  private var executorId:String = _
  private var host:String = _
  private var index:String = _
  private var launchTime:String = _
  private var speculative:String = _
  private var finishTime:String = _
  private var failed:String = _
  private var killed:String = _
  private var finished:String = _
  private var running:String = _
  private var successful:String = _
  private var status:String = _
  private var duration:String = _

  def getTaskId  = taskId
  def getExecutorId = executorId
  def getHost = host
  def getIndex = index
  def getLaunchTime = launchTime
  def getSpeculative = speculative
  def getFinishTime = finishTime
  def getFailed = failed
  def getKilled = killed
  def getFinished = finished
  def getRunning = running
  def getSuccessful = successful
  def getStatus = status
  def getDuration = duration


  def setTaskId(values:String) = this.taskId = values
  def setExecutorId(values:String) = this.executorId = values
  def setHost(values:String) = this.host = values
  def setIndex(values:String) = this.index = values
  def setLaunchTime (values:String) =  this.launchTime = values
  def setSpeculative (values:String) =  this.speculative = values
  def setFinishTime (values:String) =  this.finishTime = values
  def setFailed (values:String) =  this.failed = values
  def setKilled (values:String) =  this.killed = values
  def setFinished (values:String) =  this.finished = values
  def setRunning (values:String) =  this.running = values
  def setSuccessful (values:String) =  this.successful = values
  def setStatus (values:String) =  this.status = values
  def setDuration (values:String) =  this.duration = values

}
