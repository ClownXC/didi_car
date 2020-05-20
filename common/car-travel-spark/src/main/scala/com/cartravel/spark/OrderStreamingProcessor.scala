//package com.cartravel.spark
//
//import com.cartravel.common.{Constants, TopicName}
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.apache.spark.streaming.kafka010._
//import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
//import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
//import org.apache.log4j.Level
//import org.apache.log4j.Logger
//import org.apache.spark.streaming.dstream.InputDStream
//
//import scala.collection.JavaConversions._
//
///**
//  * 订单统计和乘车人数统计
//  */
//class OrderStreamingProcessor {
//}
//
//case class Order(oderId: String)
//
//object OrderStreamingProcessor {
//  def main(args: Array[String]): Unit = {
//    import org.apache.spark._
//    import org.apache.spark.streaming._
//    //设置Spark程序在控制台中的日志打印级别
//    Logger.getLogger("org").setLevel(Level.WARN)
//    //local[*]使用本地模式运行，*表示内部会自动计算CPU核数，也可以直接指定运行线程数比如2，就是local[2]
//    //表示使用两个线程来模拟spark集群
//    val conf = new SparkConf().setAppName("OrderMonitor").setMaster("local[1]")
//
//    //初始化Spark Streaming环境
//    val streamingContext = new StreamingContext(conf, Seconds(1))
//
//    //设置检查点
//    streamingContext.checkpoint("/sparkapp/tmp")
//
//    //"auto.offset.reset" -> "earliest"
//    //
//    val kafkaParams = Map[String, Object](
////      "bootstrap.servers" -> "192.168.21.173:6667,192.168.21.174:6667,192.168.21.175:6667",
//      "bootstrap.servers" -> Constants.KAFKA_BOOTSTRAP_SERVERS,
//      "key.deserializer" -> classOf[StringDeserializer],//(k,v)
//      "value.deserializer" -> classOf[StringDeserializer],
//      "group.id" -> "test0002",
//      "auto.offset.reset" -> "latest",
//      "enable.auto.commit" -> (false: java.lang.Boolean)
//    )
//
//    val topics = Array(
//      TopicName.HAI_KOU_ORDER_TOPIC.getTopicName,
//      TopicName.CHENG_DU_ORDER_TOPIC.getTopicName,
//      TopicName.XI_AN_ORDER_TOPIC.getTopicName
//    )
//
//    topics.foreach(println(_))
//    println("topics:" + topics)
//
//    val stream1: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
//      streamingContext,
//      PreferConsistent,
//      Subscribe[String, String](topics, kafkaParams)
//    )
//    val stream = stream1
//
//    stream.count().print();
//
//    //实时统计订单总数
//    val ordersDs = stream.map(record => {
//      //主题名称
//      val topicName = record.topic()
//      val orderInfo = record.value()
//
//      //订单信息解析器
//      var orderParser: OrderParser = null;
//
//      //不同主题的订单进行不同的处理
//      topicName match {
//        case "hai_kou_order_topic" => {
//          orderParser = new HaiKouOrderParser();
//        }
//        case "cheng_du_order_topic" => {
//          orderParser = new ChengDuOrderParser()
//        }
//        case "xi_an_order_topic" => {
//          orderParser = new XiAnOrderParser()
//        }
//        case _ => {
//          orderParser = null;
//        }
//      }
//
//      println("orderParser:" + orderParser)
//      if (null != orderParser) {
//        val order = orderParser.parser(orderInfo)
//        println("parser order:" + order)
//        order
//      } else {
//        null
//      }
//    })
//
//
//    //订单计数,对于每个订单出现一次计数1
//    val orderCountRest = ordersDs.map(order => {
//      if (null == order) {
//        ("", 0)
//      } else if (order.getClass == classOf[ChengDuTravelOrder]) {
//        (Constants.CITY_CODE_CHENG_DU + "_" + order.createDay, 1)
//      } else if (order.getClass == classOf[XiAnTravelOrder]) {
//        (Constants.CITY_CODE_XI_AN + "_" + order.createDay, 1)
//      } else if (order.getClass == classOf[HaiKouTravelOrder]) {
//        (Constants.CITY_CODE_HAI_KOU + "_" + order.createDay, 1)
//      } else {
//        ("", 0)
//      }
//    }).updateStateByKey((currValues: Seq[Int], state: Option[Int]) => {
//      var count = currValues.sum + state.getOrElse(0);
//      Some(count)
//    })
//
//    /**
//      * 乘车人数统计
//      * 如果是成都或者西安的订单，数据中没有乘车人数字段，所有按照默认一单一人的方式进行统计
//      * 海口的订单数据中有乘车人数字段，就按照具体数进行统计
//      */
//    val passengerCountRest = ordersDs.map(order => {
//      if (null == order) {
//        ("", 0)
//      } else if (order.getClass == classOf[ChengDuTravelOrder]) {
//        (Constants.CITY_CODE_CHENG_DU + "_" + order.createDay, 1)
//      } else if (order.getClass == classOf[XiAnTravelOrder]) {
//        (Constants.CITY_CODE_XI_AN + "_" + order.createDay, 1)
//      } else if (order.getClass == classOf[HaiKouTravelOrder]) {
//        var passengerCount = order.asInstanceOf[HaiKouTravelOrder].passengerCount.toInt
//        //scala不支持类似java中的三目运算符，可以使用下面的操作方式
//        passengerCount = if(passengerCount>0) passengerCount else 1
//        (Constants.CITY_CODE_HAI_KOU + "_" + order.createDay,passengerCount)
//      } else {
//        ("", 0)
//      }
//    }).updateStateByKey((currValues: Seq[Int], state: Option[Int]) => {
//      var count = currValues.sum + state.getOrElse(0);
//      Some(count)
//    })
//
//
//    orderCountRest.foreachRDD(orderCountRDD=>{
//      import com.cartravel.util.JedisUtil
//      val jedisUtil = JedisUtil.getInstance()
//      val jedis = jedisUtil.getJedis
//      //从集群中收集统计结果，然后在driver
//      val orderCountRest = orderCountRDD.collect()
//      println("orderCountRest:"+orderCountRest)
//      orderCountRest.foreach(countrest=>{
//        println("countrest:"+countrest._1+","+countrest._2)
//        if(null!=countrest){
//          jedis.hset(Constants.ORDER_COUNT, countrest._1, countrest._2 + "")
//        }
//      })
//      jedisUtil.returnJedis(jedis)
//    })
//
//    passengerCountRest.foreachRDD(passengerCountRdd=>{
//      import com.cartravel.util.JedisUtil
//      val jedisUtil = JedisUtil.getInstance()
//      val jedis = jedisUtil.getJedis
//
//      val passengerCountRest = passengerCountRdd.collect()
//      passengerCountRest.foreach(countrest=>{
//        jedis.hset(Constants.PASSENGER_COUNT, countrest._1, countrest._2 + "")
//      })
//      jedisUtil.returnJedis(jedis)
//    })
//
//    //启动sparkstreaming程序
//    streamingContext.start();
//    streamingContext.awaitTermination();
//    streamingContext.stop()
//  }
//}
