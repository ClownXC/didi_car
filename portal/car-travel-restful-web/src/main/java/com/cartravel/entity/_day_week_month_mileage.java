package com.cartravel.entity;

import java.io.Serializable;

/**
 * Created by zxc
 */
public class _day_week_month_mileage implements Serializable {
    private String totalOrderNum ;
    private String monthOrderNum ;
    private String avgOrderNum ;
    private String charge_mileage ;

    /**
     *  SELECT
     "totalOrderNum" ,
     "monthOrderNum" ,
     "avgOrderNum" ,
     "charge_mileage"
     FROM
     "summary_plat"
     * */

    public String getTotalOrderNum() {
        return totalOrderNum;
    }

    public void setTotalOrderNum(String totalOrderNum) {
        this.totalOrderNum = totalOrderNum;
    }

    public String getMonthOrderNum() {
        return monthOrderNum;
    }

    public void setMonthOrderNum(String monthOrderNum) {
        this.monthOrderNum = monthOrderNum;
    }

    public String getAvgOrderNum() {
        return avgOrderNum;
    }

    public void setAvgOrderNum(String avgOrderNum) {
        this.avgOrderNum = avgOrderNum;
    }

    public String getCharge_mileage() {
        return charge_mileage;
    }

    public void setCharge_mileage(String charge_mileage) {
        this.charge_mileage = charge_mileage;
    }


    @Override
    public String toString() {
        return "_day_week_month_mileage{" +
                "totalOrderNum='" + totalOrderNum + '\'' +
                ", monthOrderNum='" + monthOrderNum + '\'' +
                ", avgOrderNum='" + avgOrderNum + '\'' +
                ", charge_mileage='" + charge_mileage + '\'' +
                '}';
    }
}
