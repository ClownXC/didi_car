package com.cartravel.common;

import java.io.Serializable;

/**
 * 不同城市的订单和轨迹数据发往消息队列的主题名
 */
public enum TopicName implements Serializable {

    HAI_KOU_ORDER_TOPIC("hai_kou_order_topic","海口市订单主题"),

    CHENG_DU_ORDER_TOPIC("cheng_du_order_topic","成都市订单主题"),

    XI_AN_ORDER_TOPIC("xi_an_order_topic","西安市订单主题"),

    XI_AN_GPS_TOPIC("xi_an_gps_topic","海口市订单主题"),

    CHENG_DU_GPS_TOPIC("cheng_du_gps_topic","海口市订单主题");

    //主题名
    private String name;

    //主题描述
    private String desc;

    TopicName(String name, String desc){
        this.name=name;
    }

    public String getDesc(){
        return desc;
    }

    /**
     * 返回主题名称
     * @return
     */
    public String getTopicName(){
        return name;
    }

    public static void main(String[] args) {
        System.out.println(TopicName.CHENG_DU_GPS_TOPIC.getTopicName());
//        TopicName[] values = TopicName.values();
//        for(TopicName topic:values){
//            System.out.println(topic.getTopicName());
//        }
    }

}
