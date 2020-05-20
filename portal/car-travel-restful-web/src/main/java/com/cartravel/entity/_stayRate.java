package com.cartravel.entity;

/**
 * Created by zxc
 *第2屏幕（留存率）
 */
public class _stayRate {
    private String dayStateRate ;
    private String weekStateRate ;
    private String monthStateRate ;

    public String getDayStateRate() {
        return dayStateRate;
    }

    public void setDayStateRate(String dayStateRate) {
        this.dayStateRate = dayStateRate;
    }

    public String getWeekStateRate() {
        return weekStateRate;
    }

    public void setWeekStateRate(String weekStateRate) {
        this.weekStateRate = weekStateRate;
    }

    public String getMonthStateRate() {
        return monthStateRate;
    }

    public void setMonthStateRate(String monthStateRate) {
        this.monthStateRate = monthStateRate;
    }

    @Override
    public String toString() {
        return "_stayRate{" +
                "dayStateRate='" + dayStateRate + '\'' +
                ", weekStateRate='" + weekStateRate + '\'' +
                ", monthStateRate='" + monthStateRate + '\'' +
                '}';
    }
}
