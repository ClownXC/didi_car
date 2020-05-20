package com.cartravel.controller;
import com.cartravel.util.JedisUtil;
import org.codehaus.jackson.map.ObjectMapper;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootApplication
@Controller
@RequestMapping("didiRedis")
public class SearchRedisController {
    @ResponseBody
    @RequestMapping(value = "/monitoring", method = RequestMethod.POST)
    public String monitoring(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
        //获取连接redis
        JedisUtil jedisUtil = JedisUtil.getInstance();
        // Jedis jedis = JedisUtil.getInstance().getJedis();
        JedisUtil.Keys keysMethod = jedisUtil.new Keys();
        JedisUtil.Strings strings = jedisUtil.new Strings();
        //keys方法
        Set<String> keys = keysMethod.keys("MONITOR_DP*");
        List<Long> list = new ArrayList<>();
        if (null == keys || keys.size() == 0) {
            return null;
        } else {
            //遍历key
            for (String str : keys) {
                //replace 通过null替换"MONITOR_DP"
                String monitor_dp = str.replace("MONITOR_DP", "");
                long timeStamp = Long.parseLong(monitor_dp);
                list.add(timeStamp);
            }
        }
        //降序
        Collections.sort(list);
        Collections.reverse(list);
        String format = "yyyy-MM-dd";//日期格式
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = new HashMap<String,String>();
        List listDetails  = new ArrayList();
        //   List listTime  = new ArrayList();
        //详情 展示前5条
        int len;
        if (list.size()<5){
            len=list.size();
        }else {
            len=5;
        }
        for (int i = 0; i <len; i++) {
            String timeStamp = list.get(i).toString();

            String key = "MONITOR_DP" + timeStamp;
            String value = strings.get(key);
            JSONObject jsonObject = new JSONObject(value);
            //任务详情
            Object activeJobs = jsonObject.get("activeJobs");
            Object allJobs = jsonObject.get("allJobs");
            Object runningStages = jsonObject.get("runningStages");
            Object waitingStages = jsonObject.get("waitingStages");
            Object runningBatches = jsonObject.get("runningBatches");
            Object totalCompletedBatches = jsonObject.get("totalCompletedBatches");
            Object totalReceivedRecords = jsonObject.get("totalReceivedRecords");
            Object unprocessedBatches = jsonObject.get("unprocessedBatches");

            listDetails.add(activeJobs);//当前正在运行的job
            listDetails.add(allJobs);//所有的job
            listDetails.add(runningStages);//正在运行的 stage
            listDetails.add(waitingStages);//处于等待运行的stage
            listDetails.add(runningBatches);//正在运行的批次
            listDetails.add(totalCompletedBatches);//所有完成批次
            listDetails.add(totalReceivedRecords);//总处理数据条数
            listDetails.add(unprocessedBatches);//未处理的批次

            long currentTime = Long.parseLong(timeStamp);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
            Date date = new Date(currentTime);
            String timeFormat = formatter.format(date);
            System.out.println(timeFormat);
            map.put(timeFormat,listDetails);
            //      listTime.add(timeFormat);
        }

        JSONObject jsonObject1 = new JSONObject();
        //任务详情
        jsonObject1.put("任务详情",map);
        //最新时间
        String latestData = strings.get("MONITOR_DP"+list.get(0).toString());
        JSONObject jsonObject = new JSONObject(latestData);
        Integer  failedStages  = (Integer)jsonObject.get("failedStages");//出现错误的stage
        Integer  runningStages  =(Integer)jsonObject.get("runningStages");//正在运行的 stage
        Integer  waitingStages  =(Integer)jsonObject.get("waitingStages");//处于等待运行的stage
        Integer  runningBatches  =(Integer)jsonObject.get("runningBatches");//正在运行的批次
        Integer  waitingBatches  =(Integer)jsonObject.get("waitingBatches"); ///处于等待的批次
        Integer  totalCompletedBatches  =(Integer)jsonObject.get("totalCompletedBatches"); //所有完成批次
        Integer maxMemMB   =(Integer)jsonObject.get("maxMem_MB"); //最大内存
        Integer   memUsedMB =(Integer)jsonObject.get("memUsed_MB"); //使用内存
        Integer remainingMemMB   =(Integer)jsonObject.get("remainingMem_MB"); //闲置内存


        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);

        //stage
        Float errorRate;
        if (0==failedStages) {
            errorRate = Float.parseFloat(numberFormat.format((0)));
        }else {
            errorRate =  Float.parseFloat(numberFormat.format((float)failedStages  / ((float)failedStages +(float) runningStages + (float)waitingStages )* 100)); //错误率
        }

