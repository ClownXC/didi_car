package com.cartravel.spark

/**
  * 订单必须实现序列化，如果没有实现就会出现spark任务序列化失败不能正常运行
  */
class TravelOrder extends Serializable {
  //订单id
  var orderId: String = "";

  //订单产生时间
  var createTime: String = "";

  //订单产生日期
  var createDay: String = "";
}

/**
  * 西安订单
  */
class XiAnTravelOrder extends TravelOrder {}

/**
  * 成都订单
  */
class ChengDuTravelOrder extends TravelOrder {}

/**
  * 海口订单
  */
class HaiKouTravelOrder extends TravelOrder {
  var productId: String = "";
  var cityId: String = "";
  var district: String = "";
  var county: String = "";
  //订单时效类型,0实时，1预约
  var orderTimeType: String = "";
  var comboType: String = "";
  var trafficType: String = "";
  var passengerCount: String = "";
  var driverProductId: String = "";
  var startDestDistance: String = "";
  var arriveDay: String = ""
  var arriveTime: String = "";
  var departureDay: String = "";
  var departureTime: String = "";
  var preTotalFee: String = "";
  var normalTime: String = "";
  var bubbleTraceId: String = "";
  var productLlevel: String = "";
  var destLng: String = "";
  var destLat: String = "";
  var startingLng: String = "";
  var startingLat: String = "";
  var year: String = "";
  var month: String = "";
  var day: String = "";
}