package com.cartravel.kafka;

import com.cartravel.common.Constants;
import com.cartravel.common.Order;
import com.cartravel.common.TopicName;
import com.cartravel.util.HBaseUtil;
import com.cartravel.util.JedisUtil;
import com.cartravel.util.ObjUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zxc
 */
public class GpsConsumer implements Runnable {
    private static Logger log = Logger.getLogger(GpsConsumer.class);
    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    /**计数消费到的消息条数*/
    private static int count = 0;
    private FileOutputStream file = null;
    private BufferedOutputStream out = null;
    private PrintWriter printWriter = null;
    private String lineSeparator = null;
    private int batchNum = 0;
    JedisUtil instance = null;
    Jedis jedis = null;

    private String cityCode = "";
    private Map<String, String> gpsMap = new HashMap<String, String>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GpsConsumer(String topic, String groupId) {
        if (topic.equalsIgnoreCase(TopicName.CHENG_DU_GPS_TOPIC.getTopicName())) {
            cityCode = Constants.CITY_CODE_CHENG_DU;
        } else if (topic.equalsIgnoreCase(TopicName.XI_AN_GPS_TOPIC.getTopicName())) {
            cityCode = Constants.CITY_CODE_XI_AN;
        } else if (topic.equalsIgnoreCase(TopicName.HAI_KOU_ORDER_TOPIC.getTopicName())) {
            cityCode = Constants.CITY_CODE_HAI_KOU;
        } else {
            throw new IllegalArgumentException(topic + ",主题名称不合法!");
        }

        Properties props = new Properties();

        props.put("bootstrap.servers", Constants.KAFKA_BOOTSTRAP_SERVERS);

        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.offset.reset", "earliest");
//        props.put("auto.offset.reset", "latest");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<String, String>(props);
        this.topic = topic;
    }

