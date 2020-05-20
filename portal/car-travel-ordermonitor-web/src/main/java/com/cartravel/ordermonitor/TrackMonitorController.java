package com.cartravel.ordermonitor;

import com.cartravel.common.Constants;
import com.cartravel.common.Order;
import com.cartravel.common.TrackPoint;
import com.cartravel.modules.*;
import com.cartravel.service.order.OrderService;
import com.cartravel.util.HBaseUtil;
import com.cartravel.util.JedisUtil;
import com.cartravel.util.ObjUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 轨迹相关控制器
 */
@RestController
@RequestMapping("/track")


public class TrackMonitorController {
    private final static Logger logger = LoggerFactory.getLogger(TrackMonitorController.class);
    //
    @Autowired
    OrderService orderService;

    /**
     * 实时订单
     *
     * @return
     */
    @PostMapping("/realtimeOrder")
    public ResultModel<List<String>> realtimeOrder(@RequestBody QueryWrapper wrapper, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询实时轨迹...】");
        ResultModel<List<String>> result = new ResultModel<List<String>>();
        try {
            JedisUtil jedisUtil = JedisUtil.getInstance();
            Jedis jedis = jedisUtil.getJedis();

            ScanParams scanParams = new ScanParams();
            scanParams.match(wrapper.getCityCode() + "_*");
            ScanResult<String> sscan = jedis.sscan(Constants.REALTIME_ORDERS, "0", scanParams);
            List<String> orders = sscan.getResult();

            result.setSuccess(true);
            result.setData(orders);
            jedisUtil.returnJedis(jedis);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
        }
        logger.info("【查询实时轨迹】msg:{},time:{}", result.getMsg(),
                System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 查询订单实时轨迹点
     *
     * @param wrapper
     * @return
     */
    @PostMapping("/realTimeTrack")
    public ResultModel<String> realTimeTrack(@RequestBody QueryWrapper wrapper) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询实时轨迹】");
        ResultModel<String> result = new ResultModel<String>();
        try {
            Jedis jedis = JedisUtil.getInstance().getJedis();

            //实时经纬度
            String lngAndlat = jedis.get(wrapper.getOrderId());
            result.setSuccess(true);
            result.setData(lngAndlat);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
        }
        logger.info("【查询实时轨迹】msg:{},time:{}", result.getMsg(),
                System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 查询订单历史轨迹点
     *
     * @param wrapper
     * @return
     */
    @PostMapping("/historyTrackPoints")
    public ResultModel<List<TrackPoint>> historyTrackPoints(@RequestBody QueryWrapper wrapper) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询图形(点线面)】");
        ResultModel<List<TrackPoint>> result = new ResultModel<List<TrackPoint>>();
        Object tmpOrderObj = null;
        Order startEndTimeOrder = null;
        List<TrackPoint> list = null;

        try {
            String orderId = wrapper.getOrderId();
            JedisUtil instance = JedisUtil.getInstance();
            Jedis jedis = instance.getJedis();

            byte[] orderBytes = jedis.hget(Constants.ORDER_START_ENT_TIME.getBytes()
                    , orderId.getBytes());

            if (orderBytes != null) {
                tmpOrderObj = ObjUtil.deserialize(orderBytes);
            }

            if (null != tmpOrderObj) {
                startEndTimeOrder = (Order) tmpOrderObj;
                String starttime = startEndTimeOrder.getStartTime() + "";
                String enttime = startEndTimeOrder.getEndTime() + "";

                String tableName = Constants.HTAB_GPS;
                list = HBaseUtil.getRest(tableName, wrapper.getOrderId(),
                        starttime, enttime, TrackPoint.class);
            }

            result.setSuccess(true);
            result.setData(list);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
        }
        logger.info("【查询订单历史轨迹点】msg:{},time:{}", result.getMsg(),
                System.currentTimeMillis() - startTime);
        return result;
    }


    /**
     * 订单概况
     *
     * @param wrapper
     * @return
     */
    @PostMapping("/orderOverview")
    public ResultModel<OrderOverview> orderOverview(@RequestBody QueryWrapper wrapper) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询订单概况信息】");
        ResultModel<OrderOverview> result = new ResultModel<OrderOverview>();

        try {
            OrderOverview orderOverview = new OrderOverview();
            Jedis jedis = JedisUtil.getInstance().getJedis();

            String day = wrapper.getStartTime().split(" ")[0];
            //从redis中获取订单统计
            String orderCountStr = jedis.hget(Constants.ORDER_COUNT,
                    wrapper.getCityCode() + "_" + day);

            String passengerCountStr = jedis.hget(Constants.PASSENGER_COUNT,
                    wrapper.getCityCode() + "_" + day);

            String hourOrderCountTab = wrapper.getCityCode() + "_" + day + "hourOrderCount";
            Map<String, String> ordercountMap = jedis.hgetAll(hourOrderCountTab);
            Collection<String> values = ordercountMap.values();
            int ordercount = 0;
            for (String str : values) {
                ordercount += Integer.parseInt(str);
            }

            if (ordercount > 0) {
                orderOverview.setOrderCount(ordercount);
            } else {
                orderOverview.setOrderCount(Integer.parseInt(orderCountStr));
            }

            if (null != passengerCountStr) {
                orderOverview.setOrderPassengerCount(Integer.parseInt(passengerCountStr));
            }

            result.setSuccess(true);
            result.setData(orderOverview);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
        }
        logger.info("【查询订单概况信息】msg:{},time:{}", result.getMsg(),
                System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 小时订单统计
     *
     * @param wrapper
     * @return
     */
    @PostMapping("/hourOrderCount")
    public ResultModel<Map<String, String>> hourOrderCount(@RequestBody QueryWrapper wrapper) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询订单概况信息】");
        ResultModel<Map<String, String>> result = new ResultModel<Map<String, String>>();

        try {
            String day = wrapper.getStartTime().substring(0, wrapper.getStartTime().indexOf(" "));
            String hourOrderCountTab = wrapper.getCityCode() + "_" + day + "_hour_order_count";
            Jedis jedis = JedisUtil.getInstance().getJedis();
            Map<String, String> stringStringMap = jedis.hgetAll(hourOrderCountTab);

            result.setSuccess(true);
            result.setData(stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        }
        logger.info("【查询订单概况信息】msg:{},time:{}", result.getMsg(),
                System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 虚拟车站
     *
     * @param wrapper
     * @return
     */
    @PostMapping("/virtualStations")
    public ResultModel<List<VirtualStationsVo>> virtualStations(@RequestBody StationWrapper wrapper) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询订单概况信息】");
        ResultModel<List<VirtualStationsVo>> result = new ResultModel<List<VirtualStationsVo>>();

        try {
            List<VirtualStationsVo> virtualStationsVos = orderService.queryVirtualStations(wrapper);
            result.setSuccess(true);
            result.setData(virtualStationsVos);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
        }

        logger.info("【查询虚拟车站信息】msg:{},time:{}", result.getMsg(),
                System.currentTimeMillis() - startTime);
        return result;
    }


    /**
     * 查询虚拟车站统计信息
     *
     * @param wrapper
     * @return
     */
    @PostMapping("/virtualStationCount")
    public ResultModel<List<VirtualStationCountVo>> virtualStationCount(@RequestBody StationWrapper wrapper) {
        long startTime = System.currentTimeMillis();
        logger.info("【查询订单概况信息】");
        ResultModel<List<VirtualStationCountVo>> result = new ResultModel<List<VirtualStationCountVo>>();
        try {
            List<VirtualStationCountVo> list = orderService.virtualStationCount(wrapper.getCityCode());
            result.setSuccess(true);
            result.setData(list);
        } catch (Exception e) {
            result.setMsg(e.getMessage());
        }

        logger.info("【查询虚拟车站统计信息】msg:{},time:{}", result.getMsg(),
                System.currentTimeMillis() - startTime);
        return result;
    }
}
