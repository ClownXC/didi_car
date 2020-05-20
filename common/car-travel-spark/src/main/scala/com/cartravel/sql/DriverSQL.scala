package com.cartravel.sql

/**
  * Created by zxc
  */

object DriverSQL {
  //======================人均订单完成率（总）==================================
  //当日有效订单
  lazy val _effective_order_day =(day:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _effective_num_day
       |from order
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${day}'
       |and
       |cancel = 0
       |group by
       |city_name
    """.stripMargin//date_format(NOW() , 'yyyy-MM-dd')
  //weekofyear(create_time) = weekofyear(now()) 将本周改成7天
  //本周有效订单
  lazy val _effective_order_week =(weeks:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _effective_num_week
       |from order
       |where
       |weekofyear(create_time) = '${weeks}'
       |and
       |cancel = 0
       |group by
       |city_name
    """.stripMargin//weekofyear(now())
  //date_format(create_time , 'yyyy-MM') = date_format(NOW() , 'yyyy-MM')
  //本月有效订单
  lazy val _effective_order_month = (month:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _effective_num_month
       |from order
       |where
       |date_format(create_time , 'yyyy-MM') = '${month}'
       |and
       |cancel = 0
       |group by
       |city_name
    """.stripMargin//date_format(NOW() , 'yyyy-MM')

  //本季度有效订单
  lazy val _effective_order_quarter =(quarter:String , year:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _effective_num_quarter
       |from order
       |where
       |quarter(create_time) = '${quarter}'
       |and
       |date_format(create_time , 'yyyy') = '${year}'
       |and
       |cancel = 0
       |group by
       |city_name
    """.stripMargin//quarter(now()) \ date_format(NOW() , 'yyyy')

  //本年有效订单
  lazy val _effective_order_year =(year:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _effective_num_year
       |from order
       |where
       |date_format(create_time , 'yyyy') = '${year}'
       |and
       |cancel = 0
       |group by
       |city_name
    """.stripMargin//date_format(NOW() , 'yyyy')





  //当日全部订单
  lazy val _total_order_day =(today:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _total_num_day
       |from order
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${today}'
       |group by
       |city_name
    """.stripMargin//date_format(NOW() , 'yyyy-MM-dd')

  //weekofyear(create_time) = weekofyear(now())
  //本周全部订单
  lazy val _total_order_week =(week:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _total_num_week
       |from order
       |where
       |weekofyear(create_time) = '${week}'
       |group by
       |city_name
    """.stripMargin//weekofyear(now())
  //date_format(create_time , 'yyyy-MM') = date_format(NOW() , 'yyyy-MM')
  lazy val _total_order_month =(month:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _total_num_month
       |from order
       |where
       |date_format(create_time , 'yyyy-MM') = '${month}'
       |group by
       |city_name
    """.stripMargin
  lazy val _total_order_quarter =(quarter:String , year:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _total_num_quarter
       |from order
       |where
       |quarter(create_time) = '${quarter}'
       |and
       |date_format(create_time , 'yyyy') = '${year}'
       |group by
       |city_name
    """.stripMargin
  lazy val _total_order_year =(year:String)=>
    s"""
       |select
       |city_name ,
       |count(1) as _total_num_year
       |from order
       |where
       |date_format(create_time , 'yyyy') = '${year}'
       |group by
       |city_name
    """.stripMargin

  //当日的人均订单完成率
  lazy val _plat_order_day_status =
    """
      |select
      |city_name ,
      |case when _effective_num_day=0 then '0.0%' else CONCAT(cast(round(_effective_num_day*100/_total_num_day, 2) as string),'%') end as _day_comple_rate
      |from
      |(select
      |_effective_order_day.city_name ,
      |_effective_order_day._effective_num_day ,
      |_total_order_day._total_num_day
      |from
      |_effective_order_day left join _total_order_day
      |on
      |_effective_order_day.city_name = _total_order_day.city_name
      |) tb
    """.stripMargin

  //本周的人均订单完成率
  lazy val _plat_order_week_status =
    """
      |select
      |city_name ,
      |case when _effective_num_week=0 then '0.0%' else CONCAT(cast(round(_effective_num_week*100/_total_num_week, 2) as string),'%') end as _week_comple_rate
      |from
      |(select
      |_effective_order_week.city_name ,
      |_effective_order_week._effective_num_week ,
      |_total_order_week._total_num_week
      |from
      |_effective_order_week left join _total_order_week
      |on
      |_effective_order_week.city_name = _total_order_week.city_name
      |) tb
    """.stripMargin
  //本月的人均订单完成率
  lazy val _plat_order_month_status =
    """
      |select
      |city_name ,
      |case when _effective_num_month=0 then '0.0%' else CONCAT(cast(round(_effective_num_month*100/_total_num_month, 2) as string),'%') end as _month_comple_rate
      |from
      |(select
      |_effective_order_month.city_name ,
      |_effective_order_month._effective_num_month ,
      |_total_order_month._total_num_month
      |from
      |_effective_order_month left join _total_order_month
      |on
      |_effective_order_month.city_name = _total_order_month.city_name
      |) tb
    """.stripMargin
  //本季度的人均订单完成率
  lazy val _plat_order_quarter_status =
    """
      |select
      |city_name ,
      |case when _effective_num_quarter=0 then '0.0%' else CONCAT(cast(round(_effective_num_quarter*100/_total_num_quarter, 2) as string),'%') end as _quarter_comple_rate
      |from
      |(select
      |_effective_order_quarter.city_name ,
      |_effective_order_quarter._effective_num_quarter ,
      |_total_order_quarter._total_num_quarter
      |from
      |_effective_order_quarter left join _total_order_quarter
      |on
      |_effective_order_quarter.city_name = _total_order_quarter.city_name
      |) tb
    """.stripMargin
  //本年的人均订单完成率
  lazy val _plat_order_year_status =
    """
      |select
      |city_name ,
      |case when _effective_num_year=0 then '0.0%' else CONCAT(cast(round(_effective_num_year*100/_total_num_year, 2) as string),'%') end as _year_comple_rate
      |from
      |(select
      |_effective_order_year.city_name ,
      |_effective_order_year._effective_num_year ,
      |_total_order_year._total_num_year
      |from
      |_effective_order_year left join _total_order_year
      |on
      |_effective_order_year.city_name = _total_order_year.city_name
      |) tb
    """.stripMargin

  //日/周/月/季/年 - 平台订单完成率
  lazy val _final_summary_pat_order =
    """
      |select
      |cast(1 as string) as rk ,
      |cast(year_quarter_month_week_tb.city_name as  string) as city_name ,
      |cast(year_quarter_month_week_tb._year_comple_rate as string) as _year_comple_rate  ,
      |cast(if(year_quarter_month_week_tb._quarter_comple_rate is null , '0.0%' , year_quarter_month_week_tb._quarter_comple_rate) as string) as _quarter_comple_rate,
      |cast(if(year_quarter_month_week_tb._month_comple_rate is null , '0.0%' , year_quarter_month_week_tb._month_comple_rate) as string) as _month_comple_rate,
      |cast(if(year_quarter_month_week_tb._week_comple_rate is null , '0.0%' , year_quarter_month_week_tb._week_comple_rate) as string) as _week_comple_rate,
      |cast(if(_plat_order_day_status._day_comple_rate is null , '0.0%' , _plat_order_day_status._day_comple_rate) as string) as _day_comple_rate
      |from
      |(select
      |year_quarter_month_tb.city_name ,
      |year_quarter_month_tb._year_comple_rate ,
      |year_quarter_month_tb._quarter_comple_rate ,
      |year_quarter_month_tb._month_comple_rate ,
      |_plat_order_week_status._week_comple_rate
      |from
      |(select
      |year_quarter_tb.city_name ,
      |year_quarter_tb._year_comple_rate ,
      |year_quarter_tb._quarter_comple_rate ,
      |_plat_order_month_status._month_comple_rate
      |from
      |(select
      |_plat_order_year_status.city_name ,
      |_plat_order_year_status._year_comple_rate ,
      |_plat_order_quarter_status._quarter_comple_rate
      |from
      |_plat_order_year_status left join _plat_order_quarter_status
      |on
      |_plat_order_year_status.city_name = _plat_order_quarter_status.city_name
      |) year_quarter_tb left join _plat_order_month_status
      |on
      |year_quarter_tb.city_name = _plat_order_month_status.city_name
      |) year_quarter_month_tb left join _plat_order_week_status
      |on
      |year_quarter_month_tb.city_name = _plat_order_week_status.city_name
      |) year_quarter_month_week_tb left join _plat_order_day_status
      |on
      |year_quarter_month_week_tb.city_name = _plat_order_day_status.city_name
    """.stripMargin




  //====================司机订单完成率（总）========================================
  //当日 - 每个司机的订单完成率
  lazy val _effective_driver_order_day =(day:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name,
       |count(1) as _effective_num_day
       |from order
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${day}'
       |and
       |cancel = 0
       |group by
       |driver_id ,
       |driver_name
  """.stripMargin
  //7日 - 每个司机的订单完成率
  lazy val _effective_driver_order_week =(week:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _effective_num_week
       |from order
       |where
       |weekofyear(create_time) = '${week}'
       |and
       |cancel = 0
       |group by
       |driver_id ,
       |driver_name
    """.stripMargin
  //本月 - 每个司机的订单完成率
  lazy val _effective_driver_order_month =(month:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _effective_num_month
       |from order
       |where
       |date_format(create_time , 'yyyy-MM') = '${month}'
       |and
       |cancel = 0
       |group by
       |driver_id ,
       |driver_name
    """.stripMargin

  //本季度 - 每个司机的订单完成率
  lazy val _effective_driver_order_quarter =(quarter:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _effective_num_quarter
       |from order
       |where
       |quarter(create_time) = '${quarter}'
       |and
       |date_format(create_time , 'yyyy') = date_format(NOW() , 'yyyy')
       |and
       |cancel = 0
       |group by
       |driver_id ,
       |driver_name
    """.stripMargin
  //本年 - 每个司机的订单完成率
  lazy val _effective_driver_order_year =(year:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _effective_num_year
       |from order
       |where
       |date_format(create_time , 'yyyy') = '${year}'
       |and
       |cancel = 0
       |group by
       |driver_id ,
       |driver_name
    """.stripMargin

  //当日-司机-总订单
  lazy val _total_driver_order_day =(day:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _total_num_day
       |from order
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${day}'
       |group by
       |driver_id , driver_name
    """.stripMargin
  //本周-司机-总订单
  lazy val _total_driver_order_week =(week:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _total_num_week
       |from order
       |where
       |weekofyear(create_time) = '${week}'
       |group by
       |driver_id , driver_name
    """.stripMargin
  //本月-司机-总订单
  lazy val _total_driver_order_month =(month:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _total_num_month
       |from order
       |where
       |date_format(create_time , 'yyyy-MM') = '${month}'
       |group by
       |driver_id , driver_name
    """.stripMargin
  //本季度-司机-总订单
  lazy val _total_driver_order_quarter =(quarter:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _total_num_quarter
       |from order
       |where
       |quarter(create_time) = '${quarter}'
       |and
       |date_format(create_time , 'yyyy') = date_format(NOW() , 'yyyy')
       |group by
       |driver_id , driver_name
    """.stripMargin
  //本年度-司机-总订单
  lazy val _total_driver_order_year =(year:String)=>
    s"""
       |select
       |driver_id ,
       |driver_name ,
       |count(1) as _total_num_year
       |from order
       |where
       |date_format(create_time , 'yyyy') = '${year}'
       |group by
       |driver_id , driver_name
    """.stripMargin

  //当日的人均订单完成率
  lazy val _driver_order_day_status =
    """
      |select
      |driver_id ,
      |driver_name ,
      |case when _effective_num_day=0 then '0.0%' else CONCAT(cast(round(_effective_num_day*100/_total_num_day, 2) as string),'%') end as _day_comple_rate
      |from
      |(select
      |_effective_driver_order_day.driver_id ,
      |_effective_driver_order_day.driver_name ,
      |_effective_driver_order_day._effective_num_day ,
      |_total_driver_order_day._total_num_day
      |from
      |_effective_driver_order_day left join _total_driver_order_day
      |on
      |_effective_driver_order_day.driver_id = _total_driver_order_day.driver_id
      |) tb
    """.stripMargin

  //本周的人均订单完成率
  lazy val _driver_order_week_status =
    """
      |select
      |driver_id ,
      |driver_name ,
      |case when _effective_num_week=0 then '0.0%' else CONCAT(cast(round(_effective_num_week*100/_total_num_week, 2) as string),'%') end as _week_comple_rate
      |from
      |(select
      |_effective_driver_order_week.driver_id ,
      |_effective_driver_order_week.driver_name ,
      |_effective_driver_order_week._effective_num_week ,
      |_total_driver_order_week._total_num_week
      |from
      |_effective_driver_order_week left join _total_driver_order_week
      |on
      |_effective_driver_order_week.driver_id = _total_driver_order_week.driver_id
      |) tb
    """.stripMargin

  //本月的人均订单完成率
  lazy val _driver_order_month_status =
    """
      |select
      |driver_id ,
      |driver_name ,
      |case when _effective_num_month=0 then '0.0%' else CONCAT(cast(round(_effective_num_month*100/_total_num_month, 2) as string),'%') end as _month_comple_rate
      |from
      |(select
      |_effective_driver_order_month.driver_id ,
      |_effective_driver_order_month.driver_name ,
      |_effective_driver_order_month._effective_num_month ,
      |_total_driver_order_month._total_num_month
      |from
      |_effective_driver_order_month left join _total_driver_order_month
      |on
      |_effective_driver_order_month.driver_id = _total_driver_order_month.driver_id
      |) tb
    """.stripMargin

  //本季度的人均订单完成率
  lazy val _driver_order_quarter_status =
    """
      |select
      |driver_id ,
      |driver_name ,
      |case when _effective_num_quarter=0 then '0.0%' else CONCAT(cast(round(_effective_num_quarter*100/_total_num_quarter, 2) as string),'%') end as _quarter_comple_rate
      |from
      |(select
      |_effective_driver_order_quarter.driver_id ,
      |_effective_driver_order_quarter.driver_name ,
      |_effective_driver_order_quarter._effective_num_quarter ,
      |_total_driver_order_quarter._total_num_quarter
      |from
      |_effective_driver_order_quarter left join _total_driver_order_quarter
      |on
      |_effective_driver_order_quarter.driver_id = _total_driver_order_quarter.driver_id
      |) tb
    """.stripMargin
  //本年度的人均订单完成率
  lazy val _driver_order_year_status =
    """
      |select
      |driver_id ,
      |driver_name ,
      |case when _effective_num_year=0 then '0.0%' else CONCAT(cast(round(_effective_num_year*100/_total_num_year, 2) as string),'%') end as _year_comple_rate
      |from
      |(select
      |_effective_driver_order_year.driver_id ,
      |_effective_driver_order_year.driver_name ,
      |_effective_driver_order_year._effective_num_year ,
      |_total_driver_order_year._total_num_year
      |from
      |_effective_driver_order_year left join _total_driver_order_year
      |on
      |_effective_driver_order_year.driver_id = _total_driver_order_year.driver_id
      |) tb
    """.stripMargin

  //司机订单情况汇总
  lazy val _driver_order_summary =
    """
      |select
      |cast(year_quarter_month_week_tb.driver_id as string) as rk ,
      |cast(year_quarter_month_week_tb.driver_id as string) as driver_id,
      |cast(year_quarter_month_week_tb.driver_name as string) as driver_name,
      |cast(year_quarter_month_week_tb._year_comple_rate as string) as _year_comple_rate,
      |cast(if(year_quarter_month_week_tb._quarter_comple_rate is null , '0.0%' , year_quarter_month_week_tb._quarter_comple_rate) as string) as _quarter_comple_rate,
      |cast(if(year_quarter_month_week_tb._month_comple_rate is null , '0.0%' , year_quarter_month_week_tb._month_comple_rate) as string) as _month_comple_rate,
      |cast(if(year_quarter_month_week_tb._week_comple_rate is null , '0.0%' , year_quarter_month_week_tb._week_comple_rate) as string) as _week_comple_rate,
      |cast(if(_driver_order_day_status._day_comple_rate is null , '0.0%' , _driver_order_day_status._day_comple_rate) as string) as _day_comple_rate
      |from
      |(select
      |year_quarter_month_tb.driver_id ,
      |year_quarter_month_tb.driver_name ,
      |year_quarter_month_tb._year_comple_rate ,
      |year_quarter_month_tb._quarter_comple_rate ,
      |year_quarter_month_tb._month_comple_rate ,
      |_driver_order_week_status._week_comple_rate
      |from
      |(select
      |year_quarter_tb.driver_id ,
      |year_quarter_tb.driver_name ,
      |year_quarter_tb._year_comple_rate ,
      |year_quarter_tb._quarter_comple_rate ,
      |_driver_order_month_status._month_comple_rate
      |from
      |(select
      |_driver_order_year_status.driver_id ,
      |_driver_order_year_status.driver_name ,
      |_driver_order_year_status._year_comple_rate ,
      |_driver_order_quarter_status._quarter_comple_rate
      |from
      |_driver_order_year_status left join _driver_order_quarter_status
      |on
      |_driver_order_year_status.driver_id = _driver_order_quarter_status.driver_id
      |) year_quarter_tb left join _driver_order_month_status
      |on
      |year_quarter_tb.driver_id = _driver_order_month_status.driver_id
      |) year_quarter_month_tb left join _driver_order_week_status
      |on
      |year_quarter_month_tb.driver_id = _driver_order_week_status.driver_id
      |) year_quarter_month_week_tb left join _driver_order_day_status
      |on
      |year_quarter_month_week_tb.driver_id = _driver_order_day_status.driver_id
    """.stripMargin

  //====================新增司机注册数===================================
  //当日 - 各城市司机注册数
  lazy val _register_driver_day =(day:String)=>
    s"""
       |select
       |register_city ,
       |count(distinct id) _register_day_num
       |from
       |driver
       |where
       |date_format(create_time , 'yyyy-MM-dd') = ${day}
       |group by
       |register_city
  """.stripMargin



  //本周 - 各城市司机注册数
  lazy val _register_driver_week =(week:String)=>
    s"""
       |select
       |register_city ,
       |count(distinct id) _register_week_num
       |from
       |driver
       |where
       |weekofyear(create_time) = '${week}'
       |group by
       |register_city
    """.stripMargin

  //本月 - 各城市司机注册数
  lazy val _register_driver_month =(month:String)=>
    s"""
       |select
       |register_city ,
       |count(distinct id) _register_month_num
       |from
       |driver
       |where
       |date_format(create_time , 'yyyy-MM') = '${month}'
       |group by
       |register_city
    """.stripMargin

  //本季度 - 各城市司机注册数
  lazy val _register_driver_quarter =(quarter:String)=>
    s"""
       |select
       |register_city ,
       |count(distinct id) _register_quarter_num
       |from
       |driver
       |where
       |quarter(create_time) = '${quarter}'
       |and
       |date_format(create_time , 'yyyy') = date_format(NOW() , 'yyyy')
       |group by
       |register_city
    """.stripMargin

  //本年 - 各城市司机注册数
  lazy val _register_driver_year =(year:String)=>
    s"""
       |select
       |register_city ,
       |count(distinct id) _register_year_num
       |from
       |driver
       |where
       |date_format(create_time , 'yyyy') = '${year}'
       |group by
       |register_city
    """.stripMargin

  //汇总 - 各城市司机注册数
  lazy val _register_driver =
    """
      |select
      |cast(year_quarter_month_week_tb.register_city as string) as rk ,
      |cast(year_quarter_month_week_tb.register_city as string) as register_city ,
      |cast(if(year_quarter_month_week_tb._register_year_num is null,0 , year_quarter_month_week_tb._register_year_num) as string) as _register_year_num ,
      |cast(if(year_quarter_month_week_tb._register_quarter_num is null, 0 , year_quarter_month_week_tb._register_quarter_num) as string) as _register_quarter_num ,
      |cast(if(year_quarter_month_week_tb._register_month_num is null,0 , year_quarter_month_week_tb._register_month_num) as string) as _register_month_num ,
      |cast(if(year_quarter_month_week_tb._register_week_num is null,0,year_quarter_month_week_tb._register_week_num) as string) as _register_week_num ,
      |cast(if(_register_driver_day._register_day_num is null , 0 , _register_driver_day._register_day_num) as string) as _register_day_num
      |from
      |(select
      |year_quarter_month_tb.register_city ,
      |year_quarter_month_tb._register_year_num ,
      |year_quarter_month_tb._register_quarter_num ,
      |year_quarter_month_tb._register_month_num ,
      |_register_driver_week._register_week_num
      |from
      |(select
      |year_quarter_tb.register_city ,
      |year_quarter_tb._register_year_num ,
      |year_quarter_tb._register_quarter_num ,
      |_register_driver_month._register_month_num
      |from
      |(select
      |_register_driver_year.register_city ,
      |_register_driver_year._register_year_num ,
      |_register_driver_quarter._register_quarter_num
      |from
      |_register_driver_year left join _register_driver_quarter
      |on
      |_register_driver_year.register_city = _register_driver_quarter.register_city
      |) year_quarter_tb left join _register_driver_month
      |on
      |year_quarter_tb.register_city = _register_driver_month.register_city
      |) year_quarter_month_tb left join _register_driver_week
      |on
      |year_quarter_month_tb.register_city = _register_driver_week.register_city
      |) year_quarter_month_week_tb left join _register_driver_day
      |on
      |year_quarter_month_week_tb.register_city = _register_driver_day.register_city
    """.stripMargin




}
