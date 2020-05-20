package com.cartravel.ordermonitor;

import com.cartravel.util.JedisUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 订单实时轨迹监控的websocket服务
 */
@ServerEndpoint(value = "/websocket")
@Component
public class OrderTrackRealTimeMonitorWebSocket {
    private final static Logger logger = LoggerFactory.getLogger(OrderTrackRealTimeMonitorWebSocket.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<OrderTrackRealTimeMonitorWebSocket> webSocketSet =
            new CopyOnWriteArraySet<OrderTrackRealTimeMonitorWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private static Map<String, String> lastLngLat = new HashMap<String, String>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage("当前在线客户端数量:" + onlineCount);
        } catch (IOException e) {
            System.out.println("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);

        String s = realTimeTrack(message);
        if (StringUtils.isEmpty(s)) {
            //获取汽车最近一次的位置信息
            s = "none";
        }

        //群发消息
        for (OrderTrackRealTimeMonitorWebSocket item : webSocketSet) {
            try {
                item.sendMessage(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误时调用
     **/

    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {
        for (OrderTrackRealTimeMonitorWebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        OrderTrackRealTimeMonitorWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        OrderTrackRealTimeMonitorWebSocket.onlineCount--;
    }

    public String realTimeTrack(String orderId) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询实时轨迹点】:"+orderId);

        try {
            Jedis jedis = JedisUtil.getInstance().getJedis();

            //实时经纬度
            String lngAndlat = jedis.rpop(orderId);
            return lngAndlat;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
