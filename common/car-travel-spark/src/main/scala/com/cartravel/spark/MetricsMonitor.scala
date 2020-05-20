package com.cartravel.spark

import com.cartravel.loggings.Logging
import com.cartravel.tools.{PropertiesUtil, SparkMetricsUtils}
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import org.json4s.DefaultFormats
import org.json4s.jackson.Json
import redis.clients.jedis.Jedis

/**
  * Created by zxc
  */
object MetricsMonitor extends Logging{
  //redis配置文件
  val defaultRedisConfig = "jedisConfig.properties"
  //redis有效期
  //监控记录有效期，单位秒
  val monitorDataExpTime: Int = PropertiesUtil.getStringByKey("cluster.exptime.monitor", defaultRedisConfig).toInt

  val metrics = (applicationId:String , applicationUniqueName:String , rdd:RDD[String])  => {
    //链路统计
    val serversCount = rdd.map { record =>
      ("count" , 1)
    }.reduceByKey { (x, y) => x + y }
    //链路统计结果
    val serversCountMap = serversCount.collectAsMap()
    var sourceCount = 0
    for (map <- serversCountMap) {
      val value = map._2
      sourceCount = sourceCount + value
    }
    //done:Spark性能实时监控
    //TODO 监控数据获取 yarn
    //val sparkDriverHost = sc.getConf.get("spark.org.apache.hadoop.yarn.server.webproxy.amfilter.AmIpFilter.param.PROXY_URI_BASES")
    //监控信息页面路径为集群路径+/proxy/+应用id+/metrics/json
    //val url = s"${sparkDriverHost}/metrics/json"
    //TODO local模式的路径
    val url = "http://localhost:4040/metrics/json/"
    //TODO
    val jsonObj = SparkMetricsUtils.getMetricsJson(url)
    //应用的一些监控指标在节点gauges下
    val gauges = jsonObj.getJSONObject("gauges")
    //监控信息的json路径：应用id+.driver.+应用名称+具体的监控指标名称
    //最近完成批次的处理开始时间-Unix时间戳（Unix timestamp）-单位：毫秒
    val startTimePath = applicationId + ".driver." + applicationUniqueName + ".StreamingMetrics.streaming.lastCompletedBatch_processingStartTime"
    val startValue = gauges.getJSONObject(startTimePath)
    val processingStartTime = startValue.getLong("value")
    //最近完成批次的处理结束时间-Unix时间戳（Unix timestamp）-单位：毫秒
    val endTimePath = applicationId + ".driver." + applicationUniqueName + ".StreamingMetrics.streaming.lastCompletedBatch_processingEndTime"
    val endValue = gauges.getJSONObject(endTimePath)
    val processingEndTime = endValue.getLong("value")

    val processTime = processingEndTime - processingStartTime

    //#####################BlockManager###################################
    val diskSpaceUsed_MB = gauges.getJSONObject(applicationId + ".driver.BlockManager.disk.diskSpaceUsed_MB").getLong("value")//使用的磁盘空间
    val maxMem_MB = gauges.getJSONObject(applicationId + ".driver.BlockManager.memory.maxMem_MB").getLong("value") //使用的最大内存
    val memUsed_MB = gauges.getJSONObject(applicationId + ".driver.BlockManager.memory.memUsed_MB").getLong("value")//内存使用情况
    val remainingMem_MB = gauges.getJSONObject(applicationId + ".driver.BlockManager.memory.remainingMem_MB").getLong("value") //闲置内存
    //#####################stage###################################
    val activeJobs = gauges.getJSONObject(applicationId + ".driver.DAGScheduler.job.activeJobs").getLong("value")//当前正在运行的job
    val allJobs = gauges.getJSONObject(applicationId + ".driver.DAGScheduler.job.allJobs").getLong("value")//总job数
    val failedStages = gauges.getJSONObject(applicationId + ".driver.DAGScheduler.stage.failedStages").getLong("value")//失败的stage数量
    val runningStages = gauges.getJSONObject(applicationId + ".driver.DAGScheduler.stage.runningStages").getLong("value")//正在运行的stage
    val waitingStages = gauges.getJSONObject(applicationId + ".driver.DAGScheduler.stage.waitingStages").getLong("value")//等待运行的stage
    //#####################StreamingMetrics###################################
    val lastCompletedBatch_processingDelay = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.lastCompletedBatch_processingDelay").getLong("value")// 最近批次执行的延迟时间
    val lastCompletedBatch_processingEndTime = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.lastCompletedBatch_processingEndTime").getLong("value")//最近批次执行结束时间（毫秒为单位）
    val lastCompletedBatch_processingStartTime = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.lastCompletedBatch_processingStartTime").getLong("value")//最近批次开始执行时间
    //执行时间
    val lastCompletedBatch_processingTime = (lastCompletedBatch_processingEndTime - lastCompletedBatch_processingStartTime)
    val lastReceivedBatch_records = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.lastReceivedBatch_records").getLong("value")//最近批次接收的数量
    val runningBatches = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.runningBatches").getLong("value")//正在运行的批次
    val totalCompletedBatches = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.totalCompletedBatches").getLong("value")//完成的数据量
    val totalProcessedRecords = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.totalProcessedRecords").getLong("value")//总处理条数
    val totalReceivedRecords = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.totalReceivedRecords").getLong("value")//总接收条数
    val unprocessedBatches = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.unprocessedBatches").getLong("value")//为处理的批次
    val waitingBatches = gauges.getJSONObject(applicationId + ".driver.query.StreamingMetrics.streaming.waitingBatches").getLong("value")//处于等待状态的批次


    //#####################messageProcessingTime###################################
//    //跟踪dagscheduler事件循环中处理消息的时间的计时器
//    val timers = jsonObj.getJSONObject("timers")
//    val messageProcessingTimePath = applicationId + ".driver.DAGScheduler.messageProcessingTime"
//    val messageProcessing = timers.getJSONObject(messageProcessingTimePath)
//    println(messageProcessing)

    //############################################################################

    //done:监控指标
    //done:实时处理的速度监控指标-monitorIndex需要写入Redis，由web端读取Redis并持久化到Mysql
    val endTime = processingEndTime
    val costTime = processTime
    val countPerMillis = sourceCount.toFloat / costTime.toFloat
    val monitorIndex = (endTime, applicationUniqueName, applicationId, sourceCount, costTime, countPerMillis)
    //产生不重复的key值
    val keyName = PropertiesUtil.getStringByKey("cluster.key.monitor.dataProcess", defaultRedisConfig) + System.currentTimeMillis.toString

    val fieldMap = scala.collection.mutable.Map(
      //TODO=================表格，做链路统计=================================
      "applicationId" -> monitorIndex._3.toString,
      "endTime" -> new DateTime(monitorIndex._1).toDateTime.toString("yyyy-MM-dd HH:mm:ss"),
      "applicationUniqueName" -> monitorIndex._2.toString,
      "sourceCount" -> monitorIndex._4.toString, //当前处理了多条数据
      "costTime" -> monitorIndex._5.toString,//花费的时间
      "countPerMillis" -> monitorIndex._6.toString,
      "serversCountMap" -> serversCountMap ,
      //TODO=================做饼图，用来对内存和磁盘的监控=================================
      "diskSpaceUsed_MB" -> diskSpaceUsed_MB ,//磁盘使用空间
      "maxMem_MB" -> maxMem_MB ,//最大内存
      "memUsed_MB" -> memUsed_MB ,//使用的内寸
      "remainingMem_MB" -> remainingMem_MB ,//闲置内存
      //TODO =================做表格，用来对job的监控=================================
      "activeJobs" -> activeJobs ,//当前正在运行的job
      "allJobs" -> allJobs ,//所有的job
      "failedStages" -> failedStages ,//是否出现错误的stage
      "runningStages" -> runningStages ,//正在运行的 stage
      "waitingStages" -> waitingStages ,//处于等待运行的stage
      "lastCompletedBatch_processingDelay" -> lastCompletedBatch_processingDelay ,//最近批次的延迟啥时间
      "lastCompletedBatch_processingTime" -> lastCompletedBatch_processingTime ,//正在处理的 批次的时间
      "lastReceivedBatch_records" -> lastReceivedBatch_records ,//最近接收到的数据量
      "runningBatches" -> runningBatches ,//正在运行的批次
      "totalCompletedBatches" -> totalCompletedBatches ,//所有完成批次
      "totalProcessedRecords" -> totalProcessedRecords ,//总处理数据条数
      "totalReceivedRecords" -> totalReceivedRecords ,//总接收数据
      "unprocessedBatches" -> unprocessedBatches ,//未处理的批次
      "waitingBatches" -> waitingBatches//处于等待的批次
    )
    val jedis = new Jedis("cdh1")
    jedis.select(4)
    jedis.set(keyName ,Json(DefaultFormats).write(fieldMap))
    jedis.expire(keyName , monitorDataExpTime)

//    try {
//      val jedis = JedisConnectionUtil.getJedisCluster
//      //监控Redis库
////      jedis.select(0);
//      jedis.set(keyName, Json(DefaultFormats).write(fieldMap))
//      //监控数据有效期7天
//      jedis.expire(keyName, monitorDataExpTime)
//      //JedisConnectionUtil.returnRes(jedis)
//    } catch {
//      case e: Exception =>
//        e.printStackTrace()
//        logger.error("连接Redis出现异常！")
//    }
  }

}
