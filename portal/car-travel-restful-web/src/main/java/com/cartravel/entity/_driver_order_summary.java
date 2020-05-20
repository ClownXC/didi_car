package com.cartravel.entity;

/**
 * Created by zxc
 */
public class _driver_order_summary {
    private String driver_id ;
    private String driver_name ;
    private String _year_comple_rate ;
    private String _quarter_comple_rate ;
    private String _month_comple_rate ;
    private String _week_comple_rate ;
    private String _day_comple_rate ;

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String get_year_comple_rate() {
        return _year_comple_rate;
    }

    public void set_year_comple_rate(String _year_comple_rate) {
        this._year_comple_rate = _year_comple_rate;
    }

    public String get_quarter_comple_rate() {
        return _quarter_comple_rate;
    }

    public void set_quarter_comple_rate(String _quarter_comple_rate) {
        this._quarter_comple_rate = _quarter_comple_rate;
    }

    public String get_month_comple_rate() {
        return _month_comple_rate;
    }

    public void set_month_comple_rate(String _month_comple_rate) {
        this._month_comple_rate = _month_comple_rate;
    }

    public String get_week_comple_rate() {
        return _week_comple_rate;
    }

    public void set_week_comple_rate(String _week_comple_rate) {
        this._week_comple_rate = _week_comple_rate;
    }

    public String get_day_comple_rate() {
        return _day_comple_rate;
    }

    public void set_day_comple_rate(String _day_comple_rate) {
        this._day_comple_rate = _day_comple_rate;
    }

    @Override
    public String toString() {
        return "_driver_order_summary{" +
                "driver_id='" + driver_id + '\'' +
                ", driver_name='" + driver_name + '\'' +
                ", _year_comple_rate='" + _year_comple_rate + '\'' +
                ", _quarter_comple_rate='" + _quarter_comple_rate + '\'' +
                ", _month_comple_rate='" + _month_comple_rate + '\'' +
                ", _week_comple_rate='" + _week_comple_rate + '\'' +
                ", _day_comple_rate='" + _day_comple_rate + '\'' +
                '}';
    }
}
