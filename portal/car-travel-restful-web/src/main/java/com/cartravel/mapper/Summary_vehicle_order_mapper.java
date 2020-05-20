package com.cartravel.mapper;

import com.cartravel.entity._summary_vehicle_order_df;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zxc
 */
@Mapper
public interface Summary_vehicle_order_mapper {
    List<_summary_vehicle_order_df> vehicle_order_searchContext();
}
