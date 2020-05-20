package com.cartravel.entity;

/**
 * Created by zxc
 * 第2屏幕注册状态（平台注册用户总数、当日新增注册用户、本周内新增注册用户、当月新增注册用户
 */
public class _registerStatus {
    private String registerTotalCount ;
    private String dayNewUserCount ;
    private String weekNewUserCount ;
    private String monthNewUserCount ;

    public String getRegisterTotalCount() {
        return registerTotalCount;
    }

    public void setRegisterTotalCount(String registerTotalCount) {
        this.registerTotalCount = registerTotalCount;
    }

    public String getDayNewUserCount() {
        return dayNewUserCount;
    }

    public void setDayNewUserCount(String dayNewUserCount) {
        this.dayNewUserCount = dayNewUserCount;
    }

    public String getWeekNewUserCount() {
        return weekNewUserCount;
    }

    public void setWeekNewUserCount(String weekNewUserCount) {
        this.weekNewUserCount = weekNewUserCount;
    }

    public String getMonthNewUserCount() {
        return monthNewUserCount;
    }

    public void setMonthNewUserCount(String monthNewUserCount) {
        this.monthNewUserCount = monthNewUserCount;
    }

    @Override
    public String toString() {
        return "_registerStatus{" +
                "registerTotalCount='" + registerTotalCount + '\'' +
                ", dayNewUserCount='" + dayNewUserCount + '\'' +
                ", weekNewUserCount='" + weekNewUserCount + '\'' +
                ", monthNewUserCount='" + monthNewUserCount + '\'' +
                '}';
    }
}
