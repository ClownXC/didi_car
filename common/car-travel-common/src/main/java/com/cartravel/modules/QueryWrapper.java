package com.cartravel.modules;


public class QueryWrapper {
    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 轨迹开始时间戳
     */
    private String startTime;
    /**
     * 轨迹结束时间戳
     */
    private String endTime;
    /**
     * 表rowkey
     */
    private String rowkey;

    /**
     * 司机id
     */
    private String driverId;
    /**
     * 订单id
     */
    private String oerderId;

    /**
     * 轨迹产生时间戳
     */
    private String timestamp;

    /**
     * 轨迹产生时间戳,对应格式化的日期时间
     */
    private String time;

    /**
     * 经度
     */
    private String lng;

    /**
     * 维度
     */
    private String lat;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getOerderId() {
        return oerderId;
    }

    public void setOerderId(String oerderId) {
        this.oerderId = oerderId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
