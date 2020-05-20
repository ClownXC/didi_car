package com.cartravel.bean

/**
  * Created by zxc
  */
class TaskMetrics {
  private var attemptId:String = _
  private var executorDeserializeTime:String = _
  private var executorDeserializeCpuTime:String = _
  private var executorCpuTime:String = _
  private var diskBytesSpilled:String = _
  private var input_output_metrics:Input_output_metrics = _
  private var shuffle:Shuffle = _
  private var executorRunTime:String = _
  private var jvmGCTime:String = _
  private var memoryBytesSpilled:String = _
  private var peakExecutionMemory:String = _
  private var resultSerializationTime:String = _
  private var resultSize:String = _
  private var updatedBlockStatuses:String = _


  def getattemptId = attemptId
  def getexecutorDeserializeTime = executorDeserializeTime
  def getexecutorDeserializeCpuTime = executorDeserializeCpuTime
  def getexecutorCpuTime = executorCpuTime
  def getdiskBytesSpilled = diskBytesSpilled
  def getInput_output_metrics = input_output_metrics
  def getShuffle = shuffle
  def getexecutorRunTime = executorRunTime
  def getjvmGCTime = jvmGCTime
  def getmemoryBytesSpilled = memoryBytesSpilled
  def getpeakExecutionMemory = peakExecutionMemory
  def getresultSerializationTime = resultSerializationTime
  def getresultSize = resultSize
  def getupdatedBlockStatuses = updatedBlockStatuses


  def setupdatedBlockStatuses(values:String) = this.updatedBlockStatuses = values
  def setattemptId(values:String) = this.attemptId = values
  def setexecutorDeserializeTime(values:String) = this.executorDeserializeTime = values
  def setexecutorDeserializeCpuTime(values:String) = this.executorDeserializeCpuTime = values
  def setexecutorCpuTime(values:String) = this.executorCpuTime = values
  def setdiskBytesSpilled(values:String) = this.diskBytesSpilled = values
  def setInput_output_metrics(input_output_metrics:Input_output_metrics) = this.input_output_metrics = input_output_metrics
  def setShuffle(shuffle: Shuffle) = this.shuffle = shuffle
  def setexecutorRunTime(values:String) = this.executorRunTime = values
  def setjvmGCTime(values:String) = this.jvmGCTime = values
  def setmemoryBytesSpilled(values:String) = this.memoryBytesSpilled = values
  def setpeakExecutionMemory(values:String) = this.peakExecutionMemory = values
  def setresultSerializationTime(values:String) = this.resultSerializationTime = values
  def setresultSize(values:String) =this.resultSize = values

}
