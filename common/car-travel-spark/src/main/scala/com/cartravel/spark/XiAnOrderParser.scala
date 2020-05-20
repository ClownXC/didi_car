package com.cartravel.spark

class XiAnOrderParser extends OrderParser {
  def parser(orderInfo: String): TravelOrder = {
    var orderInfos = orderInfo.split(",",-1)
    var order = new XiAnTravelOrder();
    order.orderId= orderInfos(0);
    order
  }
}