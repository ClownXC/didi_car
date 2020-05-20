package com.cartravel.sql

/**
  * Created by zxc
  */
object RenterSQL {
  //平台注册用户总数()registerTotalCount
  lazy val rtc =
    """
      |select 1 as myid , count(1) as registerTotalCount from renter
    """.stripMargin

  //日新增用户数
  lazy val _dayRegister = (time:String) =>
    s"""
       |select
       |substr(last_login_city , 1 , 4) as last_login_city ,
       |count(1) as _day_new_user
       |from
       |(select last_login_city from renter
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${time}'
       |and
       |last_login_city != '') tb
       |group by substr(last_login_city , 1 , 4)
    """.stripMargin
  //各城市的日活跃用户数
  lazy val _dayActive =(time:String) =>
    s"""
       |select
       |substr(last_login_city,1,4) as last_login_city ,
       |count(distinct id) DAU
       |from
       |(select
       |last_login_city ,
       |id
       |from renter
       |where date_format(last_logon_time , 'yyyy-MM-dd') = '${time}'
       |and
       |last_login_city != '') tb
       |group by
       |substr(last_login_city,1,4)
    """.stripMargin

  //关联日活和日新增
  lazy val summary_register_active =
    """
      |select
      |cast(
      | concat(
      | if(_dayRegister.last_login_city is null , _dayActive.last_login_city , _dayRegister.last_login_city)
      |  , '00'
      | )
      | as string
      |) as rk ,
      |cast(
      | concat(
      | if(_dayRegister.last_login_city is null , _dayActive.last_login_city , _dayRegister.last_login_city)
      |  , '00'
      | )
      | as string
      |) as _city_code ,
      |cast(if(_dayRegister._day_new_user is null , 0 , _dayRegister._day_new_user) as string) as _day_new_user ,
      |cast(if(_dayActive.DAU is null , 0 , _dayActive.DAU) as  string) as DAU
      |from
      |_dayRegister  full outer join _dayActive
      |on
      |_dayRegister.last_login_city = _dayActive.last_login_city
    """.stripMargin


  //当日新增用户数
  lazy val dnuc =(time:String) =>
    s"""
       |select
       |1 as myid ,
       |count(1) as dayNewUserCount
       |from
       |(select id from renter
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${time}') tb
    """.stripMargin

  //本周新增用户数
  lazy val wnuc =(time:String) =>
    s"""
       |select 1 as myid , count(1) as weekNewUserCount
       |from
       |(select id from renter
       |where
       |weekofyear(create_time) = '${time}') tb
    """.stripMargin

  //当月新增用户数
  lazy val mnuc =(time:String) =>
    s"""
       |select 1 as myid , count(1) as monthNewUserCount
       |from
       |(select id from renter
       |where
       |date_format(create_time , 'yyyy-MM') = '${time}') tb
    """.stripMargin

  //汇总
  lazy val _palt_mwd =
    """
      |select
      |cast(tb.myid as string) as myid,
      |cast(tb.monthNewUserCount as string) as monthNewUserCount,
      |cast(tb.weekNewUserCount as string) as weekNewUserCount,
      |cast(dnuc.dayNewUserCount as string) as dayNewUserCount
      |from
      |(select
      |mnuc.myid ,
      |mnuc.monthNewUserCount ,
      |wnuc.weekNewUserCount
      |from
      |mnuc , wnuc
      |where
      |mnuc.myid = wnuc.myid) tb , dnuc
      |where
      |tb.myid = dnuc.myid
    """.stripMargin

  //计算次日留存
  lazy val dayStateRate = (today:String , lastDay:String) =>
    s"""
       |select
       |1 as myid ,
       |concat(cast(count(tb2.last_logon_time)*100/count(tb1.create_time) as string) , '%') as dayStateRate
       |from
       |(select
       |id ,
       |date_format(create_time , 'yyyy-MM-dd') create_time
       |from
       |renter
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${lastDay}') tb1
       |left outer join
       |(select
       |id ,
       |date_format(last_logon_time , 'yyyy-MM-dd') last_logon_time
       |from
       |renter
       |where
       |date_format(last_logon_time , 'yyyy-MM-dd') = '${today}') tb2
       |on
       |tb1.id = tb2.id
    """.stripMargin


