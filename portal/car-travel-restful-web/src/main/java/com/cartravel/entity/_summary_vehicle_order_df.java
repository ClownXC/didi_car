package com.cartravel.entity;

import java.io.Serializable;

/**
 * Created by zxc
 */
public class _summary_vehicle_order_df implements Serializable {
    private String o_begin_address_code ;
    private String monthVehicleCount ;
    private String dayVehicleCount ;
    private String monthOrderCount ;
    private String weekOrderCount ;
    private String dayOrderCount ;


    public String getO_begin_address_code() {
        return o_begin_address_code;
    }

    public void setO_begin_address_code(String o_begin_address_code) {
        this.o_begin_address_code = o_begin_address_code;
    }

    public String getMonthVehicleCount() {
        return monthVehicleCount;
    }

    public void setMonthVehicleCount(String monthVehicleCount) {
        this.monthVehicleCount = monthVehicleCount;
    }

    public String getDayVehicleCount() {
        return dayVehicleCount;
    }

    public void setDayVehicleCount(String dayVehicleCount) {
        this.dayVehicleCount = dayVehicleCount;
    }

    public String getMonthOrderCount() {
        return monthOrderCount;
    }

    public void setMonthOrderCount(String monthOrderCount) {
        this.monthOrderCount = monthOrderCount;
    }

    public String getWeekOrderCount() {
        return weekOrderCount;
    }

    public void setWeekOrderCount(String weekOrderCount) {
        this.weekOrderCount = weekOrderCount;
    }

    public String getDayOrderCount() {
        return dayOrderCount;
    }

    public void setDayOrderCount(String dayOrderCount) {
        this.dayOrderCount = dayOrderCount;
    }

    @Override
    public String toString() {
        return "_summary_vehicle_order_df{" +
                "o_begin_address_code='" + o_begin_address_code + '\'' +
                ", monthVehicleCount='" + monthVehicleCount + '\'' +
                ", dayVehicleCount='" + dayVehicleCount + '\'' +
                ", monthOrderCount='" + monthOrderCount + '\'' +
                ", weekOrderCount='" + weekOrderCount + '\'' +
                ", dayOrderCount='" + dayOrderCount + '\'' +
                '}';
    }
}
