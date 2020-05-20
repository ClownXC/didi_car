package com.cartravel.sql

/**
  * Created by zxc
  */
object HotAreaOrderSQL {
  //计算当日热区订单数--精度7，大概半径为1200米
  lazy val orderHotTable_day =(time:String)=> s"""
                                 |select
                                 |open_lat ,
                                 |open_lng ,
                                 |create_time ,
                                 |begin_address_code ,
                                 |locationToH3(open_lat , open_lng , 7) as h3Code
                                 | from order
                                 | where date_format(create_time , 'yyyy-MM-dd') = '${time}'
                               """.stripMargin//date_format(NOW() , 'yyyy-MM-dd')
  lazy val _today_hot_area  = """
                                |select
                                |table2.begin_address_code ,
                                |h3ToCoordinate(table2.h3Code) coordinate,
                                |table2.h3Code ,
                                |1200 radius ,
                                |table2.rank as count
                                | from
                                |(select * ,
                                |row_number() over(partition by h3Code order by rank desc) num
                                |from
                                |(select * ,
                                |row_number() over(partition by h3Code order by create_time desc) rank
                                |from orderHotTable_day
                                |) table
                                |)table2
                                |where table2.num = 1
                              """.stripMargin









  //求昨日热力订单情况 2019-07-14
  lazy val yestdayOrderStatusTMP =(time:String)=> s"""
                                       |select
                                       |open_lat ,
                                       |open_lng ,
                                       |create_time ,
                                       |begin_address_code ,
                                       |locationToH3(open_lat , open_lng , 7) as h3Code
                                       | from order
                                       | where
                                       | date_format(create_time , 'yyyy-MM-dd') = '${time}'
                                     """.stripMargin//DATE_SUB(date_format(NOW() , 'yyyy-MM-dd'), 1)


 lazy val yestdayOrderStatus =  """
                                  |select
                                  |table2.begin_address_code ,
                                  |h3ToCoordinate(table2.h3Code) coordinate ,
                                  |table2.h3Code ,
                                  |1200 radius ,
                                  |table2.rank as count
                                  | from
                                  |(select * ,
                                  |row_number() over(partition by h3Code order by rank desc) num
                                  |from
                                  |(select * ,
                                  |row_number() over(partition by h3Code order by create_time desc) rank
                                  |from yestdayOrderStatusTMP
                                  |) table
                                  |)table2
                                  |where table2.num = 1
                                """.stripMargin





  lazy val newHotOrderTmp =
    """
    |select
    |if(t_begin_address_code is null,y_begin_address_code , t_begin_address_code) as city_code,
    |if(t_coordinate is null,y_coordinate , t_coordinate) as coordinate,
    |if(t_radius is null , y_radius , t_radius) as radius ,
    |if(t_count is null,0-if(y_count is null , 0 , y_count) , t_count-if(y_count is null,0,y_count)) as count
    |from
    |(
    |select
    |today_hot_area.begin_address_code as t_begin_address_code,
    |today_hot_area.coordinate as t_coordinate ,
    |today_hot_area.radius as t_radius ,
    |today_hot_area.count as t_count ,
    |yestdayOrderStatus.begin_address_code as y_begin_address_code ,
    |yestdayOrderStatus.coordinate as y_coordinate ,
    |yestdayOrderStatus.radius as y_radius ,
    |yestdayOrderStatus.count as y_count
    |from
    |today_hot_area full outer join yestdayOrderStatus
    |on
    |today_hot_area.h3Code = yestdayOrderStatus.h3Code
    |) tb
    """.stripMargin

  lazy val _hot_order ="""
                       |select
                       |cast(_today_hot_df.city_code as string) as rk ,
                       |cast(_today_hot_df.city_code as string) as _city_code ,
                       |cast(_today_hot_df.city_num as string) as _day_order_num ,
                       |cast(_new_hot_df.city_num as string) as _new_orders_num
                       |from
                       |_today_hot_df left join _new_hot_df
                       |on
                       |_today_hot_df.city_code = _new_hot_df.city_code
                     """.stripMargin




}
