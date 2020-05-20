package com.cartravel.sql

/**
  * Created by zxc
  */
object OrderTransationSQL {

  //当日各城市的车辆分布
  lazy val dayVehicle = (time: String) =>
    s"""
       |SELECT
       |substr(tb.begin_address_code , 1 ,4) AS begin_address_code ,
       |count(distinct vehicle_license) AS dayVehicleCount
       |FROM
       |     (SELECT
       |         begin_address_code ,
       |         vehicle_license
       |      FROM
       |         order
       |      WHERE
       |       date_format(create_time , 'yyyy-MM-dd')  = '${time}'
       |     ) tb
       |GROUP BY
       |    SUBSTR(tb.begin_address_code , 1 ,4)
    """.stripMargin


  //当日各城市的车辆分布yyyy-MM
  lazy val monthVehicle = (time: String) =>
    s"""
       |select
       |substr(tb.begin_address_code , 1 ,4) as begin_address_code ,
       |count(distinct vehicle_license) as monthVehicleCount
       |from
       |  (select
       |      begin_address_code ,
       |      vehicle_license
       |   from
       |      order
       |   where
       |      date_format(create_time , 'yyyy-MM')  = '${time}') tb
       |group by
       |   substr(tb.begin_address_code , 1 ,4)
    """.stripMargin

  //汇总当日车辆和当月车辆
  lazy val vehicleCount =
    """
      |select
      |    monthVehicle.begin_address_code ,
      |    NVL(monthVehicle.monthVehicleCount ,0) as monthVehicleCount,
      |    NVL(dayVehicle.dayVehicleCount , 0) as dayVehicleCount
      |from
      |    monthVehicle left join dayVehicle
      |on
      |    monthVehicle.begin_address_code = dayVehicle.begin_address_code
    """.stripMargin

  //当日订单分布
  lazy val dayOrder = (time: String) =>
    s"""
       |select
       |    substr(tb.begin_address_code , 1 , 4)  as begin_address_code  ,
       |    count(1) as dayOrderCount
       |from
       |    (select
       |        begin_address_code from order
       |     where
       |        date_format(create_time , 'yyyy-MM-dd') = '${time}') tb
       |group by
       |     substr(tb.begin_address_code , 1 , 4)
    """.stripMargin

  //本周订单分布
  lazy val weekOrder = (time: String) =>
    s"""
       |select
       |    substr(tb.begin_address_code , 1 , 4)  as begin_address_code  ,
       |    count(1) as weekOrderCount
       |from
       |    (select begin_address_code from order
       |where
       |    weekofyear(create_time) = '${time}') tb
       |group by
       |    substr(tb.begin_address_code , 1 , 4)
    """.stripMargin

  //当日订单分布
  lazy val monthOrder = (time: String) =>
    s"""
       |select
       |    substr(tb.begin_address_code , 1 , 4)  as begin_address_code  ,
       |    count(1) as monthOrderCount
       |from
       |    (select
       |        begin_address_code
       |     from
       |        order
       |     where
       |        date_format(create_time , 'yyyy-MM') = '${time}') tb
       |group by
       |    substr(tb.begin_address_code , 1 , 4)
    """.stripMargin


  //汇总日、周、月订单分布
  //1、月 join 周 = tb1
  //2、t1 join 日 = 结果
  lazy val _totalOrder =
  """
    |select
    |    tb1.begin_address_code ,
    |    tb1.monthOrderCount ,
    |    tb1.weekOrderCount ,
    |    dayOrder.dayOrderCount
    |from
    |   (select
    |       monthOrder.begin_address_code ,
    |       monthOrder.monthOrderCount ,
    |       weekOrder.weekOrderCount
    |    from
    |       monthOrder left join weekOrder
    |    on
    |       monthOrder.begin_address_code  =  weekOrder.begin_address_code)  tb1
    |left join
    |     dayOrder
    |on
    |    tb1.begin_address_code = dayOrder.begin_address_code
  """.stripMargin

