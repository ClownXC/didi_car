package com.cartravel.programApp

import com.cartravel.hbase.conn.HbaseConnections
import com.cartravel.kafka.KafkaManager
import com.cartravel.loggings.Logging
import com.cartravel.spark.{MetricsMonitor, SparkEngine, StreamMonitor}
import com.cartravel.tools.{JsonParse, StructInterpreter, TimeUtils}
import com.cartravel.utils.GlobalConfigUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.util.Try
//mysql业务数据 --->实时同步到Hbase
/**
  * 1、为什么实时同步
  * 2、怎么同步
  *   2.1、mysql（binlog）--》maxwell ---》kafka
  *   2.2、怎么去消费kafka（低级api，自己维护偏移量）
  *   2.3、维护偏移量的时候，考虑offset矫正问题
  *   2.4、怎么把数据写到hbase
  *     2.4.1、负载均衡（rowkey的设计 、 预分区）
  *
  **/

/**
  * Created by zxc
  */
object StreamApp extends Logging {

  //5 node01:9092,node02:9092,node03:9092,node04:9092,node05:9092,node06:9092 test_car test_consumer_car node01:2181,node02:2181,node03:2181,node04:2181,node05:2181,node06:2181
  def main(args: Array[String]): Unit = {
    //1、从kafka拿数据
    if (args.length < 5) {
      System.err.println("Usage: KafkaDirectStream \n" +
        "<batch-duration-in-seconds> \n" +
        "<kafka-bootstrap-servers> \n" +
        "<kafka-topics> \n" +
        "<kafka-consumer-group-id> \n" +
        "<kafka-zookeeper-quorum> "
      )
      System.exit(1)
    }
    //TODO
    val startTime = TimeUtils.getNowDataMin

    val batchDuration = args(0)
    val bootstrapServers = args(1).toString
    val topicsSet = args(2).toString.split(",").toSet
    val consumerGroupID = args(3)
    val zkQuorum = args(4)
    val sparkConf = SparkEngine.getSparkConf()
    val session = SparkEngine.getSparkSession(sparkConf)
    val sc = session.sparkContext
    val ssc = new StreamingContext(sc, Seconds(batchDuration.toLong))

    val topics = topicsSet.toArray

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> bootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> consumerGroupID,
      "auto.offset.reset" -> GlobalConfigUtils.getProp("auto.offset.reset"),
      "enable.auto.commit" -> (false: java.lang.Boolean) //禁用自动提交Offset，否则可能没正常消费完就提交了，造成数据错误
    )

    //1:
    /**
      * val stream:InputDStream = KafkaUtils.createDirectStream[String, String](
      * streamingContext,
      * PreferConsistent,
      * Subscribe[String, String](topics, kafkaParams)
      * )
      **/

    val kafkaManager = new KafkaManager(zkQuorum, kafkaParams)
    val inputDStream: InputDStream[ConsumerRecord[String, String]] = kafkaManager.createDirectStream(ssc, topics)

    inputDStream.print()

    inputDStream.foreachRDD(rdd => {

      if (!rdd.isEmpty()) {
        val ranges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        ranges.foreach(line => {
          println(s" 当前读取的topic:${line.topic} , partition：${line.partition} , fromOffset:${line.fromOffset} , untilOffset: ${line.untilOffset}")
        })

        val doElse = Try {
          val data: RDD[String] = rdd.map(line => line.value())
          // TODO
          // MetricsMonitor.metrics(session.sparkContext.applicationId , "query" , data)

          data.foreachPartition(partition => {
            //构建连接
            val conn = HbaseConnections.getHbaseConn

            //写业务
            partition.foreach(d => {
              val parse: (String, Any) = JsonParse.parse(d)
              StructInterpreter.interpreter(parse._1, parse, conn)
            })

            //注销连接
            HbaseConnections.closeConn(conn)

          })

        }


        if (doElse.isSuccess) {
          ssc.addStreamingListener(new StreamMonitor(session, sparkConf, batchDuration.toInt, "streaming", rdd, kafkaManager))
        }

      }
    })
    ssc.start()
    ssc.awaitTermination()
  }


  /**
    * 1、消费的并行度  director    task-->partition
    * partition(1) 10M/S    -> sparkStreaming
    *
    * partition(10000)
    *
    *
    * 分区数 ： broker * 3/6/9
    * 6 --> partittion(18)
    *
    *
    * 2、序列化
    * java的序列化很沉重，会序列化好多无关的（时间长）
    * Kryo
    *
    *
    * 3、限流和背压/压背
    *
    *
    * 每秒钟从kafka每一个分区拉取的数据量是无限追 --》10000000000000000
    * 1条数据 大概1KB
    *
    * 限流：spark.streaming.kafka.maxRatePerPartition
    * 压背：流启动之后 --》checkpoint --》metastore
    *
    * 动态的调整消费的速度
    *
    * RateController
    * RateEstimite
    * RateLimit
    *
    *
    * 4、cpu空转时间
    * 流 -->task 如果task没拿到数据
    *spark.locality.wait	3s
    *
    *
    * 5、不要在代码中判断这个表是否存在
    *
    *
    * 6、推测执行
    * 流 -run()  task
    *
    * task失败 --->重试
    * 5  --> 8  3s  3*8 = 24s
    *
    * pedding--> task
    *
    *
    * JOIN 分组取topN
    *
    * task --> 50~60s
    * task -> 2小时
    * 数据倾斜  --》yarn : yarn logs -applicationiD YARNID
    *
    *
    * shuffle  fetchError
    **/

}