  //计算周留存
  lazy val weekStateRate = (sevenDays:String , today:String) =>
    s"""
       |select
       |1 as myid ,
       |concat(cast(count(tb2.last_logon_time)*100/count(tb1.create_time) as string) , '%') as weekStateRate
       |from
       |(select
       |id ,
       |date_format(create_time , 'yyyy-MM-dd') create_time
       |from
       |renter
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${sevenDays}') tb1
       |left outer join
       |(select
       |id ,
       |date_format(last_logon_time , 'yyyy-MM-dd') last_logon_time
       |from
       |renter
       |where
       |date_format(last_logon_time , 'yyyy-MM-dd') = '${today}') tb2
       |on
       |tb1.id = tb2.id
    """.stripMargin


  //计算月留存
  lazy val monthStateRate = (day30:String , today:String) =>
    s"""
       |select
       |1 as myid ,
       |concat(cast(count(tb2.last_logon_time)*100/count(tb1.create_time) as string) , '%') as monthStateRate
       |from
       |(select
       |id ,
       |date_format(create_time , 'yyyy-MM-dd') create_time
       |from
       |renter
       |where
       |date_format(create_time , 'yyyy-MM-dd') = '${day30}') tb1
       |left outer join
       |(select
       |id ,
       |date_format(last_logon_time , 'yyyy-MM-dd') last_logon_time
       |from
       |renter
       |where
       |date_format(last_logon_time , 'yyyy-MM-dd') = '${today}') tb2
       |on
       |tb1.id = tb2.id
    """.stripMargin

  //汇总
  lazy  val stateRate =
    """
      |select
      |cast(NVL(tb1.myid,1) as string) as myid ,
      |cast(NVL(tb1.monthStateRate , '0.0%') as string) as monthStateRate,
      |cast(NVL(tb1.weekStateRate, '0.0%') as string) as weekStateRate,
      |cast(NVL(dayStateRate.dayStateRate, '0.0%') as string) as dayStateRate
      |from
      |(select
      |NVL(monthStateRate.myid , 1) as  myid,
      |NVL(monthStateRate.monthStateRate , '0.0%') as monthStateRate,
      |NVL(weekStateRate.weekStateRate , '0.0%') as weekStateRate
      |from
      |monthStateRate full outer join weekStateRate
      |on
      |monthStateRate.myid = weekStateRate.myid) tb1 full outer join dayStateRate
      |on
      |tb1.myid = dayStateRate.myid
    """.stripMargin



  //统计日活跃用户
  lazy val dailyActive =(today:String)=> s"""
                                            |select
                                            |1 tmpID ,
                                            |count(distinct id) DAU
                                            |from renter
                                            |where date_format(last_logon_time , 'yyyy-MM-dd') = '${today}'
                         """.stripMargin
  //date_format(NOW() , 'yyyy-MM-dd')


  //统计本周活跃用户
  lazy val weekActive =(week:String)=> s"""
                                          |select
                                          |1 tmpID ,
                                          |count(distinct id) WAU
                                          |from renter
                                          |where weekofyear(date_format(last_logon_time , 'yyyy-MM-dd')) = '${week}'
                        """.stripMargin// weekofyear(now())
  //cast(date_format(last_logon_time , 'yyyyMMdd') as int) >= cast(date_format(DATE_SUB(date_format(NOW() , 'yyyy-MM-dd'), 6) , 'yyyyMMdd') as int)
  //统计本月活跃用户
  lazy val monthActive =(month:String)=> s"""
                                            |select
                                            |1 tmpID ,
                                            |count(distinct id) MAU
                                            |from renter
                                            |where
                                            |date_format(last_logon_time , 'yyyy-MM') = '${month}'
                         """.stripMargin//date_format(NOW() , 'yyyy-MM')
  //cast(date_format(last_logon_time , 'yyyyMMdd') as int) >= cast(date_format(DATE_SUB(date_format(NOW() , 'yyyy-MM-dd'), 29) , 'yyyyMMdd') as int)

  //活跃用户表格汇总
  lazy val finalActive = """
                           |select
                           |cast(1 as string) as rk  ,
                           |cast(a.DAU as string) as DAU,
                           |cast(a.WAU as string) as WAU ,
                           |cast(monthActiveUsers.MAU as string) as MAU
                           |from
                           |(select
                           |dailyActiveUsers.tmpID ,
                           |dailyActiveUsers.DAU ,
                           |weekActiveUsers.WAU
                           |from dailyActiveUsers , weekActiveUsers
                           |where dailyActiveUsers.tmpID = weekActiveUsers.tmpID
                           |) a , monthActiveUsers
                           |where a.tmpID = monthActiveUsers.tmpID
                         """.stripMargin
}
