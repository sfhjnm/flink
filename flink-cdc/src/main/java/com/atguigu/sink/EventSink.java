package com.atguigu.sink;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class EventSink extends RichSinkFunction<JSONObject> {


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void invoke(JSONObject jsonObject, Context context){
        System.out.println("sink----"+jsonObject);

    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
