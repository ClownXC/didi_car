package com.cartravel.dao;

import com.cartravel.entity.GpsEntity;
import com.cartravel.modules.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GpsMapper {
    /**
     * 查询订单轨迹信息
     * @param wrapper
     * @return
     */
    List<GpsEntity> query(QueryWrapper wrapper);

    /**
     * 查询gps信息
     * @param wrapper
     * @return
     */
    List<GpsVo> queryGps(QueryWrapper wrapper);

    /**
     * 查询虚拟车站
     * @param wrapper
     * @return
     */
    List<VirtualStationsVo> queryVirtualStations(StationWrapper wrapper);

    /**
     * 获取行政区域虚拟车站数量统计信息
     * @param cityId
     * @return
     */
    List<VirtualStationCountVo> virtualStationCount(String cityId);
}
