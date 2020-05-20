package com.cartravel.common;

public class Constants {
    //dev-cdh
    public final static String KAFKA_BOOTSTRAP_SERVERS = "cdh-node01:9092,cdh-node02:9092,cdh-node03:9092";
//    public final static String KAFKA_BOOTSTRAP_SERVERS = "10.20.3.177:9092,10.20.3.178:9092,10.20.3.179:9092";
//    public final static String KAFKA_BOOTSTRAP_SERVERS = "117.51.136.252:9092,117.51.142.225:9092,117.51.148.59:9092";
    //存储订单gps轨迹信息
    public final static String HTAB_GPS = "HTAB_GPS";

    public final static String HTAB_HAIKOU_ORDER = "HTAB_HAIKOU_ORDER";

    //默认hbase表的列簇名
    public final static String DEFAULT_FAMILY = "f1";

    //成都市编码
    public final static String CITY_CODE_CHENG_DU = "510100";

    //西安市城市编码
    public final static String CITY_CODE_XI_AN = "610100";

    //海口
    public final static String CITY_CODE_HAI_KOU = "460100";

    //订单数,在redis中作为hash结构的key名称
    public final static String ORDER_COUNT = "order_count";

    //人数统计,在redis中存储乘车人数实时统计的结果.
    public final static String PASSENGER_COUNT = "passenger_count";

    //实时订单
    public final static String REALTIME_ORDERS = "realtime_orders";

    //订单起始时间,将订单实体类进行序列化存入到redis中,用于判断订单是实时订单还是历史订单.
    public final static String ORDER_START_ENT_TIME = "order_start_ent_time";

    //经纬度栅格化半径(单位:米)
    public final static int GRID_LENGTH=100;
}
