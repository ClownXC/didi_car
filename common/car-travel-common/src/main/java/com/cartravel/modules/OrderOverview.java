package com.cartravel.modules;

/**
 * 首页订单概览
 */
public class OrderOverview {
    /**
     * 订单总数
     */
    private int orderCount;

    /**
     * 订单乘车人数
     */
    private int orderPassengerCount;

    /**
     * 疲劳报警数
     */
    private int fatigueAlarmCount;


    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getOrderPassengerCount() {
        return orderPassengerCount;
    }

    public void setOrderPassengerCount(int orderPassengerCount) {
        this.orderPassengerCount = orderPassengerCount;
    }

    public int getFatigueAlarmCount() {
        return fatigueAlarmCount;
    }

    public void setFatigueAlarmCount(int fatigueAlarmCount) {
        this.fatigueAlarmCount = fatigueAlarmCount;
    }
}
