package com.cartravel.entity;

/**
 * Created by zxc
 */
public class _hot_area_result {
    private String begin_address_code ;
    private String centerPoint ;
    private String count ;

    public String getBegin_address_code() {
        return begin_address_code;
    }

    public void setBegin_address_code(String begin_address_code) {
        this.begin_address_code = begin_address_code;
    }

    public String getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(String centerPoint) {
        this.centerPoint = centerPoint;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "_hot_area_result{" +
                "begin_address_code='" + begin_address_code + '\'' +
                ", centerPoint='" + centerPoint + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
