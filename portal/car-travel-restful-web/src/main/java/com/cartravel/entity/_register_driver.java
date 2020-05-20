package com.cartravel.entity;

/**
 * Created by zxc
 * 各城市司机注册情况
 */
public class _register_driver {
    private String register_city ;
    private String _register_year_num ;
    private String _register_quarter_num ;
    private String _register_month_num ;
    private String _register_week_num ;
    private String _register_day_num ;

    public String getRegister_city() {
        return register_city;
    }

    public void setRegister_city(String register_city) {
        this.register_city = register_city;
    }

    public String get_register_year_num() {
        return _register_year_num;
    }

    public void set_register_year_num(String _register_year_num) {
        this._register_year_num = _register_year_num;
    }

    public String get_register_quarter_num() {
        return _register_quarter_num;
    }

    public void set_register_quarter_num(String _register_quarter_num) {
        this._register_quarter_num = _register_quarter_num;
    }

    public String get_register_month_num() {
        return _register_month_num;
    }

    public void set_register_month_num(String _register_month_num) {
        this._register_month_num = _register_month_num;
    }

    public String get_register_week_num() {
        return _register_week_num;
    }

    public void set_register_week_num(String _register_week_num) {
        this._register_week_num = _register_week_num;
    }

    public String get_register_day_num() {
        return _register_day_num;
    }

    public void set_register_day_num(String _register_day_num) {
        this._register_day_num = _register_day_num;
    }

    @Override
    public String toString() {
        return "_register_driver{" +
                "register_city='" + register_city + '\'' +
                ", _register_year_num='" + _register_year_num + '\'' +
                ", _register_quarter_num='" + _register_quarter_num + '\'' +
                ", _register_month_num='" + _register_month_num + '\'' +
                ", _register_week_num='" + _register_week_num + '\'' +
                ", _register_day_num='" + _register_day_num + '\'' +
                '}';
    }
}
