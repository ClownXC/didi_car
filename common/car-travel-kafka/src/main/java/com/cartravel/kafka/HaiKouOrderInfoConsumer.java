package com.cartravel.kafka;

import com.cartravel.common.Constants;
import com.cartravel.common.TopicName;
import com.cartravel.util.HBaseUtil;
import com.cartravel.util.JedisUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HaiKouOrderInfoConsumer implements Runnable {
    private static Logger log = Logger.getLogger(HaiKouOrderInfoConsumer.class);
    //正则用于匹配订单行数据
    private static Pattern pattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    //计数消费到的消息条数
    private static int count = 0;
    private FileOutputStream file = null;
    private BufferedOutputStream out = null;
    private PrintWriter printWriter = null;
    private String lineSeparator = null;
    private int batchNum = 0;

    private String cityCode = "";
    private Map<String, String> orderMap = new HashMap<String, String>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HaiKouOrderInfoConsumer(String topic, String groupId) {
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

        //dev-hdp
//        props.put("bootstrap.servers", "192.168.21.173:6667,192.168.21.174:6667,192.168.21.175:6667");
        //dev-cdh
//        props.put("bootstrap.servers", "192.168.21.177:9092,192.168.21.178:9092,192.168.21.179:9092");
        //pro-cdh
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
        consumer.subscribe(Collections.singletonList(this.topic));
        ConsumerRecords<String, String> records = consumer.poll(1000);
        System.out.println("第" + batchNum + "批次," + records.count());

        if (records.count() > 0) {
            Table table = HBaseUtil.getTable(Constants.HTAB_HAIKOU_ORDER);
            JedisUtil instance = JedisUtil.getInstance();
            List<Put> puts = new ArrayList<>();
            String rowkey = "";

            if (orderMap.size() > 0) {
                orderMap.clear();
            }

            //表不存在时创建表
            if (!HBaseUtil.tableExists(Constants.HTAB_HAIKOU_ORDER)) {
                HBaseUtil.createTable(HBaseUtil.getConnection(), Constants.HTAB_HAIKOU_ORDER, Constants.DEFAULT_FAMILY);
            }

            for (ConsumerRecord<String, String> record : records) {
                count++;
                log.warn("Received message: (" + record.key() + ", " + record.value() + ") at offset " +
                        record.offset() + ",count:" + count);
                String line = record.value();

                //数据行时进行处理
                if (isDataLine(line)) {
                    String[] fields = line.split(" ");

                    //过滤掉不合法的数据行
                    if (fields.length != 26) {
                        continue;
                    }

                    //订单ID+出发时间作为hbase表的rowkey
                    rowkey = fields[0] + "_" + fields[13].replaceAll("-", "")
                            + fields[14].replaceAll(":", "");

                    orderMap.put("ORDER_ID", fields[0]);
                    orderMap.put("PRODUCT_ID", fields[1]);
                    orderMap.put("CITY_ID", fields[2]);
                    orderMap.put("DISTRICT", fields[3]);
                    orderMap.put("COUNTY", fields[4]);
                    orderMap.put("TYPE", fields[5]);
                    orderMap.put("COMBO_TYPE", fields[6]);
                    orderMap.put("TRAFFIC_TYPE", fields[7]);
                    orderMap.put("PASSENGER_COUNT", fields[8]);
                    orderMap.put("DRIVER_PRODUCT_ID", fields[9]);
                    orderMap.put("START_DEST_DISTANCE", fields[10]);
                    orderMap.put("ARRIVE_TIME", fields[11] + " " + fields[12]);
                    orderMap.put("DEPARTURE_TIME", fields[13] + " " + fields[14]);
                    orderMap.put("PRE_TOTAL_FEE", fields[15]);
                    orderMap.put("NORMAL_TIME", fields[16]);
                    orderMap.put("BUBBLE_TRACE_ID", fields[17]);
                    orderMap.put("PRODUCT_1LEVEL", fields[18]);
                    orderMap.put("DEST_LNG", fields[19]);
                    orderMap.put("DEST_LAT", fields[20]);
                    orderMap.put("STARTING_LNG", fields[21]);
                    orderMap.put("STARTING_LAT", fields[22]);
                    orderMap.put("YEAR", fields[23]);
                    orderMap.put("MONTH", fields[24]);
                    orderMap.put("DAY", fields[25]);

                    puts.add(HBaseUtil.createPut(rowkey, Constants.DEFAULT_FAMILY.getBytes(), orderMap));
                }
            }
            //批量插入数据到hbase中
            table.put(puts);
        }

        log.warn("正常结束...");
    }

    /**
     * 判断是否为数据行,过滤掉数据文件中的空行和文件头(也就是文件的第一行)
     *
     * @param line
     * @return
     * @throws Exception
     */
    public static boolean isDataLine(String line) throws Exception {
        if (StringUtils.isEmpty(line)) {
            return false;
        }
        Matcher matcher = pattern.matcher(line);
        boolean b = matcher.find();
        return b;
    }

    /**
     * 对经纬度进行栅格化
     * 这个是之前写的旧代码，使用墨卡托的转换方式，在整个项目中不在使用，改用uber h3算法
     * @param lnglats
     * @return 栅格化长度
     */
    public static int[] gridlength(double[] lnglats) {
        int latV =Integer.parseInt(new java.text.DecimalFormat("0").format((lnglats[0]*111*1000)/ Constants.GRID_LENGTH));
        int lngV =Integer.parseInt(new java.text.DecimalFormat("0").format((lnglats[1]*111*1000)/ Constants.GRID_LENGTH));

        return new int[]{latV, lngV};
    }

    public static void main(String[] args) {

//        Logger.getLogger("org.apache.kafka").setLevel(Level.INFO);
        //kafka主题
        String topic = "hai_kou_order_topic";
        //消费组id
        String groupId = "haikou_order_g_004";

        HaiKouOrderInfoConsumer gpsConsumer = new HaiKouOrderInfoConsumer(topic, groupId);
        Thread start = new Thread(gpsConsumer);
        start.start();
    }
}