        Float  operatingRate;
        if (0==runningStages) {
            operatingRate=  Float.parseFloat(numberFormat.format((0)));

        }else {
            operatingRate= Float.parseFloat(numberFormat.format((float)runningStages  / ((float)failedStages +(float) runningStages + (float)waitingStages )* 100)); //正在运行率

        }
        Float  waitingRate;

        if (0==waitingStages) {

            waitingRate=  Float.parseFloat(numberFormat.format((0)));
        }else {

            waitingRate= Float.parseFloat(numberFormat.format((float)waitingStages  / ((float)failedStages +(float) runningStages + (float)waitingStages )* 100));//等待率
        }

//批次情况
        Float proportionOfBatchesInOperation;
        if (0==runningBatches) {
            proportionOfBatchesInOperation=  Float.parseFloat(numberFormat.format((0)));

        }else {

            proportionOfBatchesInOperation= Float.parseFloat(numberFormat.format((float)runningBatches  / ((float)runningBatches +(float) waitingBatches + (float)totalCompletedBatches )* 100)); //正在运行的批次占比
        }

        Float waitingBatchProportion;
        if (0==waitingBatches) {

            waitingBatchProportion=  Float.parseFloat(numberFormat.format((0)));
        }else {
            waitingBatchProportion= Float.parseFloat(numberFormat.format((float)waitingBatches  / ((float)runningBatches +(float) waitingBatches + (float)totalCompletedBatches )* 100));//等待批次占比

        }

        Float ratioOfCompletedBatches;
        if (0==totalCompletedBatches) {

            ratioOfCompletedBatches=  Float.parseFloat(numberFormat.format((0)));
        }else {

            ratioOfCompletedBatches= Float.parseFloat(numberFormat.format((float)totalCompletedBatches  / ((float)runningBatches +(float) waitingBatches + (float)totalCompletedBatches )* 100)); //所有完成批次占比
        }
        //内存
        Float memUsedMBProportion;
        if (0==totalCompletedBatches) {

            memUsedMBProportion=  Float.parseFloat(numberFormat.format((0)));
        }else {

            memUsedMBProportion= Float.parseFloat(numberFormat.format((float)memUsedMB  / ((float)memUsedMB +(float) remainingMemMB )* 100)); //使用内存占比
        }
        Float remainingMemMBProportion;
        if (0==totalCompletedBatches) {

            remainingMemMBProportion=  Float.parseFloat(numberFormat.format((0)));
        }else {

            remainingMemMBProportion= Float.parseFloat(numberFormat.format((float)remainingMemMB  / ((float)memUsedMB +(float) remainingMemMB )* 100)); //闲置内存占比
        }
        Float maxMemMBMBProportion;
        if (0==totalCompletedBatches) {

            maxMemMBMBProportion=  Float.parseFloat(numberFormat.format((0)));
        }else {

            maxMemMBMBProportion= Float.parseFloat(numberFormat.format((float)(maxMemMB  / (memUsedMB +remainingMemMB )* 100))); //最大内存占比
        }

        List list2 = new ArrayList();
        list2.add(errorRate);//错误占比
        list2.add(operatingRate);//正在运行占比
        list2.add(waitingRate);//等待占比

        List list3 = new ArrayList();
        list3.add(proportionOfBatchesInOperation);//正在运行的批次占比
        list3.add(waitingBatchProportion); //等待批次占比
        list3.add(ratioOfCompletedBatches);//所有完成批次占比

        List list4 = new ArrayList();
        list4.add(memUsedMBProportion);//使用内存占比
        list4.add(remainingMemMBProportion);//闲置内存占比
        list4.add(maxMemMBMBProportion);//最大内存占比

        //任务描述
        List list1 =  new ArrayList<>();
        list1.add(failedStages);//出现错误的stage
        list1.add(runningStages);//正在运行的 stage
        list1.add(waitingStages);//处于等待运行的stage
        list1.add(runningBatches);//正在运行的批次
        list1.add(waitingBatches);///处于等待的批次
        list1.add(totalCompletedBatches);//所有完成批次
        list1.add(maxMemMB);//最大内存
        list1.add(remainingMemMBProportion);//使用内存
        list1.add(remainingMemMB);//闲置内存

        jsonObject1.put("stageSituation",list2);//stage情况
        jsonObject1.put("batchSituation",list3);//批次情况
        jsonObject1.put("mBSituation",list4);//内存情况
        jsonObject1.put("概况",list1);
        String toString = jsonObject1.toString();
        return toString ;
    }
}


