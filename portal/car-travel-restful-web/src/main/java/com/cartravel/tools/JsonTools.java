package com.cartravel.tools;

import com.cartravel.entity._summary_vehicle_order_df;
import com.google.gson.Gson;

/**
 * Created by zxc
 */
public class JsonTools {

    public static _summary_vehicle_order_df parseJson2RequestParam_vehicle_order(String json){
        Gson gson = new Gson();
        final _summary_vehicle_order_df _vehicle_orderInfo = gson.fromJson(json, _summary_vehicle_order_df.class);
        return _vehicle_orderInfo;
    }
}
