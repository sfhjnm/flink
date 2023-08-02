package com.atguigu;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.async.CompanyDetailFunction;
import com.atguigu.async.MyJoinFunction;
import com.atguigu.serialize.CdcDeserializationSchema;
import com.atguigu.transform.FlinkStreamDataSource;
import com.atguigu.watermark.JoinDataStreamWatermarks;
import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;


import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


/**
 * DataStream 和FlinkSQL 模式对比
 *
 * 1、DataStream 1.12和1.13两个版本都可用
 *   FlinkSQL只能在1.13版本可用
 *
 * 2、DataStream可以监控多库多表
 *   FlinkSQL只能监控单表
 *
 * 3、DataStream 需要自定义反序列化器】
 *   FlinkSQL不需要
 */

/**
 *
 //2.Flink-CDC 将读取 binlog 的位置信息以状态的方式保存在 CK,如果想要做到断点续传,需要从 Checkpoint 或者 Savepoint 启动程序
 //        env.enableCheckpointing(5000);//开启 Checkpoint,每隔 5 秒钟做一次 CK
 //        env.getCheckpointConfig().setCheckpointTimeout(10000);
 //        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);//指定 CK 的一致性语义
 //        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
 //
 //        env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8082/cdc-test/ck"));//设置状态后端
 */
public class FlinkCDC {

    public static void main(String[] args) throws Exception {
        //获取Flink执行环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        //设置事件时间，每个事件在其生产设备上发生的时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //watermark 自动添加水印调度时间
        env.getConfig().setAutoWatermarkInterval(200);

        DataStreamSource<JSONObject> companyStream = FlinkStreamDataSource
                .dataStreamSource(env, "test.company", "company_detail_id");
        DataStreamSource<JSONObject> companyDetailStream = FlinkStreamDataSource
                .dataStreamSource(env, "test.company_detail", "id");


//        DataStream<JSONObject> apply = companyStream.join(companyDetailStream)
//                .where(new KeySelector<Tuple2<String, JSONObject>, Object>() {
//                    @Override
//                    public Object getKey(Tuple2<String, JSONObject> value) throws Exception {
//                        return value.f0;
//                    }
//                })
//                .equalTo(new KeySelector<Tuple2<String, JSONObject>, Object>() {
//                    @Override
//                    public Object getKey(Tuple2<String, JSONObject> value) throws Exception {
//                        return value.f0;
//                    }
//                })
//                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
//                .apply(new JoinFunction<Tuple2<String, JSONObject>, Tuple2<String, JSONObject>, JSONObject>() {
//                    @Override
//                    public JSONObject join(Tuple2<String, JSONObject> value1, Tuple2<String, JSONObject> value2) throws Exception {
//                        JSONObject f1 = value2.f1;
//                        JSONObject f11 = value1.f1;
//                        f11.put("address", f1.getString("address"));
//                        return f11;
//                    }
//                });
//        apply.print();

        SingleOutputStreamOperator<JSONObject> streamOperator = companyStream.assignTimestampsAndWatermarks(WatermarkStrategy.<JSONObject>forMonotonousTimestamps()
                .withTimestampAssigner((jsonObject, l) -> {
                    String ts = jsonObject.getString("ts");
                    return Long.valueOf(ts);
                }));
        SingleOutputStreamOperator<JSONObject> streamDetailOperator = companyDetailStream.assignTimestampsAndWatermarks(WatermarkStrategy.<JSONObject>forMonotonousTimestamps()
                .withTimestampAssigner((jsonObject, l) -> {
                    String ts = jsonObject.getString("ts");
                    return Long.valueOf(ts);
                }));


// 将两个数据流进行关联
        streamOperator
                .keyBy(row -> Long.valueOf(row.getString("ts"))) // 假设A数据流的第一个字段是用来关联B数据流的字段
                .intervalJoin(streamDetailOperator.keyBy(row -> Long.valueOf(row.getString("ts")))) // 使用intervalJoin算子进行关联
                .between(Time.seconds(-5), Time.seconds(5)) // 设置匹配窗口
                .process(new MyJoinFunction()); // 自定义处理函数

//        DataStream<Object> apply1 = companyStream.coGroup(companyDetailStream)
//                .where(new KeySelector<Tuple2<String, JSONObject>, Object>() {
//                    @Override
//                    public Object getKey(Tuple2<String, JSONObject> value) throws Exception {
//                        return value.f0;
//                    }
//                })
//                .equalTo(new KeySelector<Tuple2<String, JSONObject>, Object>() {
//                    @Override
//                    public Object getKey(Tuple2<String, JSONObject> value) throws Exception {
//                        return value.f0;
//                    }
//                })
//                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
//                .apply(new CoGroupFunction<Tuple2<String, JSONObject>, Tuple2<String, JSONObject>, Object>() {
//                    @Override
//                    public void coGroup(Iterable<Tuple2<String, JSONObject>> value1, Iterable<Tuple2<String, JSONObject>> value2, Collector<Object> collector) throws Exception {
//                        JSONObject jsonObject = null;
//                        for (Tuple2<String, JSONObject> tuple2 : value1) {
//                            jsonObject = tuple2.f1;
//                        }
//                        for (Tuple2<String, JSONObject> tuple2 : value2) {
//                            JSONObject f1 = tuple2.f1;
//                            jsonObject.put("address", f1.getString("address"));
//                        }
//                        collector.collect(jsonObject);
//                    }
//                });
//        apply1.print();



        //打印
        //dataStreamSource.print();
        //执行任务
        env.execute("FlinkCDC");
    }


}
