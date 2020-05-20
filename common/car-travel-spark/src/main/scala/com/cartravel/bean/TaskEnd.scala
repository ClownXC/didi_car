package com.cartravel.bean

/**
  * Created by zxc
  */
class TaskEnd {
  private var taskMetrics:TaskMetrics=_
  private var taskInfo:TaskInfos=_
  private var stageId:String = _
  private var failureReason:String = _
  private var taskType:String = _
  private var stageAttemptId:String = _



  def getTaskMetrics = taskMetrics
  def getstageId = stageId
  def getfailureReason = failureReason
  def getTaskInfo = taskInfo
  def gettaskType = taskType
  def getstageAttemptId = stageAttemptId

  def setTaskMetrics(taskMetrics:TaskMetrics) = this.taskMetrics = taskMetrics
  def setstageId(values:String) = this.stageId = values
  def setfailureReason(values:String) = this.failureReason = values
  def setTaskInfo(taskInfo:TaskInfos) = this.taskInfo = taskInfo
  def settaskType(values:String) = this.taskType = values
  def setstageAttemptId(values:String) = this.stageAttemptId = values

}
