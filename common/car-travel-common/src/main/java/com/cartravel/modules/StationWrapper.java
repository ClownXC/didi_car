package com.cartravel.modules;

public class StationWrapper {
    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 行政区域名称
     */
    private String districtName;

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
}
