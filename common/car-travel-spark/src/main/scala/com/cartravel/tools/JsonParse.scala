package com.cartravel.tools

import com.alibaba.fastjson.JSON
import com.cartravel.bean.{DriverInfo, Opt_alliance_business, OrderInfo, RegisterUsers}
import com.cartravel.utils.GlobalConfigUtils


/**
  * Created by zxc
  */
object JsonParse {
  val order_info_table = GlobalConfigUtils.getProp("syn.table.order_info")
  val renter_info_table = GlobalConfigUtils.getProp("syn.table.renter_info")
  val driver_info_table = GlobalConfigUtils.getProp("syn.table.driver_info")
  val opt_alliance_business = GlobalConfigUtils.getProp("syn.table.opt_alliance_business")
  def parse(str:String):(String  , Any) =  {
    val json=JSON.parseObject(str)
    val table = if(json.getString("table") != null) json.getString("table") else ""
    val type_ = if(json.getString("type") != null) json.getString("type") else "insert"
    val data = json.getJSONObject("data")
    //此处判别表
    val bean:(String , Any) = table match{
      //以order_info为开头
      case table if(table.startsWith(order_info_table)) =>
        val id = data.getString("id_")
        val create_time = if(data.get("create_time") != null) data.get("create_time").toString else ""// 订单创建时间
        val pay_all = if(data.get("pay_all") != null) data.get("pay_all").toString else ""// 支付金额
        //pay_normal
        val pay_normal = if(data.get("pay_normal") != null) data.get("pay_normal").toString else ""// 支付金额
        val open_lng = if(data.get("open_lng") != null) data.get("open_lng").toString else ""//乘客预计上车经度
        val open_lat = if(data.get("open_lat") != null) data.get("open_lat").toString else ""//乘客预计上车维度
        val begin_address_code = if(data.get("begin_address_code") != null) data.get("begin_address_code").toString else ""//打车点的城市编码
        val charge_mileage = if(data.get("charge_mileage") != null) data.get("charge_mileage").toString else "" //订单里程
        val city_name = if(data.get("city_name") != null) data.get("city_name").toString else ""//城市名称
        val vehicle_license = if(data.get("vehicle_license") !=  null) data.get("vehicle_license").toString else ""//车牌号
        val driver_id = if(data.get("driver_id") != null) data.get("driver_id").toString else ""//司机ID
        val driver_name = if(data.get("driver_name") != null) data.get("driver_name").toString else ""//司机名称
        val cancel = if(data.get("cancel") != null) data.get("cancel").toString else "" //是否取消
        val state = if(data.get("state") != null) data.get("state").toString else "" //是否取消
        val order_type = if(data.get("order_type") != null) data.get("order_type").toString else "" //是否取消
        //work_flow
        val work_flow = if(data.get("work_flow") != null) data.get("work_flow").toString else "" //是否取消
        //close_gps_time
        val close_gps_time = if(data.get("close_gps_time") != null) data.get("close_gps_time").toString else "" //是否取消
        /**
          * cancel_time :String , //取消订单时间
            pay_time :String //订单支付时间
          * */
        val cancel_time = if(data.get("cancel_time") != null) data.get("cancel_time").toString else "" //取消订单时间
        val pay_time = if(data.get("pay_time") != null) data.get("pay_time").toString else "" //订单支付时间
        (table , OrderInfo(
          table ,
          id ,
          type_ ,
          pay_all ,
          pay_normal,
          create_time ,
          open_lng ,
          open_lat ,
          begin_address_code ,
          charge_mileage ,
          city_name ,
          vehicle_license ,
          driver_id ,
          driver_name,
          cancel ,
          state ,
          order_type ,
          work_flow ,
          close_gps_time ,
          cancel_time ,
          pay_time
        ))
      case table if(table.equals(renter_info_table) || table == renter_info_table)=>
        val id = data.getString("id_")
        val create_time = if(data.get("create_time") != null) data.get("create_time").toString else ""// 用户注册侧时间
        val last_login_city = if(data.get("last_login_city") != null) data.get("last_login_city").toString else ""//最后登录城市
        val last_logon_time = if(data.get("last_logon_time") != null) data.get("last_logon_time").toString else ""//最后登录时间
        val city_name = if(data.get("city_name") != null) data.get("city_name").toString else ""//注册城市名称

        (table , RegisterUsers(
          table,
          id ,
          type_ ,
          create_time ,
          last_login_city ,
          last_logon_time ,
          city_name
        ))

      case table if(table.equals(driver_info_table) || table == driver_info_table)=>
        val id = data.getString("id_")
        val create_time = if(data.get("create_time") != null) data.get("create_time").toString else ""// 用户注册侧时间
        val register_city = if(data.get("register_city") != null) data.get("register_city").toString else ""//最后登录城市
        //city_name
//        val city_name = if(data.get("city_name") != null) data.get("city_name").toString else ""//最后登录城市
        val driver_name = if(data.get("driver_name") != null) data.get("driver_name").toString else ""
        val mobile = if(data.get("mobile") != null) data.get("mobile").toString else ""
        val driver_type = if(data.get("driver_type") != null) data.get("driver_type").toString else ""
        val cancel_count = if(data.get("cancel_count") != null) data.get("cancel_count").toString else ""
        val driver_management_id = if(data.get("driver_management_id") != null) data.get("driver_management_id").toString else ""
        (table , DriverInfo(
          table,
          id ,
          type_ ,
          create_time ,
          register_city ,
//          city_name,
          driver_name ,
          mobile ,
          driver_type ,
          cancel_count ,
          driver_management_id
        ))

        //加盟表
      case table if(table.equals(opt_alliance_business) || table == opt_alliance_business) =>
        val id_ = data.getString("id_")
        val alliance_name = data.getString("alliance_name")
        val organization_code = data.getString("organization_code")
        val alliance_role = data.getString("alliance_role")
        val linkman = data.getString("linkman")
        val contact_number = data.getString("contact_number")
        val create_user = data.getString("create_user")
        val create_time = data.getString("create_time")
        val update_user = data.getString("update_user")
        val update_time = data.getString("update_time")
        val state = data.getString("state")
        val del_state = data.getString("del_state")
        (
          table , Opt_alliance_business(
          table ,
          type_ ,
          id_ ,
          alliance_name ,
          organization_code ,
          alliance_role ,
          linkman ,
          contact_number ,
          create_user ,
          create_time ,
          update_user ,
          update_time ,
          state ,
          del_state
        ))
      case _ =>
        ("#A" , "#A")

    }
    bean





  }
}
