package com.cartravel.programApp

import com.cartravel.loggings.Logging
import com.cartravel.spark.SparkEngine
import com.cartravel.transaction._
import com.cartravel.utils.GlobalConfigUtils
import org.apache.commons.cli.{CommandLine, GnuParser, HelpFormatter, Options}
import org.apache.spark.sql.DataFrame


/**
  * Created by zxc
  *
  * mysql(事务)-->binlog --> maxwell/canal --> kafka(json) --> sparkStreaming -> hbase
  * 自定义数据源去读写hbase
  *
  */
object ReadApp extends Logging {

  /**
    * //1、车辆和订单分布
    * //2、订单汇总（总、月、日均、里程）
    * //3、平台订单总数、注册总数、收入总数
    * //4、各城市当日新增和当日活跃用户数
    * //5、平台当日、本周、当月注册用户数
    * //6、留存率（次日、七日、30日）
    * //7、平台（日、周、月 活跃用户数）
    *
    **/
  val warnings =
    """"
      | opts.addOption("o" , false , "解析各城市的车辆和订单分布")
      |    opts.addOption("u" , false , "解析用户相关")
      |    opts.addOption("r" , false , "解析热力图")
      |    opts.addOption("d" , false , "解析司机相关")
      |    opts.addOption("p" , false , "解析平台相关")
      |    opts.addOption("z" , false , "解析热区订单相关")
      |    opts.addOption("h" , false , "help")
    """.stripMargin

  def main(args: Array[String]): Unit = {



    if (args.size <= 0) {
      error(warnings)
      //      sys.addShutdownHook()
    }
    parse(args)
    //--o
  }

  //接收指令， 执行具体的业务
  def parse(args: Array[String]): Unit = {
    //1: 解析命令
    val opts = new Options()
    //解析订单相关的 --o
    opts.addOption("o", false, "解析各城市的车辆和订单分布")
    opts.addOption("u", false, "解析用户相关")
    opts.addOption("d", false, "解析司机相关")
    opts.addOption("r", false, "解析热力图")
    opts.addOption("p", false, "解析平台相关")
    opts.addOption("z", false, "解析热区订单相关")
    opts.addOption("h", false, "help")

    //2:根据命令来解析业务
    //2.1 : 解析工具
    val parser = new GnuParser()
    var commonLine: CommandLine = null
    try {
      commonLine = parser.parse(opts, args)

      //解析帮助 --h
      if (commonLine.hasOption("h")) {
        //打印帮助信息
        val formatter = new HelpFormatter()
        formatter.printHelp("--help", opts)
      } else {
        //解析业务

        val sparkConf = SparkEngine.getSparkConf()
        val session = SparkEngine.getSparkSession(sparkConf)


        val order: DataFrame = session.read
          .format(GlobalConfigUtils.getProp("custom.hbase.path"))
          .options(Map(
            GlobalConfigUtils.getProp("sparksql_table_schema") -> GlobalConfigUtils.getProp("order.sparksql_table_schema"),
            GlobalConfigUtils.getProp("hbase_table_name") -> GlobalConfigUtils.getProp("syn.table.order_info"),
            GlobalConfigUtils.getProp("hbase_table_schema") -> GlobalConfigUtils.getProp("order.hbase_table_schema")
          )).load()

        val driver: DataFrame = session.read
          .format(GlobalConfigUtils.getProp("custom.hbase.path"))
          .options(Map(
            GlobalConfigUtils.getProp("sparksql_table_schema") -> GlobalConfigUtils.getProp("drivers.spark_sql_table_schema"),
            GlobalConfigUtils.getProp("hbase_table_name") -> GlobalConfigUtils.getProp("syn.table.driver_info"),
            GlobalConfigUtils.getProp("hbase_table_schema") -> GlobalConfigUtils.getProp("driver.hbase_table_schema")
          )).load()

        val renter: DataFrame = session.read
          .format(GlobalConfigUtils.getProp("custom.hbase.path"))
          .options(Map(
            GlobalConfigUtils.getProp("sparksql_table_schema") -> GlobalConfigUtils.getProp("registe.sparksql_table_schema"),
            GlobalConfigUtils.getProp("hbase_table_name") -> GlobalConfigUtils.getProp("syn.table.renter_info"),
            GlobalConfigUtils.getProp("hbase_table_schema") -> GlobalConfigUtils.getProp("registe.hbase_table_schema")
          )).load()

        //注册
        order.createOrReplaceTempView("order")
        driver.createOrReplaceTempView("driver")
        renter.createOrReplaceTempView("renter")

        //cache
        session.sqlContext.cacheTable("order")
        session.sqlContext.cacheTable("driver")
        session.sqlContext.cacheTable("renter")

        if (commonLine.hasOption("o")) {
          //解析各城市的车辆和订单分布
          OrderTransation.init(session)
        }
        if (commonLine.hasOption("u")) {
          //解析用户相关
          RenterTransation.init(session)
        }
        if (commonLine.hasOption("d")) {
          //解析司机相关
          DriverTransation.init(session)
        }
        if (commonLine.hasOption("r")) {
          //解析热力图
          //graph + dbscan  ---> uber h3
          HotOrderTransation.init(session)
        }

        if (commonLine.hasOption("z")) {
          //解析街道热区订单相关
          HotAreaOrder.init(session)
        }
      }
    } catch {
      case e: Exception =>
        error(s"处理出错:${e.printStackTrace()}")
    }
  }
}
