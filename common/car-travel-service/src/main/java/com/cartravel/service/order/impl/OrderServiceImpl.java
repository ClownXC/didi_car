package com.cartravel.service.order.impl;

import com.cartravel.modules.*;
import com.cartravel.dao.GpsMapper;
import com.cartravel.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    GpsMapper gpsMapper;

    @Override
    public List<GpsVo> getOrderGps(QueryWrapper wrapper) {
        List<GpsVo> rest = new ArrayList<>();
        try {
            rest = gpsMapper.queryGps(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rest;
    }

    @Override
    public List<VirtualStationsVo> queryVirtualStations(StationWrapper wrapper) {
        List<VirtualStationsVo> rest = new ArrayList<>();
        try {
            rest = gpsMapper.queryVirtualStations(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rest;
    }

    @Override
    public List<VirtualStationCountVo> virtualStationCount(String cityId) {
        return gpsMapper.virtualStationCount(cityId);
    }
}
