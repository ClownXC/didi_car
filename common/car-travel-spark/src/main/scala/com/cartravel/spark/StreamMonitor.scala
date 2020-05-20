package com.cartravel.spark

import java.util.Properties

import com.cartravel.common.MailUtil
import com.cartravel.kafka.KafkaManager
import com.cartravel.utils.{DataStruct, GlobalConfigUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.scheduler._
import org.json4s.DefaultFormats
import org.json4s.jackson.Json
import redis.clients.jedis.Jedis

/**
  * Created by zxc
  */
class StreamMonitor(
                   session:SparkSession ,
                   conf:SparkConf ,
                   duration:Int ,
                   appName: String ,
                   rdd:RDD[ConsumerRecord[String, String]] ,
                   kafkaManager: KafkaManager
                   ) extends StreamingListener{

  val currentTimes = System.currentTimeMillis()
  val jedis = new Jedis("cdh-node01")
  jedis.select(3)
  var map = Map[String , String]()

  override def onReceiverStarted(receiverStarted: StreamingListenerReceiverStarted): Unit = super.onReceiverStarted(receiverStarted)

  override def onReceiverStopped(receiverStopped: StreamingListenerReceiverStopped): Unit = {
    val key = s"StreamMonitor_${appName}_${currentTimes}"
    jedis.set(key , Json(DefaultFormats).write(map))
    jedis.expire(key , 3600)
  }

  override def onReceiverError(receiverError: StreamingListenerReceiverError): Unit = {
    val lastError = receiverError.receiverInfo.lastError
    val lastErrorMessage = receiverError.receiverInfo.lastErrorMessage

    if(StringUtils.isNotBlank(lastError)){
      if(conf.getBoolean("enableSendEmailOnTaskFail" , false)){
        val args = Array(GlobalConfigUtils.getProp("mail.host") , s"sparkStreaming 监控任务${appName}:${lastError}" , lastErrorMessage)
        val prop: Properties = DataStruct.convertProp(
          ("mail.host", GlobalConfigUtils.getProp("mail.host")),
          ("mail.transport.protocol", GlobalConfigUtils.getProp("mail.transport.protocol")),
          ("mail.smtp.auth", GlobalConfigUtils.getProp("mail.smtp.auth"))
        )

        MailUtil.sendMail(prop , args)
      }

      Thread.interrupted()
    }
  }

  override def onBatchStarted(batchStarted: StreamingListenerBatchStarted): Unit = {
    super.onBatchStarted(batchStarted)
    //调度时间 : 提交   -time-> 执行
    val schedulingDelay = batchStarted.batchInfo.schedulingDelay.get.toString
    map += ("schedulingDelay" -> schedulingDelay)
  }

  override def onBatchSubmitted(batchSubmitted: StreamingListenerBatchSubmitted): Unit = {
    super.onBatchSubmitted(batchSubmitted)
    //当前批次的提交数据量
    val numRecords = batchSubmitted.batchInfo.numRecords.toString
    map += ("numRecords" -> numRecords)
  }

  //只有这个批次执行成功之后才会回调这个
  override def onBatchCompleted(batchCompleted: StreamingListenerBatchCompleted): Unit = {

    kafkaManager.persistOffset(rdd)
    //这个批次处理的总耗时
    val batch_takeTime = batchCompleted.batchInfo.totalDelay.get.toString
    map += ("batch_takeTime" -> batch_takeTime)

    val batchInfo = batchCompleted.batchInfo
    val processingDelay = batchInfo.processingDelay
    val processingEndTime = batchInfo.processingEndTime
    val processingStartTime = batchInfo.processingStartTime
    val totalDelay = batchInfo.totalDelay.get

    if(duration * 6 < totalDelay * 1000){
      val monitorTile = s"sparkStreaming  ${appName} 程序出现阻塞!!!"
      val monitorContent =
        s"""
          |Streamlistener:
          |总消耗时间：${totalDelay} ,
          |processingDelay:${processingDelay} ,
          |processingStartTime:${processingStartTime} ,
          |processingEndTime:${processingEndTime}
          |请及时检查！！！
        """.stripMargin
      if(conf.getBoolean("enableSendEmailOnTaskFail" , false)){
        val args = Array(GlobalConfigUtils.getProp("mail.host") , s"sparkStreaming监控任务程序出现阻塞!!!${appName}:${monitorTile}" , monitorContent)
        val prop: Properties = DataStruct.convertProp(
          ("mail.host", GlobalConfigUtils.getProp("mail.host")),
          ("mail.transport.protocol", GlobalConfigUtils.getProp("mail.transport.protocol")),
          ("mail.smtp.auth", GlobalConfigUtils.getProp("mail.smtp.auth"))
        )

        MailUtil.sendMail(prop , args)
      }
    }

  }


  override def onOutputOperationCompleted(outputOperationCompleted: StreamingListenerOutputOperationCompleted): Unit = {
    //输出的总耗时
    val duration = outputOperationCompleted.outputOperationInfo.duration.get.toString
    val failureReason = outputOperationCompleted.outputOperationInfo.failureReason.get

    map += ("duration" -> duration)
    if(StringUtils.isNotBlank(failureReason)){
      val monitorTile = s"sparkStreaming  ${appName} 程序出现阻塞!!!"
      val monitorContent =
        s"""
           |Streamlistener:
           |duration：${duration} ,
           |failureReason:${failureReason} ,
           |请及时检查！！！
        """.stripMargin
      if(conf.getBoolean("enableSendEmailOnTaskFail" , false)){
        val args = Array(GlobalConfigUtils.getProp("mail.host") , s"sparkStreaming监控任务[输出出现异常]${appName}:${monitorTile}" , monitorContent)
        val prop: Properties = DataStruct.convertProp(
          ("mail.host", GlobalConfigUtils.getProp("mail.host")),
          ("mail.transport.protocol", GlobalConfigUtils.getProp("mail.transport.protocol")),
          ("mail.smtp.auth", GlobalConfigUtils.getProp("mail.smtp.auth"))
        )

        MailUtil.sendMail(prop , args)
      }
    }
  }
}
