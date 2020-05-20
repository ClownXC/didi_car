package com.cartravel.spark

/**
  * 订单解析器
  */
trait OrderParser {
  def parser(orderInfo:String):TravelOrder
}
