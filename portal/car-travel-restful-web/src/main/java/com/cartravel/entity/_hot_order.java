package com.cartravel.entity;

/**
 * Created by zxc
 */
public class _hot_order {
    private String _city_code ;
    private String _day_order_num ;
    private String _new_orders_num ;

    public String get_city_code() {
        return _city_code;
    }

    public void set_city_code(String _city_code) {
        this._city_code = _city_code;
    }

    public String get_day_order_num() {
        return _day_order_num;
    }

    public void set_day_order_num(String _day_order_num) {
        this._day_order_num = _day_order_num;
    }

    public String get_new_orders_num() {
        return _new_orders_num;
    }

    public void set_new_orders_num(String _new_orders_num) {
        this._new_orders_num = _new_orders_num;
    }

    @Override
    public String toString() {
        return "_hot_order{" +
                "_city_code='" + _city_code + '\'' +
                ", _day_order_num='" + _day_order_num + '\'' +
                ", _new_orders_num='" + _new_orders_num + '\'' +
                '}';
    }
}
