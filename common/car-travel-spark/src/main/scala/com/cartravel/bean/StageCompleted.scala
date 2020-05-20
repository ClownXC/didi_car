package com.cartravel.bean

/**
  * Created by zxc
  */
class StageCompleted {
  private var taskMetrics:TaskMetrics=_
  private var numTasks:String = _
  private var name:String = _
  private var completionTime:String = _
  private var submissionTime:String = _
  private var stageId:String = _
  private var failureReason:String = _



  def getnumTasks = numTasks
  def getname = name
  def getcompletionTime = completionTime
  def getsubmissionTime = submissionTime
  def getstageId = stageId
  def getfailureReason = failureReason
  def getTaskMetrics = taskMetrics



  def setnumTasks(values:String) = this.numTasks = values
  def setname(values:String) = this.name = values
  def setcompletionTime(values:String) = this.completionTime = values
  def setsubmissionTime(values:String) = this.submissionTime = values
  def setstageId(values:String) = this.stageId = values
  def setfailureReason(values:String) = this.failureReason = values
  def setTaskMetrics(taskMetrics: TaskMetrics) = this.taskMetrics = taskMetrics




}
