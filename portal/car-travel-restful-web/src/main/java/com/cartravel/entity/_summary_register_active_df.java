package com.cartravel.entity;

/**
 * Created by zxc
 * 新增 、 活跃
 */
public class _summary_register_active_df {
    private String _city_code ;
    private String _day_new_user ; //日新增
    private String DAU ;    //日活跃

    public String get_city_code() {
        return _city_code;
    }

    public void set_city_code(String _city_code) {
        this._city_code = _city_code;
    }

    public String get_day_new_user() {
        return _day_new_user;
    }

    public void set_day_new_user(String _day_new_user) {
        this._day_new_user = _day_new_user;
    }

    public String getDAU() {
        return DAU;
    }

    public void setDAU(String DAU) {
        this.DAU = DAU;
    }

    @Override
    public String toString() {
        return "_summary_register_active_df{" +
                "_city_code='" + _city_code + '\'' +
                ", _day_new_user='" + _day_new_user + '\'' +
                ", DAU='" + DAU + '\'' +
                '}';
    }
}
