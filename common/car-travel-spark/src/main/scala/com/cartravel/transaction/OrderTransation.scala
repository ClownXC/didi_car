package com.cartravel.transaction

import com.cartravel.hbase.org.custom.spark.sql.hbase.DataFrameToHbase
import com.cartravel.sql.{OrderTransationSQL, RenterSQL}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by zxc
  */
object OrderTransation {

  def init(session: SparkSession): Unit = {
    orderMonitor(session) //  城市车辆和订单分布情况
    summary_order(session) //订单汇总（总、月、日均、里程）
    _order_renter_pay(session) //平台总订单、平台注册用户数、平台订单金额总数
  }

  //1、订单监控情况
  private def orderMonitor(session: SparkSession): Unit = {
    //各个城市的车辆分布 group
    //当日
    session.sql(OrderTransationSQL.dayVehicle("2019-07-15")).createOrReplaceTempView("dayVehicle")

    //当月
    session.sql(OrderTransationSQL.monthVehicle("2019-07")).createOrReplaceTempView("monthVehicle")

    //把当月和当日的车辆分布 关联起来
    session.sql(OrderTransationSQL.vehicleCount).createOrReplaceTempView("vehicleCount")


    //当日的订单分布
    session.sql(OrderTransationSQL.dayOrder("2019-07-15")).createOrReplaceTempView("dayOrder")
    //周 1 4
    session.sql(OrderTransationSQL.weekOrder("29")).createOrReplaceTempView("weekOrder")
    //月
    session.sql(OrderTransationSQL.monthOrder("2019-07")).createOrReplaceTempView("monthOrder")

    //订单分布汇总
    session.sql(OrderTransationSQL._totalOrder).createOrReplaceTempView("_totalOrder")

    //关联车辆分布和订单分布  full outer join
    val _final_summary_order_ = session.sql(OrderTransationSQL._final_summary_order_)
      .toDF("rk", "monthVehicleCount", "dayVehicleCount", "o_begin_address_code", "monthOrderCount", "weekOrderCount", "dayOrderCount")

    DataFrameToHbase.save(_final_summary_order_, "final_summary_order", "rk", 1, false)
  }

  //订单汇总（总、月、日均、里程）
  def summary_order(session: SparkSession): Unit = {
    session.sql(OrderTransationSQL._total_order).createOrReplaceTempView("_total_order")
    session.sql(OrderTransationSQL.month_totalOrder("2019-07")).createOrReplaceTempView("month_totalOrder")
    session.sql(OrderTransationSQL._totalCharge_mileage).createOrReplaceTempView("_totalCharge_mileage")
    session.sql(OrderTransationSQL.avgOrder).createOrReplaceTempView("avgOrder")

    //合并
    //平台总   join 当月 = t1
    //t1 join 日均 = t2
    //t2 join 公里
    val summary_plat: DataFrame = session.sql(OrderTransationSQL.summary_plat).toDF("rk", "totalOrderNum", "monthOrderNum", "avgOrderNum", "charge_mileage")
    DataFrameToHbase.save(summary_plat, "summary_plat", "rk", 1, false)

  }


  //########################## 平台总订单、平台注册用户数、平台订单金额总数#############################
  def _order_renter_pay(sparkSession: SparkSession): Unit = {
    //平台总订单数
    sparkSession.sql(OrderTransationSQL._total_order).createOrReplaceTempView("totalOrders")

    //平台注册用户数
    sparkSession.sql(RenterSQL.rtc).createOrReplaceTempView("rtc")

    //平台订单金额总数
    sparkSession.sql(OrderTransationSQL.pay_all).createOrReplaceTempView("pay_all")

    //汇总结果
    //1、平台总订单数 关联 平台注册用户数 = tb1
    //2、t1 关联 pay_all = 结果
    val summary_order_register_pay = sparkSession.sql(OrderTransationSQL.summary_order_register_pay)
      .toDF("myid", "totalCount", "registerTotalCount", "pay_all")

    DataFrameToHbase.save(summary_order_register_pay, "summary_order_register_pay", "myid", 1, false)
  }


}
