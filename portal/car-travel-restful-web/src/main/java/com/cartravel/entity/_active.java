package com.cartravel.entity;

/**
 * Created by zxc
 */
public class _active {
    private String DAU ;
    private String WAU ;
    private String MAU ;

    public String getDAU() {
        return DAU;
    }

    public void setDAU(String DAU) {
        this.DAU = DAU;
    }

    public String getWAU() {
        return WAU;
    }

    public void setWAU(String WAU) {
        this.WAU = WAU;
    }

    public String getMAU() {
        return MAU;
    }

    public void setMAU(String MAU) {
        this.MAU = MAU;
    }

    @Override
    public String toString() {
        return "_active{" +
                "DAU='" + DAU + '\'' +
                ", WAU='" + WAU + '\'' +
                ", MAU='" + MAU + '\'' +
                '}';
    }
}
