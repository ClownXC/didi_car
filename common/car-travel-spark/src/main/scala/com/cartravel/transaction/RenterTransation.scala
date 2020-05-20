package com.cartravel.transaction

import com.cartravel.hbase.org.custom.spark.sql.hbase.DataFrameToHbase
import com.cartravel.sql.{RenterSQL}
import org.apache.spark.sql.SparkSession

/**
  * Created by zxc
  */
object RenterTransation {

  def init(session: SparkSession): Unit = {
    renter_register_active(session) //各城市当日新增和当日活跃用户数 renter(last_login_time = data_format(NOW() , 'YYYY-MM'))
    plat_register_status(session) //平台当日、本周(weekofyear(1))、当月注册用户数
    stateRate(session) //留存率（次日(昨天注册的用户集合A ， 今天登录的人B)、七日、30日）
    active_user(session) //平台（日、周、月 活跃用户数）
  }


  //###########各城市当日新增用户数 、 当日活跃用户数################
  def renter_register_active(session: SparkSession): Unit = {

    //日新增用户数
    session.sql(RenterSQL._dayRegister("2019-07-15")).createOrReplaceTempView("_dayRegister")

    //各城市的日活跃用户数
    //1、求活跃用户
    session.sql(
      """
        |SELECT
        |   last_login_city ,
        |   id
        |FROM renter
        |   WHERE date_format(last_login_time , 'yyyy-MM-dd') = date_format(Now() , 'yyyy-MM-dd')
        |AND
        |   last_login_city != ''
      """.stripMargin)
    //2、求各城市的活跃用户数
    session.sql(
      """
        |SELECT
        |    SUBSTR(last_login_city,1,4) AS last_login_city ,
        |    COUNT(distinct id) DAU
        |FROM
        |    (SELECT
        |        last_login_city ,
        |        id
        |     FROM renter
        |     WHERE date_format(last_login_time , 'yyyy-MM-dd') = date_format(Now() , 'yyyy-MM-dd') AND last_login_city != '')
        |     tb
        |GROUP BY
        |substr(last_login_city,1,4)
      """.stripMargin)

    session.sql(RenterSQL._dayActive("2019-07-15")).createOrReplaceTempView("_dayActive")

    //关联日新增和日活

    /**
      * 直接关联：但是我们要考虑关联后出现null的现象
      **/
    session.sql(
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
      """.stripMargin)

    val summary_register_active = session.sql(RenterSQL.summary_register_active)
      .toDF("rk", "_city_code", "_day_new_user", "DAU")

    //结果落地
    DataFrameToHbase.save(summary_register_active, "summary_register_active", "rk", 1, false)
  }

  //###################日、周、月新增用户数###############################
  def plat_register_status(session: SparkSession): Unit = {
    //得到当日的id
    session.sql(
      """
        |select 1 as myid , count(1) as dayNewUserCount
        |from
        |(select id from renter
        |where
        |date_format(create_time , 'yyyy-MM-dd') = date_format(NOW() , 'yyyy-MM-dd')) tb
      """.stripMargin)


    //当日新增注册用户数
    session.sql(RenterSQL.dnuc("2019-07-15")).createOrReplaceTempView("dnuc")
    session.sql(RenterSQL.wnuc("29")).createOrReplaceTempView("wnuc")
    session.sql(RenterSQL.mnuc("2019-07")).createOrReplaceTempView("mnuc")

    //汇总1：月 和 周

    session.sql(
      """
        |select
        |tb.myid ,
        |tb.monthNewUserCount ,
        |tb.weekNewUserCount ,
        |dnuc.dayNewUserCount
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
      """.stripMargin)

    val _palt_mwd = session.sql(RenterSQL._palt_mwd)
      .toDF("myid", "monthNewUserCount", "weekNewUserCount", "dayNewUserCount")
    DataFrameToHbase.save(_palt_mwd, "_palt_mwd", "myid", 1, false)
  }

  //留存率
  def stateRate(session: SparkSession): Unit = {
    //第一步：求昨天注册的（id ， create_time）
    session.sql(
      """
        |select
        |id ,
        |date_format(create_time , 'yyyy-MM-dd') create_time
        |from
        |renter
        |where
        |date_format(create_time , 'yyyy-MM-dd') = date_format(date_sub(NOW() , 1), 'yyyy-MM-dd')
      """.stripMargin)


    //第二步：求今天登录的(id , last_login_time)
    session.sql(
      """
        |select
        |id ,
        |date_format(last_logon_time , 'yyyy-MM-dd') last_logon_time
        |from
        |renter
        |where
        |date_format(last_logon_time , 'yyyy-MM-dd') = date_format(date_sub(NOW() , 0), 'yyyy-MM-dd')
      """.stripMargin)

    //第三步：合并（left outer join）
    //1): 先让 昨天注册的表  join   今天登录的表
    session.sql(
      """
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
        |date_format(create_time , 'yyyy-MM-dd') = date_format(date_sub(NOW() , 1), 'yyyy-MM-dd')) tb1
        |left outer join
        |(select
        |id ,
        |date_format(last_logon_time , 'yyyy-MM-dd') last_logon_time
        |from
        |renter
        |where
        |date_format(last_logon_time , 'yyyy-MM-dd') = date_format(date_sub(NOW() , 0), 'yyyy-MM-dd')) tb2
        |on
        |tb1.id = tb2.id
      """.stripMargin)

    session.sql(RenterSQL.dayStateRate("2019-07-15", "2019-07-14")).createOrReplaceTempView("dayStateRate")
    session.sql(RenterSQL.weekStateRate("2019-07-15", "2019-07-07")).createOrReplaceTempView("weekStateRate")
    session.sql(RenterSQL.monthStateRate("2019-07-30", "2019-07-01")).createOrReplaceTempView("monthStateRate")

    //汇总结果
    //1:月  join  周
    session.sql(
      """
        |select
        |NVL(tb1.myid,1) as myid ,
        |NVL(tb1.monthStateRate , '0.0%') as monthStateRate,
        |NVL(tb1.weekStateRate, '0.0%') as weekStateRate,
        |NVL(dayStateRate.dayStateRate, '0.0%') as dayStateRate
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
      """.stripMargin)

    val stateRate = session.sql(RenterSQL.stateRate)
      .toDF("myid", "monthStateRate", "weekStateRate", "dayStateRate")

    //落地
    DataFrameToHbase.save(stateRate, "stateRate", "myid", 1, false)
  }


  def active_user(session: SparkSession): Unit = {

    //平台统计日活跃用户
    session.sql(RenterSQL.dailyActive("2019-07-15")).createOrReplaceTempView("dailyActiveUsers")
    //统计本周活跃用户
    session.sql(RenterSQL.weekActive("29")).createOrReplaceTempView("weekActiveUsers")
    //统计30日活跃用户
    session.sql(RenterSQL.monthActive("2019-07")).createOrReplaceTempView("monthActiveUsers")
    //活跃用户汇总
    val active = session.sql(RenterSQL.finalActive).toDF("rk", "DAU", "WAU", "MAU")

    DataFrameToHbase.save(active, "active", "rk", 1, false)
  }


}
