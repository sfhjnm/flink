package com.atguigu.async;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.util.Collector;

public class MyJoinFunction extends ProcessJoinFunction<JSONObject, JSONObject, JSONObject> {


    @Override
    public void processElement(JSONObject left, JSONObject right, ProcessJoinFunction<JSONObject, JSONObject, JSONObject>.Context context, Collector<JSONObject> collector) throws Exception {
        System.out.println("company--"+left);
        System.out.println("companyDetail--"+right);
    }
}
