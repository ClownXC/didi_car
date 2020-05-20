package com.cartravel.entity;

/**
 * 封装订单轨迹信息实体类
 */
public class GpsEntity {
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

    /**
     *
     */
    private String[] lnglat;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String[] getLnglat() {
        return this.lnglat;
    }

    public void setLnglat(String[] lnglat) {
        this.lnglat = lnglat;
    }
}
