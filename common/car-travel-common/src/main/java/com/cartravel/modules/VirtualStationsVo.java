package com.cartravel.modules;

public class VirtualStationsVo {
    //城市编码
    private String cityCode;
    //行政区域名称
    private String districtName;
    //开始经度
    private String startingLng;
    //开始维度
    private String startingLat;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getStartingLng() {
        return startingLng;
    }

    public void setStartingLng(String startingLng) {
        this.startingLng = startingLng;
    }

    public String getStartingLat() {
        return startingLat;
    }

    public void setStartingLat(String startingLat) {
        this.startingLat = startingLat;
    }
}
