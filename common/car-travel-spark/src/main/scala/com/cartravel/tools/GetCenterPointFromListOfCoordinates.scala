package com.cartravel.tools

import redis.clients.jedis.GeoCoordinate

/**
  * Created by zxc
  */
object GetCenterPointFromListOfCoordinates {
  /**
    * 根据输入的地点坐标计算中心点
    *
    * @param geoCoordinateList
    * @return
    */
  def getCenterPoint(geoCoordinateList: List[GeoCoordinate]): GeoCoordinate = {
    val total: Int = geoCoordinateList.size
    var X: Double = 0
    var Y: Double = 0
    var Z: Double = 0
    for (g <- geoCoordinateList) {
      var lat: Double = .0
      var lon: Double = .0
      var x: Double = .0
      var y: Double = .0
      var z: Double = .0
      lat = g.getLatitude * Math.PI / 180
      lon = g.getLongitude * Math.PI / 180
      x = Math.cos(lat) * Math.cos(lon)
      y = Math.cos(lat) * Math.sin(lon)
      z = Math.sin(lat)
      X = X + x
      Y = Y + y
      Z = Z + z
    }
    X = X / total
    Y = Y / total
    Z = Z / total
    val Lon: Double = Math.atan2(Y, X)
    val Hyp: Double = Math.sqrt(X * X + Y * Y)
    val Lat: Double = Math.atan2(Z, Hyp)
    return new GeoCoordinate(Lat * 180 / Math.PI, Lon * 180 / Math.PI)
  }


}
