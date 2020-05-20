package com.cartravel.controller;

import com.cartravel.entity.*;
import com.cartravel.log.Logging;
import com.cartravel.mapper.*;
import com.cartravel.tools.JsonTools;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by zxc
 */
@SpringBootApplication
@Controller
@RequestMapping("didi")//localhost:8989/didi/vehicle_order
public class SearchController extends Logging {
    /**各城市车辆分布和各城市订单*/
    @Autowired
    private Summary_vehicle_order_mapper summary_vehicle_order_mapper ;

    /**订单汇总表(总、月、日、里程)*/
    @Autowired
    private _day_week_month_mileage_mapper day_week_month_mileage_mapper ;

    /**订单总数 、 注册总数 、 收入总数*/
    @Autowired
    private _payAll_register_order_count_mapper payAll_register_order_count_mapper ;

    /**各城市当日新增用户数、当日活跃用户*/
    @Autowired
    private _summary_register_active_mapper summary_register_active_mapper ;

    @Autowired
    private _registerStatusMapper registerStatusMapper ;

    @Autowired
    private _stayRate_mapper stayRateMapper;

    @Autowired
    private _active_mapper activeMapper;

    @Autowired
    private _hot_order_mapper hotOrderMapper;

    @Autowired
    private _final_summary_pat_order_mapper final_summary_pat_order_mapper ;

    @Autowired
    private _driver_order_summary_mapper driver_order_summary_mapper ;

    @Autowired
    private _register_driver_mapper register_driver_mapper ;

    @Autowired
    private hot_area_result_mapper hot_area_result_mapper;

    /**
     * (各城市车辆分布和各城市订单)
     * */
    @ResponseBody
    @RequestMapping(value = "/vehicle_order" , method = RequestMethod.POST)
    public String _receive_vehicle_order(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();

            final List<_summary_vehicle_order_df> iter_vehicle_order = summary_vehicle_order_mapper.vehicle_order_searchContext();
            for(_summary_vehicle_order_df list:iter_vehicle_order){
                System.out.println(list.toString());
            }
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(iter_vehicle_order);
        }catch (Exception e){
            error("#########################################\n" +
                    "###error#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;

    }

    /**
     订单汇总表(总、月、日、里程)
     * */
    @ResponseBody
    @RequestMapping(value = "/dwmm" , method = RequestMethod.POST)
    public String _day_week_month_mileage_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_day_week_month_mileage> day_week_month_mileages = day_week_month_mileage_mapper._day_week_month_mileage_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(day_week_month_mileages);
        }catch (Exception e){
            error("#########################################\n" +
                    "###error   dwmm#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }


    /**
     * 订单总数 、 注册总数 、 收入总数）
     * */
    @ResponseBody
    @RequestMapping(value = "/pro" , method = RequestMethod.POST)
    public String _payAll_register_order_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_payAll_register_order_count> payAll_register_order_counts = payAll_register_order_count_mapper._payAll_register_order_count_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(payAll_register_order_counts);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   pro#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     * 各城市当日新增用户数、当日活跃用户
     * */
    @ResponseBody
    @RequestMapping(value = "/sras" , method = RequestMethod.POST)
    public String _summary_register_active_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_summary_register_active_df> summary_register_active_dfs = summary_register_active_mapper._summary_register_active_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(summary_register_active_dfs);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   sras#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     * 第2屏幕注册状态（平台注册用户总数、当日新增注册用户、本周内新增注册用户、当月新增注册用户
     * */
    @ResponseBody
    @RequestMapping(value = "/rss" , method = RequestMethod.POST)
    public String _registerStatus_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_registerStatus> registerStatuses = registerStatusMapper._registerStatus_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(registerStatuses);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   rss#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     * 第2屏幕（留存率）
     * */
    @ResponseBody
    @RequestMapping(value = "/srs" , method = RequestMethod.POST)
    public String _stayRate_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_stayRate> stayRates = stayRateMapper._stayRate_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(stayRates);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   srs#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     * 第2屏幕（活跃用户）
     * */
    @ResponseBody
    @RequestMapping(value = "/as" , method = RequestMethod.POST)
    public String _active_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_active> actives = activeMapper._active_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(actives);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   as#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }



    /**
     * 日/周/月/季/年 - 平台订单完成率
     * */
    @ResponseBody
    @RequestMapping(value = "/fspos" , method = RequestMethod.POST)
    public String finalSummaryPatOrder_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_final_summary_pat_order> final_summary_pat_orders = final_summary_pat_order_mapper._final_summary_pat_order_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(final_summary_pat_orders);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   fspos#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     *司机订单完成率
     * */
    @ResponseBody
    @RequestMapping(value = "/doss" , method = RequestMethod.POST)
    public String _driver_order_summary_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_driver_order_summary> driver_order_summaries = driver_order_summary_mapper._driver_order_summary_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(driver_order_summaries);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   fspos#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     *各个城市的司机注册情况
     * */
    @ResponseBody
    @RequestMapping(value = "/rds" , method = RequestMethod.POST)
    public String _register_driver_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_register_driver> register_drivers = register_driver_mapper._register_driver_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(register_drivers);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   fspos#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     *热力图
     * */
    @ResponseBody
    @RequestMapping(value = "/hars" , method = RequestMethod.POST)
    public String hot_area_result_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_hot_area_result> hot_area_results = hot_area_result_mapper.hot_area_result_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(hot_area_results);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   hars#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }

    /**
     * 第3屏幕（当日热区订单、当日新增热区订单）
     * */
    @ResponseBody
    @RequestMapping(value = "/hos" , method = RequestMethod.POST)
    public String _hot_order_search(@RequestBody String json , HttpServletRequest request , HttpServletResponse response){
        String toJson = null ;
        try{
            Long startTime = System.currentTimeMillis();
            final List<_hot_order> hot_orders = hotOrderMapper._hot_order_searchContext();
            Long endTime = System.currentTimeMillis();
            info("############当前检索hbase消耗的时间：[" + (endTime - startTime)+"ms]  ################\n\n");
            Gson gson = new Gson();
            toJson = gson.toJson(hot_orders);
        }catch (Exception  e){
            error("#########################################\n" +
                    "###error   hos#####\n" +
                    "#########################################");
            e.printStackTrace();
        }
        return toJson;
    }
}
