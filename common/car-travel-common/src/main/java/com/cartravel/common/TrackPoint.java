package com.cartravel.common;

/**
 * 抽象描述轨迹中的一个轨迹点:
 * 司机ID
 * 订单ID
 * 时间戳
 * 经度
 * 纬度
 */
public class TrackPoint {
    //订单id
    private String orderId;
    //司机id
    private String driverId;
    //时间戳
    private Long timestamp;
    //经度
    private String lng;

    //维度
    private String lat;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }




}
