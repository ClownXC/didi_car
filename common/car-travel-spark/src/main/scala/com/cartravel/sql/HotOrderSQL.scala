package com.cartravel.sql

/**
  * Created by zxc
  */
object HotOrderSQL {

  /**
    *  create_time:String , //订单创建时间
       open_lng:String ,  //乘客预计上车经度
       open_lat:String , //乘客预计上车维度
       begin_address_code:String , //区域编码(街道)
    * */

  //1:把经纬度转成h3编码
  lazy val orderHotTmp =(time:Int) =>
    s"""
      |select
      |open_lng ,
      |open_lat ,
      |create_time ,
      |begin_address_code ,
      |locationToH3(open_lng , open_lat , 7) as h3Code
      |from
      |order
      |where ${time} <= cast(date_format(create_time , 'yyyyMMdd') as int)
    """.stripMargin
  //2:基于h3编码取热力图

   lazy  val getHotArea =
     """
       |select tb2.h3Code ,
       |tb2.num as count ,
       |tb2.create_time ,
       |tb2.begin_address_code
       |from
       |(select * ,
       |row_number()  over(partition by h3Code order by rank desc) num
       |from
       |(select * ,
       |row_number() over(partition by h3Code order by create_time) rank
       |from
       |orderHotTmp) tb) tb2
       |where  tb2.num = 1
     """.stripMargin


}
