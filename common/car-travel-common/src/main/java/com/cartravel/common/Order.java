package com.cartravel.common;

import java.io.Serializable;

/**
 * 订单实体类，用于描述订单信息
 */
public class Order implements Serializable {
    //城市编码
    private String cityCode="";
    //订单id
    private String orderId;

    //开始时间
    private long startTime;
    //结束时间
    private long endTime;
    //上车维度
    private String getOnLat;
    //上车经度
    private String getOnLng;

    //下车维度
    private String getOfLat;
    //下车经度
    private String getOfLng;

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getGetOnLat() {
        return getOnLat;
    }

    public void setGetOnLat(String getOnLat) {
        this.getOnLat = getOnLat;
    }

    public String getGetOnLng() {
        return getOnLng;
    }

    public void setGetOnLng(String getOnLng) {
        this.getOnLng = getOnLng;
    }

    public String getGetOfLat() {
        return getOfLat;
    }

    public void setGetOfLat(String getOfLat) {
        this.getOfLat = getOfLat;
    }

    public String getGetOfLng() {
        return getOfLng;
    }

    public void setGetOfLng(String getOfLng) {
        this.getOfLng = getOfLng;
    }
}
