package com.cartravel.spark

import com.cartravel.bean._
import com.cartravel.utils.GlobalConfigUtils
import org.apache.spark.{SparkConf, SparkFiles}
import org.apache.spark.sql.SparkSession

/**
  * Created by zxc
  */
object SparkEngine {
  def getSparkConf(): SparkConf = {
    val sparkConf: SparkConf = new SparkConf()
      .set("spark.worker.timeout", GlobalConfigUtils.getProp("spark.worker.timeout"))
      .set("spark.cores.max", GlobalConfigUtils.getProp("spark.cores.max"))
      .set("spark.rpc.askTimeout", GlobalConfigUtils.getProp("spark.rpc.askTimeout"))
      .set("spark.task.maxFailures", GlobalConfigUtils.getProp("spark.task.maxFailures"))
      .set("spark.driver.allowMutilpleContext", GlobalConfigUtils.getProp("spark.driver.allowMutilpleContext"))
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.sql.adaptive.enabled", "true")
      .set("spark.streaming.kafka.maxRatePerPartition", GlobalConfigUtils.getProp("spark.streaming.kafka.maxRatePerPartition"))
      .set("spark.streaming.backpressure.enabled", GlobalConfigUtils.getProp("spark.streaming.backpressure.enabled"))
      .set("spark.streaming.backpressure.initialRate", GlobalConfigUtils.getProp("spark.streaming.backpressure.initialRate"))
      .set("spark.streaming.backpressure.pid.minRate", "10")
      .set("enableSendEmailOnTaskFail", "true")
      .set("spark.buffer.pageSize", "16m")
      //      .set("spark.streaming.concurrentJobs" , "5")
      .set("spark.driver.host", "localhost")
      .setMaster("local[*]")
      .setAppName("query")
    sparkConf.set("spark.speculation", "true")
    sparkConf.set("spark.speculation.interval", "300")
    sparkConf.set("spark.speculation.quantile", "0.9")
    sparkConf.set("spark.streaming.backpressure.initialRate", "500")
    sparkConf.set("spark.streaming.backpressure.enabled", "true")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "5000")
    sparkConf.registerKryoClasses(
      Array(
        classOf[OrderInfo],
        classOf[Opt_alliance_business],
        classOf[DriverInfo],
        classOf[RegisterUsers],
        classOf[Reservation]
      )
    )
    sparkConf
  }


  def getSparkSession(sparkConf: SparkConf): SparkSession = {
    val sparkSession: SparkSession = SparkSession.builder()
      .config(sparkConf)
      //调度模式
      .config("spark.scheduler.mode", "FAIR")
      .config("spark.executor.memoryOverhead", "512") //堆外内存
      .config("enableSendEmailOnTaskFail", "true")
      .config("spark.extraListeners", "com.cartravel.spark.SparkAppListener")//TODO 离线
      .enableHiveSupport() //开启支持hive
      .getOrCreate()
    sparkSession.sparkContext.setLocalProperty("spark.scheduler.pool", "n1")
    sparkSession
  }
}
