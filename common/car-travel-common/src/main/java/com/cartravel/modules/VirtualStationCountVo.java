package com.cartravel.modules;

public class VirtualStationCountVo {
    /**
     * 行政区名称
     */
    private String districtName;
    /**
     * 虚拟车站统计结果
     */
    private String count;

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
