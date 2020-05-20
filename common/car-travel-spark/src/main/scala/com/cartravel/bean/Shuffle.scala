package com.cartravel.bean

/**
  * Created by zxc
  */
class Shuffle {
  private var shuffleReadMetrics_totalBytesRead:String = _
  private var shuffleReadMetrics_recordsRead:String = _
  private var shuffleReadMetrics_fetchWaitTime:String = _
  private var shuffleReadMetrics_localBlocksFetched:String = _
  private var shuffleReadMetrics_localBytesRead:String = _
  private var shuffleReadMetrics_remoteBlocksFetched:String = _
  private var shuffleWriteMetrics_bytesWritten:String = _
  private var shuffleWriteMetrics_recordsWritten:String = _
  private var shuffleWriteMetrics_writeTime:String = _
  private var shuffleReadMetrics_remoteBytesRead:String = _
  private var shuffleReadMetrics_totalBlocksFetched:String  = _

  def getshuffleReadMetrics_totalBytesRead = shuffleReadMetrics_totalBytesRead
  def getshuffleReadMetrics_recordsRead = shuffleReadMetrics_recordsRead
  def getshuffleReadMetrics_fetchWaitTime = shuffleReadMetrics_fetchWaitTime
  def getshuffleReadMetrics_localBlocksFetched = shuffleReadMetrics_localBlocksFetched
  def getshuffleReadMetrics_localBytesRead = shuffleReadMetrics_localBytesRead
  def getshuffleReadMetrics_remoteBlocksFetched = shuffleReadMetrics_remoteBlocksFetched
  def getshuffleWriteMetrics_bytesWritten = shuffleWriteMetrics_bytesWritten
  def getshuffleWriteMetrics_recordsWritten = shuffleWriteMetrics_recordsWritten
  def getshuffleWriteMetrics_writeTime = shuffleWriteMetrics_writeTime
  def getshuffleReadMetrics_remoteBytesRead = shuffleReadMetrics_remoteBytesRead
  def getshuffleReadMetrics_totalBlocksFetched = shuffleReadMetrics_totalBlocksFetched

  def setshuffleReadMetrics_totalBytesRead(values:String) = this.shuffleReadMetrics_totalBytesRead = values
  def setshuffleReadMetrics_recordsRead(values:String) = this.shuffleReadMetrics_recordsRead = values
  def setshuffleReadMetrics_fetchWaitTime(values:String) = this.shuffleReadMetrics_fetchWaitTime = values
  def setshuffleReadMetrics_localBlocksFetched(values:String) = this.shuffleReadMetrics_localBlocksFetched = values
  def setshuffleReadMetrics_localBytesRead(values:String) = this.shuffleReadMetrics_localBytesRead = values
  def setshuffleReadMetrics_remoteBlocksFetched(values:String) = this.shuffleReadMetrics_remoteBlocksFetched = values
  def setshuffleWriteMetrics_bytesWritten(values:String) = this.shuffleWriteMetrics_bytesWritten = values
  def setshuffleWriteMetrics_recordsWritten(values:String) = this.shuffleWriteMetrics_recordsWritten = values
  def setshuffleWriteMetrics_writeTime(values:String) = this.shuffleWriteMetrics_writeTime = values
  def setshuffleReadMetrics_remoteBytesRead(values:String) = this.shuffleReadMetrics_remoteBytesRead = values
  def setshuffleReadMetrics_totalBlocksFetched(values:String) = this.shuffleReadMetrics_totalBlocksFetched = values
}
