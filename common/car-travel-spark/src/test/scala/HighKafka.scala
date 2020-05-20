import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object HighKafka {


  def main(args: Array[String]): Unit = {


    val context: StreamingContext = StreamingContext.getActiveOrCreate("/ck", () => {
      val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("HighKafkaApi")

      val ssc: StreamingContext = new StreamingContext(conf, Seconds(3))
      ssc.checkpoint("/ck")
      ssc
    })



    KafkaUtils.createDirectStream()


  }




}