  //车辆分布和订单分布的汇总结果
  lazy val _final_summary_order_ =
    """
      |select
      |cast(
      | concat(
      |   if(v_begin_address_code is null ,o_begin_address_code , v_begin_address_code)
      | , '00')
      |as string) as rk,
      |cast(if(monthVehicleCount is null , 0 ,monthVehicleCount) as string) as monthVehicleCount ,
      |cast(if(dayVehicleCount is null , 0 , dayVehicleCount) as string) as dayVehicleCount ,
      |cast(
      | concat(
      |   if(o_begin_address_code is null ,v_begin_address_code , o_begin_address_code)
      | , '00')
      |as string) as o_begin_address_code,
      |cast(if(monthOrderCount is null , 0 , monthOrderCount) as string) as monthOrderCount ,
      |cast(if(weekOrderCount is null, 0 , weekOrderCount) as string) as weekOrderCount ,
      |cast(if(dayOrderCount is null ,0, dayOrderCount) as string) as dayOrderCount
      |from
      |(select
      |vehicleCount.begin_address_code as v_begin_address_code,
      |vehicleCount.monthVehicleCount ,
      |vehicleCount.dayVehicleCount ,
      |_totalOrder.begin_address_code as o_begin_address_code ,
      |_totalOrder.monthOrderCount ,
      |_totalOrder.weekOrderCount ,
      |_totalOrder.dayOrderCount
      |from
      |vehicleCount full outer join _totalOrder
      |on
      |vehicleCount.begin_address_code = _totalOrder.begin_address_code
      |) tb
    """.stripMargin


  //################平台：总、月、公里数 、日均 ########################
  //总
  lazy val _total_order =
  """
    |select
    |1 myid ,
    |count(1) as totalOrderNum
    |from
    |order
  """.stripMargin

  //月
  lazy val month_totalOrder = (time: String) =>
    s"""
       |select
       |1 myid ,
       |count(1) as monthOrderNum
       |from
       |order
       |where
       |date_format(create_time , 'yyyy-MM') = '${time}'
    """.stripMargin //weekofyear(1)

  //公里数
  lazy val _totalCharge_mileage =
    """
      |select
      |1 as myid ,
      |sum(charge_mileage)  as charge_mileage
      |from
      |order
    """.stripMargin

  //日均
  /**
    * 1、时间间隔（订单  max - min）datediff
    * 2、总订单数
    * 3、总/时间间隔 = 日均订单
    **/


  //日均订单
  lazy val avgOrder =
  """
    |select
    |1 as myid ,
    |round((tb.totalOrder /tb.num) , 2) as avgOrderNum
    |from
    |(select
    |     count(1) as totalOrder ,
    |    (select
    |         DATEDIFF(
    |            max(date_format(create_time , 'yyyy-MM-dd')) ,
    |            min(date_format(create_time , 'yyyy-MM-dd'))
    |     ) as dayNum
    |from
    |order ) num
    |from order) tb
  """.stripMargin

  //汇总结果
  //平台总   join 当月 = t1
  //t1 join 日均 = t2
  //t2 join 公里
  lazy val summary_plat =
  """
    |select
    |cast(t2.myid as string) as rk,
    |    cast(t2.totalOrderNum as string) as totalOrderNum,
    |    cast(t2.monthOrderNum as  string) as monthOrderNum,
    |    cast(t2.avgOrderNum as string) as avgOrderNum ,
    |    cast(_totalCharge_mileage.charge_mileage as string)  as charge_mileage
    |from
    |    (select
    |         t1.myid ,
    |         t1.totalOrderNum ,
    |         t1.monthOrderNum ,
    |         avgOrder.avgOrderNum
    |     from
    |         (select
    |              _total_order.myid ,
    |              _total_order.totalOrderNum ,
    |              month_totalOrder.monthOrderNum
    |          from
    |              _total_order
    |          left join month_totalOrder on _total_order.myid = month_totalOrder.myid) t1
    |          left join avgOrder on t1.myid = avgOrder.myid) t2
    |          left join _totalCharge_mileage on t2.myid = _totalCharge_mileage.myid
  """.stripMargin


  //######################平台汇总################################
  //收入总数
  lazy val pay_all =
  """
    |select 1 as myid ,  sum(pay_all) pay_all from order
  """.stripMargin


  //汇总
  lazy val summary_order_register_pay =
    """
      |select
      |    tb1.myid as myid,
      |    cast(tb1.totalCount as string) as totalCount ,
      |    cast(tb1.registerTotalCount as string) as registerTotalCount,
      |    cast(pay_all.pay_all as string) as pay_all
      |from
      |   (select
      |        totalOrders.myid ,
      |        totalOrders.totalOrderNum as totalCount ,
      |        rtc.registerTotalCount
      |    from
      |        totalOrders , rtc
      |    where
      |        totalOrders.myid = rtc.myid) tb1 , pay_all
      |where
      |    tb1.myid = pay_all.myid
    """.stripMargin


}
