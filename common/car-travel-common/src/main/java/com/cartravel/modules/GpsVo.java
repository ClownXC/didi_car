package com.cartravel.modules;

/**
 * 封装订单轨迹信息实体类
 */
public class GpsVo {
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
