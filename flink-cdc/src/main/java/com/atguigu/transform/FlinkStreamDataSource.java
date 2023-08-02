package com.atguigu.transform;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.serialize.CdcDeserializationSchema;
import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkStreamDataSource {


    public static SingleOutputStreamOperator<Tuple2<String, JSONObject>> singleOutputStreamOperator(StreamExecutionEnvironment env, String tableName, String key){
        DebeziumSourceFunction<JSONObject> build = MySqlSource.<JSONObject>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("test") // set captured database
                .tableList(tableName) // 必须加上库名
                .username("root")
                .password("12345678")
                .deserializer(new CdcDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();
        DataStreamSource<JSONObject> dataStreamSource = env.addSource(build);

        SingleOutputStreamOperator<Tuple2<String, JSONObject>> map = dataStreamSource.map(new MapFunction<JSONObject, Tuple2<String, JSONObject>>() {
            @Override
            public Tuple2<String, JSONObject> map(JSONObject jsonObject) throws Exception {
                String id = jsonObject.getString(key);
                return Tuple2.of(id, jsonObject);
            }
        });
        return map;
    }

    public static DataStreamSource<JSONObject> dataStreamSource(StreamExecutionEnvironment env, String tableName, String key){
        DebeziumSourceFunction<JSONObject> build = MySqlSource.<JSONObject>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("test") // set captured database
                .tableList(tableName) // 必须加上库名
                .username("root")
                .password("12345678")
                .deserializer(new CdcDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();
        DataStreamSource<JSONObject> dataStreamSource = env.addSource(build);

        return dataStreamSource;
    }
}
