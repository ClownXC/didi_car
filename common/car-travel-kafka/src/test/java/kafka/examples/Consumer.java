package kafka.examples;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumer extends ShutdownableThread {
    private Logger LOG = Logger.getLogger(this.getClass());
    private final KafkaConsumer<Integer, String> consumer;
    private final String topic;

    private FileOutputStream file = null;
    private BufferedOutputStream out = null;
    private PrintWriter printWriter = null;
    private String lineSeparator = null;
    private int batchNum = 0;


    public Consumer(String topic, String groupId) {
        super("KafkaConsumerExample", false);
        Properties props = new Properties();

        props.put("bootstrap.servers", "cdh-node01:9092");
//        props.put("bootstrap.servers", "192.168.52.110:9092");
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.offset.reset", "earliest");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);
        System.out.println("consumer:"+consumer);
        this.topic = topic;
    }

    @Override
    public void doWork() {

        consumer.subscribe(Collections.singletonList(this.topic));
        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(1));
        System.out.println("消费到消息数:" + records.count());
        if (records.count() > 0) {
            for (ConsumerRecord<Integer, String> record : records) {
                LOG.warn("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
                String value = record.value();
            }
        }
    }

    /**
     * 写入消息log
     *
     * @param msg 从kafka消费来的消息
     */
    protected void writeLog(String msg) {
        printWriter.write(msg + lineSeparator);
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    public static void main(String[] args) {
        //kafka主题
//        String topic = "test_msg";
        String topic = "cheng_du_gps_topic";
        //消费组id
        String groupId = "test001";

        Consumer consumer = new Consumer(topic, groupId);
        consumer.start();
    }
}