    @Override
    public void run() {
        while (true) {
            try {
                doWork();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doWork() throws Exception {
        batchNum++;
        consumer.subscribe(Collections.singletonList(this.topic));
        ConsumerRecords<String, String> records = consumer.poll(1000);
        System.out.println("第" + batchNum + "批次," + records.count());
        //司机ID
        String driverId = "";
        //订单ID
        String orderId = "";
        //经度
        String lng = "";
        //维度
        String lat = "";
        //时间戳
        String timestamp = "";
        Order order = null;
        Order startEndTimeOrder = null;
        Object tmpOrderObj = null;
        if (records.count() > 0) {
            Table table = HBaseUtil.getTable(Constants.HTAB_GPS);
            JedisUtil instance = JedisUtil.getInstance();
            jedis = instance.getJedis();
            List<Put> puts = new ArrayList<>();
            String rowkey = "";

            if (gpsMap.size() > 0) {
                gpsMap.clear();
            }

            //表不存在时创建表
            if (!HBaseUtil.tableExists(Constants.HTAB_GPS)) {
                HBaseUtil.createTable(HBaseUtil.getConnection(), Constants.HTAB_GPS, Constants.DEFAULT_FAMILY);
            }

            for (ConsumerRecord<String, String> record : records) {
                count++;
                log.warn("Received message: (" + record.key() + ", " + record.value() + ") at offset " +
                        record.offset() + ",count:" + count);
                String value = record.value();

                //如果是轨迹文件内容结束的话，讲end标记写入到redis中，表名当前订单已经结束
                if (value.startsWith("end") && value.contains(",")) {
                    orderId = value.split(",")[1];
                    jedis.lpush(cityCode + "_" + orderId, "end");
                    continue;
                }
                //否则正常的经纬度信息直接使用逗号进行分割写入到redis中
                if (value.contains(",")) {
                    order = new Order();
                    String[] split = value.split(",");
                    driverId = split[0];
                    orderId = split[1];
                    timestamp = split[2];
                    lng = split[3];
                    lat = split[4];

                    rowkey = orderId + "_" + timestamp;
                    gpsMap.put("CITYCODE", cityCode);
                    gpsMap.put("DRIVERID", driverId);
                    gpsMap.put("ORDERID", orderId);
                    gpsMap.put("TIMESTAMP", timestamp + "");
                    gpsMap.put("TIME", sdf.format(new Date(Long.parseLong(timestamp + "000"))));
                    gpsMap.put("LNG", lng);
                    gpsMap.put("LAT", lat);

                    order.setOrderId(orderId);

                    puts.add(HBaseUtil.createPut(rowkey, Constants.DEFAULT_FAMILY.getBytes(), gpsMap));

                    //1.存入实时订单单号
                    jedis.sadd(Constants.REALTIME_ORDERS, cityCode + "_" + orderId);
                    //2.存入实时订单的经纬度信息
                    jedis.lpush(cityCode + "_" + orderId, lng + "," + lat);
                    //3.存入订单的开始结束时间信息

                    byte[] orderBytes = jedis.hget(Constants.ORDER_START_ENT_TIME.getBytes()
                            , orderId.getBytes());

                    if (orderBytes != null) {
                        tmpOrderObj = ObjUtil.deserialize(orderBytes);
                    }

                    if (null != tmpOrderObj) {
                        startEndTimeOrder = (Order) tmpOrderObj;
                        startEndTimeOrder.setEndTime(Long.parseLong(timestamp + "000"));
                        jedis.hset(Constants.ORDER_START_ENT_TIME.getBytes(), orderId.getBytes(),
                                ObjUtil.serialize(startEndTimeOrder));
                    } else {
                        //第一次写入订单的开始时间,开始时间和结束时间一样
                        order.setStartTime(Long.parseLong(timestamp));
                        order.setEndTime(Long.parseLong(timestamp));
                        jedis.hset(Constants.ORDER_START_ENT_TIME.getBytes(), orderId.getBytes(),
                                ObjUtil.serialize(order));
                    }
                    hourOrderInfoGather(jedis, gpsMap);
                }
            }
            table.put(puts);
            instance.returnJedis(jedis);
        }
        log.warn("正常结束...");
    }

    /**
     * 统计城市的每小时的订单信息和订单数
     *
     * @throws Exception
     */
    public void hourOrderInfoGather(Jedis jedis, Map<String, String> gpsMap) throws Exception {
        String time = gpsMap.get("TIME");
        String orderId = gpsMap.get("ORDERID");
        String day = time.substring(0, time.indexOf(" "));
        String hour = time.split(" ")[1].substring(0, 2);
        //redis表名,小时订单统计
        String hourOrderCountTab = cityCode + "_" + day + "_hour_order_count";

        //redis表名,小时订单ID
        String hourOrderField = cityCode + "_" + day + "_" + hour;
        String hourOrder = cityCode + "_order";

        int hourOrderCount = 0;
        //redis set集合中存放每小时内的所有订单id
        if (!jedis.sismember(hourOrder, orderId)) {
            //使用set存储小时订单id
            jedis.sadd(hourOrder, orderId);
            String hourOrdernum = jedis.hget(hourOrderCountTab, hourOrderField);
            if (StringUtils.isEmpty(hourOrdernum)) {
                hourOrderCount = 1;
            } else {
                hourOrderCount = Integer.parseInt(hourOrdernum) + 1;
            }

            //HashMap 存储每个小时的订单总数
            jedis.hset(hourOrderCountTab, hourOrderField, hourOrderCount + "");
        }
    }

    public static void main(String[] args) {

        Logger.getLogger("org.apache.kafka").setLevel(Level.INFO);
        //kafka主题
        String topic = "cheng_du_gps_topic";
        //消费组id
        String groupId = "cheng_du_gps_consumer_01";

        GpsConsumer gpsConsumer = new GpsConsumer(topic, groupId);
        Thread start = new Thread(gpsConsumer);
        start.start();
    }
}
