package com.cartravel.entity;

/**
 * Created by zxc
 */
public class _final_summary_pat_order {
    //city_name  | _year_comple_rate  | _quarter_comple_rate  | _month_comple_rate  | _week_comple_rate  | _day_comple_rate
    private String city_name ;
    private String _year_comple_rate; //平台年订单完成率
    private String _quarter_comple_rate ; //平台季度订单完成率
    private String _month_comple_rate ; //月订单完成率
    private String _week_comple_rate ;
    private String _day_comple_rate ;

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
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
        return "_final_summary_pat_order{" +
                "city_name='" + city_name + '\'' +
                ", _year_comple_rate='" + _year_comple_rate + '\'' +
                ", _quarter_comple_rate='" + _quarter_comple_rate + '\'' +
                ", _month_comple_rate='" + _month_comple_rate + '\'' +
                ", _week_comple_rate='" + _week_comple_rate + '\'' +
                ", _day_comple_rate='" + _day_comple_rate + '\'' +
                '}';
    }
}
