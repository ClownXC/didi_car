//package kafka.examples;
//
//
//import com.alibaba.fastjson.JSON;
//import kafka.common.OffsetAndMetadata;
//import kafka.server.KafkaConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.TopicPartition;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * Created by zxc on 2020/3/20
// */
//public abstract class PrefectCosumer {
//    private Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
//    int count = 0;
//    public final void consume() {
//        Properties properties = PropertiesConfig.getConsumerProperties();
//        properties.put("group.id", getGroupId());
//        Consumer<String, String> consumer = new KafkaConsumer<>(properties);
//        consumer.subscribe(getTopics());
//        consumer.poll(0);
//        // 把offset记录到数据库中 从指定的offset处消费
//        consumer.partitionsFor(getTopics()).stream().map(info ->
//                new TopicPartition(getTopics(), info.partition()))
//                .forEach(tp -> {
//                    consumer.seek(tp, JdbcUtils.queryOffset().get(tp.partition()));
//                });
//
//
//        try {
//            while (true) {
//                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMinutes(KafkaConfig.pollTimeoutOfMinutes));
//                if (!consumerRecords.isEmpty()) {
//                    for (ConsumerRecord<String, String> record : consumerRecords) {
//
//                        KafkaMessage kafkaMessage = JSON.parseObject(record.value(), KafkaMessage.class);
//                        boolean handleResult = handle(kafkaMessage);
//
//                        if (handleResult) {
//                            //注意：提交的是下一条消息的位移。所以OffsetAndMetadata 对象时，必须使用当前消息位移加 1。
//                            offsets.put(new TopicPartition(record.topic(), record.partition()),
//                                    new OffsetAndMetadata(record.offset() + 1));
//
//                            // 细粒度控制提交 每10条提交一次offset
//                            if (count % 10 == 0) {
//                                // 异步提交offset
//                                consumer.commitAsync(offsets, (offsets, exception) -> {
//                                    if (exception != null) {
//                                        handleException(exception);
//                                    }
//                                    // 将消费位移再记录一份到数据库中
//                                    offsets.forEach((k, v) -> {
//
//                                        String s = "insert into kafka_offset(`topic`,`group_id`,`partition_id`,`offset`) values" +
//                                                " ('" + k.topic() + "','" + getGroupId() + "'," + k.partition() + "," + v.offset() + ")" +
//                                                " on duplicate key update offset=values(offset);";
//                                        JdbcUtils.insertTable(s);
//                                    });
//
//
//                                });
//                            }
//                            count++;
//                        } else {
//                            // 失败的时候重试10次 防止因为网络抖动导致没有成功处理逻辑
//                            Boolean retryResult = false;
//                            for (int i = 0; i < 10; i++) {
//                                retryResult = handle(kafkaMessage);
//                                if (retryResult) {
//                                    break;
//                                }
//                            }
//
//                            if (!retryResult) {
//                                System.out.println("消费消息失败 kafkaMessage={}" + getTopics() + getGroupId() + kafkaMessage.toString());
//                            }
//                        }
//                    }
//
//
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("kafka consumer error:" + e.toString());
//        } finally {
//            try {
//                // 最后一次提交 使用同步提交offset
//                consumer.commitSync();
//            } finally {
//                consumer.close();
//            }
//
//
//        }
//    }
//
//
//    /**
//     * 具体的业务逻辑
//     *
//     * @param kafkaMessage
//     * @return
//     */
//    public abstract boolean handle(KafkaMessage kafkaMessage);
//
//    public abstract List<String> getTopics();
//
//    public abstract String getGroupId();
//
//    void handleException(Exception e) {
//        System.out.println("commitAsync handleException"+e);
//    }
//}
