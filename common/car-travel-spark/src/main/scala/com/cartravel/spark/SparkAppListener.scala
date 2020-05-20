package com.cartravel.spark

import java.util.Properties
import javafx.scene.chart.NumberAxis.DefaultFormatter

import com.cartravel.common.MailUtil
import com.cartravel.loggings.Logging
import com.cartravel.utils.{DataStruct, GlobalConfigUtils}
import org.apache.spark._
import org.apache.spark.scheduler.{SparkListener, SparkListenerTaskEnd}
import org.json4s.DefaultFormats
import org.json4s.jackson.Json
import redis.clients.jedis.Jedis

/**
  * Created by zxc
  */


/**
  * 通过 SparkListener 对 App,job,stage,task，executor, blockManager 等产生的指标进实时的收集，
  * 并在这些事件触发时嵌入一些代码，可以达到当某些指标达到警戒阈值触发自定义的一些规则
  * @param conf
  */
class SparkAppListener(conf: SparkConf) extends SparkListener with Logging {

  val defaultRedisConfig = "jedisConfig.properties"
  val jedis = new Jedis("cdh-node02")


  /**
    * 当 task 执行完成时执行的回调函数
    * @param taskEnd
    */
  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
    /**
      * 1、taskMetrics
      * 2、shuffle
      * 3、task 运行（input output）
      * 4、taskInfo
      **/
    jedis.select(2)
    val currentTimestamp = System.currentTimeMillis()

    val metrics = taskEnd.taskMetrics

    val taskMetricsMap = scala.collection.mutable.HashMap(
      "executorDeserializeTime" -> metrics.executorDeserializeTime, // executor 的反序列化时间
      "executorDeserializeCpuTime" -> metrics.executorDeserializeCpuTime, // executor 的反序列化的 cpu 时间
      "executorRunTime" -> metrics.executorRunTime, // executor 的运行时间
      "resultSize" -> metrics.resultSize, //结果集大小
      "jvmGCTime" -> metrics.jvmGCTime,
      "resultSerializationTime" -> metrics.resultSerializationTime,
      "memoryBytesSpilled" -> metrics.memoryBytesSpilled,
      "diskBytesSpilled" -> metrics.diskBytesSpilled,
      "peakExecutionMemory" -> metrics.peakExecutionMemory //executor 的最大内存
    )


    val taskMetricsKey = s"taskMetrics_${currentTimestamp}"
    jedis.set(taskMetricsKey, Json(DefaultFormats).write(taskMetricsMap))
    jedis.expire(taskMetricsKey, 3600)


    //####################shuffle#######
    val shuffleReadMetrics = metrics.shuffleReadMetrics
    val shuffleWriteMetrics = metrics.shuffleWriteMetrics

    val shuffleMap = scala.collection.mutable.HashMap(
      "remoteBlocksFetched" -> shuffleReadMetrics.remoteBlocksFetched, //shuffle 远程拉取数据块
      "localBlocksFetched" -> shuffleReadMetrics.localBlocksFetched,
      "remoteBytesRead" -> shuffleReadMetrics.remoteBytesRead, //shuffle远程读取的字节数
      "localBytesRead" -> shuffleReadMetrics.localBytesRead,
      "fetchWaitTime" -> shuffleReadMetrics.fetchWaitTime,
      "recordsRead" -> shuffleReadMetrics.recordsRead, //shuffle读取的记录总数
      "bytesWritten" -> shuffleWriteMetrics.bytesWritten, //shuffle写的总大小
      "recordsWritte" -> shuffleWriteMetrics.recordsWritten, //shuffle写的总记录数
      "writeTime" -> shuffleWriteMetrics.writeTime
    )

    val shuffleKey = s"shuffleKey${currentTimestamp}"
    jedis.set(shuffleKey, Json(DefaultFormats).write(shuffleMap))
    jedis.expire(shuffleKey, 3600)


    //####################input   output#######
    val inputMetrics = metrics.inputMetrics
    val outputMetrics = metrics.outputMetrics
    val input_output = scala.collection.mutable.HashMap(
      "bytesRead" -> inputMetrics.bytesRead, //读取的大小
      "recordsRead" -> inputMetrics.recordsRead, //总记录数
      "bytesWritten" -> outputMetrics.bytesWritten,
      "recordsWritten" -> outputMetrics.recordsWritten
    )
    val input_outputKey = s"input_outputKey${currentTimestamp}"
    jedis.set(input_outputKey, Json(DefaultFormats).write(input_output))
    jedis.expire(input_outputKey, 3600)


    //####################taskInfo#######
    val taskInfo = taskEnd.taskInfo

    val taskInfoMap = scala.collection.mutable.HashMap(
      "taskId" -> taskInfo.taskId,
      "host" -> taskInfo.host,
      "speculative" -> taskInfo.speculative, //推测执行
      "failed" -> taskInfo.failed,
      "killed" -> taskInfo.killed,
      "running" -> taskInfo.running
    )


    val taskInfoKey = s"taskInfo${currentTimestamp}"
    jedis.set(taskInfoKey, Json(DefaultFormats).write(taskInfoMap))
    jedis.expire(taskInfoKey, 3600)


    //####################邮件告警#######
    if (taskInfo != null && taskEnd.stageAttemptId != -1) {
      val reason: TaskEndReason = taskEnd.reason

      val errorMsg = reason match {
        case kill: TaskKilledException => Some(kill.getMessage)
        case e: TaskFailedReason => Some(e.toErrorString)
        case e: ExceptionFailure => Some(e.toErrorString)
        case e: Exception => Some(e.getMessage)
        case _ => None
      }

      if (errorMsg.nonEmpty) {
        if (conf.getBoolean("enableSendEmailOnTaskFail", false)) {


          val args = Array(GlobalConfigUtils.getProp("mail.host"), s"spark监控任务:${reason}", errorMsg.get)
          val prop: Properties = DataStruct.convertProp(
            ("mail.host", GlobalConfigUtils.getProp("mail.host")),
            ("mail.transport.protocol", GlobalConfigUtils.getProp("mail.transport.protocol")),
            ("mail.smtp.auth", GlobalConfigUtils.getProp("mail.smtp.auth"))
          )

          MailUtil.sendMail(prop, args)
        }
      }

    }


  }
}
