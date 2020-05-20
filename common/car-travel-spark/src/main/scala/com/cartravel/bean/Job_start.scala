package com.cartravel.bean

/**
  * Created by zxc
  */
class Job_start {
  private var jobId:String  = _
  private var interruptOnCancel:String  = _
  private var batchTime:String  = _
  private var checkpointAllMarkedAncestors:String  = _
  private var schedulerPool:String  = _
  private var startTime:String  = _
  private var stageId:String  = _
  private var failureReason:String = _
  private var stateName:String = _
  private var numTasks:String = _


  def getJobId = jobId
  def getInterruptOnCancel = interruptOnCancel
  def getBatchTime = batchTime
  def getCheckpointAllMarkedAncestors = checkpointAllMarkedAncestors
  def getSchedulerPool = schedulerPool
  def getStartTime = startTime
  def getStageId = stageId
  def getFailureReason = failureReason
  def getStateName = stateName
  def getNumTasks = numTasks



  def setjobId(jobId:String)  = this.jobId = jobId
  def setinterruptOnCancel(interruptOnCancel:String) = this.interruptOnCancel = interruptOnCancel
  def setbatchTime(batchTime:String) = this.batchTime = batchTime
  def setcheckpointAllMarkedAncestors(checkpointAllMarkedAncestors:String) = this.checkpointAllMarkedAncestors = checkpointAllMarkedAncestors
  def setschedulerPool(schedulerPool:String) = this.schedulerPool = schedulerPool
  def setstartTime(startTime:String) = this.startTime = startTime
  def setstageId(stageId:String) =  this.stageId = stageId
  def setfailureReason(failureReason:String) = this.failureReason = failureReason
  def setstateName(stateName:String) = this.stateName = stateName
  def setnumTasks(numTasks:String) = this.numTasks = numTasks









}
