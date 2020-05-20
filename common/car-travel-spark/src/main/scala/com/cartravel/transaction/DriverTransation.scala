package com.cartravel.transaction

import com.cartravel.hbase.org.custom.spark.sql.hbase.DataFrameToHbase
import com.cartravel.sql.DriverSQL
import org.apache.spark.sql.SparkSession

/**
  * Created by zxc
  */
object DriverTransation {
  def init(sparkSession: SparkSession): Unit ={
    _per_order_rate(sparkSession)
  }


  //==============================日/周/月/季/年 - 各城市订单完成率(有效订单/总订单)==============================
  def _per_order_rate(sparkSession: SparkSession): Unit ={
    //当日 - 平台有效订单总数
    sparkSession.sql(DriverSQL._effective_order_day("2019-07-15")).createOrReplaceTempView("_effective_order_day")
    //本周 - 平台有效订单总数
    sparkSession.sql(DriverSQL._effective_order_week("29")).createOrReplaceTempView("_effective_order_week")
    //本月 - 平台有效订单总数
    sparkSession.sql(DriverSQL._effective_order_month("2019-07")).createOrReplaceTempView("_effective_order_month")
    //本季度 - 平台有效订单总数
    sparkSession.sql(DriverSQL._effective_order_quarter("3" , "2019")).createOrReplaceTempView("_effective_order_quarter")
    //本年 - 平台有效订单总数
    sparkSession.sql(DriverSQL._effective_order_year("2019")).createOrReplaceTempView("_effective_order_year")


    //当日 - 平台全部订单总数
    sparkSession.sql(DriverSQL._total_order_day("2019-07-15")).createOrReplaceTempView("_total_order_day")
    //本周 - 平台全部订单总数
    sparkSession.sql(DriverSQL._total_order_week("29")).createOrReplaceTempView("_total_order_week")
    //本月 - 平台全部订单总数
    sparkSession.sql(DriverSQL._total_order_month("2019-07")).createOrReplaceTempView("_total_order_month")
    //本季度 - 平台全部订单总数
    sparkSession.sql(DriverSQL._total_order_quarter("3" , "2019")).createOrReplaceTempView("_total_order_quarter")
    //本年 - 平台全部订单总数
    sparkSession.sql(DriverSQL._total_order_year("2019")).createOrReplaceTempView("_total_order_year")


    //当日 - 平台订单完成率
    sparkSession.sql(DriverSQL._plat_order_day_status).createOrReplaceTempView("_plat_order_day_status")
    //当周 - 平台订单完成率
    sparkSession.sql(DriverSQL._plat_order_week_status).createOrReplaceTempView("_plat_order_week_status")
    //当月 - 平台订单完成率
    sparkSession.sql(DriverSQL._plat_order_month_status).createOrReplaceTempView("_plat_order_month_status")
    //当季度 - 平台订单完成率
    sparkSession.sql(DriverSQL._plat_order_quarter_status).createOrReplaceTempView("_plat_order_quarter_status")
    //当年  - 平台订单完成率
    sparkSession.sql(DriverSQL._plat_order_year_status).createOrReplaceTempView("_plat_order_year_status")



    val _final_summary_pat_order = sparkSession.sql(DriverSQL._final_summary_pat_order).toDF("rk" , "city_name" , "_year_comple_rate" , "_quarter_comple_rate" , "_month_comple_rate" , "_week_comple_rate" , "_day_comple_rate")


    //日/周/月/季/年 - 平台订单完成率
    DataFrameToHbase.save(_final_summary_pat_order , "_final_summary_pat_order" , "rk" , 1, false)

    //========================司机订单完成率（司机）========================================
    //当日 - 司机有效订单总数
    sparkSession.sql(DriverSQL._effective_driver_order_day("2019-07-15")).createOrReplaceTempView("_effective_driver_order_day")
    //当周 - 司机有效订单总数
    sparkSession.sql(DriverSQL._effective_driver_order_week("29")).createOrReplaceTempView("_effective_driver_order_week")
    //当月 - 司机有效订单总数
    sparkSession.sql(DriverSQL._effective_driver_order_month("2019-07")).createOrReplaceTempView("_effective_driver_order_month")
    //当季度 - 司机有效订单总数
    sparkSession.sql(DriverSQL._effective_driver_order_quarter("3")).createOrReplaceTempView("_effective_driver_order_quarter")
    //当年 - 司机有效订单总数
    sparkSession.sql(DriverSQL._effective_driver_order_year("2019")).createOrReplaceTempView("_effective_driver_order_year")


    //当日 - 司机订单总数
    sparkSession.sql(DriverSQL._total_driver_order_day("2019-07-15")).createOrReplaceTempView("_total_driver_order_day")
    //本周 - 司机订单总数
    sparkSession.sql(DriverSQL._total_driver_order_week("29")).createOrReplaceTempView("_total_driver_order_week")
    //本月 - 司机订单总数
    sparkSession.sql(DriverSQL._total_driver_order_month("2019-07")).createOrReplaceTempView("_total_driver_order_month")
    //本季度 - 司机订单总数
    sparkSession.sql(DriverSQL._total_driver_order_quarter("3")).createOrReplaceTempView("_total_driver_order_quarter")
    //本年度 - 司机订单总数
    sparkSession.sql(DriverSQL._total_driver_order_year("2019")).createOrReplaceTempView("_total_driver_order_year")


    //当日 - 司机订单完成率
    sparkSession.sql(DriverSQL._driver_order_day_status).createOrReplaceTempView("_driver_order_day_status")
    //本周 - 司机订单完成率
    sparkSession.sql(DriverSQL._driver_order_week_status).createOrReplaceTempView("_driver_order_week_status")
    //本月 - 司机订单完成率
    sparkSession.sql(DriverSQL._driver_order_month_status).createOrReplaceTempView("_driver_order_month_status")
    //本季度 - 司机订单完成率
    sparkSession.sql(DriverSQL._driver_order_quarter_status).createOrReplaceTempView("_driver_order_quarter_status")
    //本年度 - 司机订单完成率
    sparkSession.sql(DriverSQL._driver_order_year_status).createOrReplaceTempView("_driver_order_year_status")


    //日/周/月/季/年 - 司机订单完成率
    val _driver_order_summary = sparkSession.sql(DriverSQL._driver_order_summary).toDF("rk" , "driver_id" , "driver_name" , "_year_comple_rate" , "_quarter_comple_rate" , "_month_comple_rate" , "_week_comple_rate" , "_day_comple_rate")
    DataFrameToHbase.save(_driver_order_summary , "_driver_order_summary" , "rk"  , 1 , false)
    //==========================新增司机注册数=========================================

    //当日 - 各城市的司机注册数
    sparkSession.sql(DriverSQL._register_driver_day("2019-07-15")).createOrReplaceTempView("_register_driver_day")
    //本周 - 各城市的司机注册数
    sparkSession.sql(DriverSQL._register_driver_week("29")).createOrReplaceTempView("_register_driver_week")
    //本月 - 各城市的司机注册数
    sparkSession.sql(DriverSQL._register_driver_month("2019-07")).createOrReplaceTempView("_register_driver_month")
    //本季度 - 各城市的司机注册数
    sparkSession.sql(DriverSQL._register_driver_quarter("3")).createOrReplaceTempView("_register_driver_quarter")
    //本年 - 各城市的司机注册数
    sparkSession.sql(DriverSQL._register_driver_year("2019")).createOrReplaceTempView("_register_driver_year")

    //日/周/月/季/年 - 各城市的司机注册数
    val _register_driver = sparkSession.sql(DriverSQL._register_driver).toDF("rk" , "register_city" , "_register_year_num" , "_register_quarter_num" , "_register_month_num" , "_register_week_num" , "_register_day_num")
    DataFrameToHbase.save(_register_driver , "_register_driver" , "rk" , 1 , false)
  }



}
