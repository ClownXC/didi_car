package com.cartravel.bean

/**
  * Created by zxc
  */
class Input_output_metrics {
  private var inputMetrics_recordsRead:String = _
  private var inputMetrics_bytesRead:String = _
  private var outputMetrics_recordsWritten:String = _
  private var outputMetrics_bytesWritten:String = _

  def getinputMetrics_recordsRead = inputMetrics_recordsRead
  def getinputMetrics_bytesRead = inputMetrics_bytesRead
  def getoutputMetrics_recordsWritten = outputMetrics_recordsWritten
  def getoutputMetrics_bytesWritten = outputMetrics_bytesWritten

  def setinputMetrics_recordsRead(values:String) = this.inputMetrics_recordsRead = values
  def setinputMetrics_bytesRead(values:String) = this.inputMetrics_bytesRead = values
  def setoutputMetrics_recordsWritten(values:String) = this.outputMetrics_recordsWritten = values
  def setoutputMetrics_bytesWritten(values:String) = this.outputMetrics_bytesWritten = values
}
