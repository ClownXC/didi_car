package com.cartravel.bean

/**
  * Created by zxc
  */
class Job_end {
  private var jobId:String  = _
  private var endTime:String = _
  private var jobResult:String = _

  def getJobId = jobId
  def getendTime = endTime
  def getjobResult = jobResult

  def setjobId(jobId:String)  = this.jobId = jobId
  def setendTime(endTime:String) = this.endTime = endTime
  def setjobResult(jobResult:String) = this.jobResult = jobResult
}
