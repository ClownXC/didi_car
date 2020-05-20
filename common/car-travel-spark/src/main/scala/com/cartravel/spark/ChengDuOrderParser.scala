package com.cartravel.spark

class ChengDuOrderParser extends OrderParser {
  def parser(orderInfo: String): TravelOrder = {
    var orderInfos = orderInfo.split(",",-1)
    var order = new ChengDuTravelOrder();
    order.orderId= orderInfos(0);
    order
  }
}