package com.atguigu;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.sink.EventSink;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EventMessage {

    public void run(DataStreamSource<JSONObject> dataStreamSource){
        HashMap<String, List<JSONObject>> map=new HashMap<>();
        //return transform(map, dataStreamSource);
        SingleOutputStreamOperator<JSONObject> transform = transform(dataStreamSource);
        transform.addSink(new EventSink());

    }

    private SingleOutputStreamOperator<JSONObject> transform(DataStreamSource<JSONObject> dataStreamSource ){

        SingleOutputStreamOperator<JSONObject> mp1a = dataStreamSource.map(new MapFunction<JSONObject, JSONObject>() {

            @Override
            public JSONObject map(JSONObject jsonObject) throws Exception {
                JSONObject jsonObject2 = new JSONObject();
                if ("tableName".equals("user")) {
                    JSONObject jsonObject1 = new JSONObject();
                    String id = jsonObject1.getString("id");
                    jsonObject1.put("id",id );
                    jsonObject1.put("name", jsonObject1.getString("name"));
                    jsonObject1.put("sex", jsonObject1.getString("sex"));
                    jsonObject1.put("startTime", jsonObject1.getString("startTime"));

//                    List<JSONObject> users = map.get("user");
//                    if (users.size() > 0) {
//                        users.add(jsonObject1);
//                    } else {
//                        map.put("user", Arrays.asList(jsonObject1));
//                    }
                    jsonObject2.put("user_"+id,jsonObject1);
                }
                if ("tableName".equals("company")) {
                    JSONObject jsonObject1 = new JSONObject();
                    String id = jsonObject1.getString("id");
                    jsonObject1.put("user_id", jsonObject1.getString("user_id"));
                    jsonObject1.put("company", jsonObject1.getString("company"));


//                    List<JSONObject> companys = map.get("company");
//                    if (companys.size() > 0) {
//                        companys.add(jsonObject1);
//                    } else {
//                        map.put("company", Arrays.asList(jsonObject1));
//                    }
                    jsonObject2.put("company_"+id,jsonObject1);
                }
                return jsonObject2;
            }
        });
        return mp1a;
    }
}
