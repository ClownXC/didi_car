package com.cartravel.transaction

import com.cartravel.hbase.org.custom.spark.sql.hbase.DataFrameToHbase
import com.cartravel.sql.HotAreaOrderSQL
import com.uber.h3core.H3Core
import com.uber.h3core.util.GeoCoord
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}


/**
  * Created by zxc
  */
object HotAreaOrder {
  val h3 = H3Core.newInstance

  def init(sparkSession: SparkSession): Unit = {
    _thrid_hotArea_order(sparkSession)
  }

  //========================当日热区订单、当日新增热区订单=================
  def _thrid_hotArea_order(sparkSession: SparkSession): Unit = {


    sparkSession.udf.register("locationToH3", locationToH3 _)
    sparkSession.udf.register("h3ToCoordinate", h3ToCoordinate _)
    //计算当日热区订单数--精度7，大概半径为1200米
    //1）：将经纬度转换成H3编码


    sparkSession.sql(HotAreaOrderSQL.orderHotTable_day("2019-07-15")).createOrReplaceTempView("orderHotTable_day")

    //2）：得到当日的热区订单 ，生成dataframe用来处理每个街道的热区
    val today_hot_area = sparkSession.sql(HotAreaOrderSQL._today_hot_area)
      .toDF("begin_address_code", "coordinate", "h3Code", "radius", "count")

    //注册临时表，用来做后面的当日新增热区
    today_hot_area.createOrReplaceTempView("today_hot_area")

    //根据当日热区情况，获取每个街道的热区
    val today_hot_area_rdd: RDD[Row] = today_hot_area.rdd
    //(街道编码 , (街道编码 ， 当日热区经纬度 ，当日热区半径，当日热区数量 ))



    val data: RDD[(String, Iterable[(String, String, Int, Int)])] = today_hot_area_rdd.map {
      line =>
        //当日下单的街道经纬度编码
        val begin_address_code = line.getAs[String]("begin_address_code")
        //当日热区经纬度坐标
        val coordinate = line.getAs[String]("coordinate")
        //当日热区半径
        val radius = line.getAs[Int]("radius")
        //当日热区数量
        val count = line.getAs[Int]("count")
        (begin_address_code, coordinate, radius, count)
    }.groupBy(line => line._1)


    //返回当日对每个街道热区情况计数(街道编码，数量，详情)(begin_address_code, coordinate, radius, count)
    val parseData: RDD[(String, Int, List[(String, String, Int, Int)])] = data.map {
      line =>
        var count = 0
        for (dat <- line._2) {
          count = count + dat._4
        }
        //(街道编码，数量，详情) (begin_address_code, coordinate, radius, count)
        (line._1, count, line._2.toList)
    }


    import sparkSession.sqlContext.implicits._
    //city_code | city_hot_num | [[coordinate , radius , count] ,[coordinate , radius , count] .... ]
    val _today_hot_df = parseData.toDF("city_code", "city_num", "today_hot_arr")


    //当提新增热区订单数
    //求昨日热力订单情况
    sparkSession.sql(HotAreaOrderSQL.yestdayOrderStatusTMP("2019-07-14")).createOrReplaceTempView("yestdayOrderStatusTMP")

    sparkSession.sql(HotAreaOrderSQL.yestdayOrderStatus).createOrReplaceTempView("yestdayOrderStatus")


    //根据昨日的热区订单，与今日热区订单做对比，得到日新增订单
    val yest_hot_new_status = sparkSession.sql(HotAreaOrderSQL.newHotOrderTmp)
      .toDF("city_code", "coordinate", "radius", "count")

    //得到当日热区和昨日热区对比后的每个街道的热区情况
    val yest_hot_new_status_rdd = yest_hot_new_status.rdd

    //依然对街道编码做分组，只不过这次是新增后的热区情况
    val _new_hot = yest_hot_new_status_rdd.map {
      line =>
        //昨日下单的街道经纬度编码
        val city_code = line.getAs[String]("city_code")
        //昨日热区经纬度坐标
        val coordinate = line.getAs[String]("coordinate")
        //昨日热区半径
        val radius = line.getAs[Int]("radius")
        //昨日热区数量
        val count = line.getAs[Int]("count")
        (city_code, coordinate, radius, count)
    }.groupBy(line => line._1)


    //对每个街道热区情况计数
    val _new_hot_parse = _new_hot.map {
      line =>
        var count = 0
        for (dat <- line._2) {
          count = count + dat._4
        }
        (line._1, count, line._2.toList)
    }

    import sparkSession.sqlContext.implicits._

    //新增热区的dataframe
    val _new_hot_df = _new_hot_parse.toDF("city_code", "city_num", "yest_hot_arr")

    //新增热区临时表
    _new_hot_df.createOrReplaceTempView("_new_hot_df")

    //当日热区临时表
    _today_hot_df.createOrReplaceTempView("_today_hot_df")

    //当日热区订单、当日新增热区订单
    val _hot_order = sparkSession.sql(HotAreaOrderSQL._hot_order)

    _hot_order.show()
    //    DataFrameToHbase.save(_hot_order , "_hot_order" , "rk" , 1 , false)
  }


  //经纬度转h3Code
  def locationToH3(lat: Double, lon: Double, res: Int): Long = {
    h3.geoToH3(lat, lon, res)
  }

  //根据h3编码，转成经纬度
  def h3ToCoordinate(line: String): String = {
    val geo: GeoCoord = h3.h3ToGeo(line.toLong)
    geo.lat + "," + geo.lng
  }
}
