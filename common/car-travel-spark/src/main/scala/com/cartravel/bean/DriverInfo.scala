package com.cartravel.bean

/**
  * Created by zxc
  */
case class DriverInfo(
                       table:String ,
                       id:String ,
                       operatorType:String , //操作类型
                       create_time : String , //注册时间
                       register_city : String , //注册城市编码
//                       city_name:String , //城市名称
                       driver_name : String  , //司机姓名
                       mobile : String , //司机电话
                       driver_type : String ,  //司机所属业务类型
                       cancel_count : String ,//当天有责取消订单的次数
                       driver_management_id :String //司官方ID
                     )

