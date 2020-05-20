package com.cartravel.service.order;


import com.cartravel.modules.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单服务
 */
@Service
public interface OrderService {
    /**
     * 返回订单的轨迹信息
     *
     * @param wrapper
     * @return
     * @throws Exception
     */
    List<GpsVo> getOrderGps(QueryWrapper wrapper);

    /**
     * 返回订单的轨迹信息
     *
     * @param wrapper
     * @return
     * @throws Exception
     */
    List<VirtualStationsVo> queryVirtualStations(StationWrapper wrapper);

    /**
     * 获取行政区域虚拟车站数量统计信息
     * @param cityId
     * @return
     */
    List<VirtualStationCountVo> virtualStationCount(String cityId);
}
