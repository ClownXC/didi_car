package com.cartravel.bean

/**
  * Created by zxc
  */
case class OrderInfo(
                      table: String,
                      id: String,
                      operatorType: String,
                      pay_all: String,
                      pay_normal: String, //产生费用：里程费 + 时间费 + 过桥费 + 停车费 + 高速费 + 远途费 + ...
                      create_time: String, //订单创建时间
                      open_lng: String, //乘客预计上车经度
                      open_lat: String, //乘客预计上车维度
                      begin_address_code: String, //区域编码(街道)
                      charge_mileage: String, //公里数
                      city_name: String, //下单的城市名称
                      vehicle_license: String, //车牌
                      driver_id: String, //司机id
                      driver_name: String, //司机名称
                      cancel: String, //0 未取消，1 用户取消 ，2司机取消 ， 3超时未接单，系统自动取消
                      state: String, //订单流转状态：1已下单，2、已接单，3用车中，4到达目的地，5未支付，6已支付完成，7司机到达约定地点
                      order_type: String, //订单类型。0、实时订单 2、预约订单
                      work_flow: String, //0未结束，1已结束
                      close_gps_time: String, //订单结束时间
                      cancel_time: String, //取消订单时间
                      pay_time: String //订单支付时间
                    )










