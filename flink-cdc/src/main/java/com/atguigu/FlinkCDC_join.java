package com.atguigu;

import com.alibaba.fastjson.JSONObject;

import com.atguigu.async.CompanyDetailFunction;
import com.atguigu.entity.CompanyDetailEntity;
import com.atguigu.mapper.CompanyDetailMapper;
import com.atguigu.serialize.CdcDeserializationSchema;
import com.atguigu.sink.EventSink;
import com.atguigu.transform.FlinkStreamDataSource;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import org.apache.commons.compress.utils.Lists;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


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
public class FlinkCDC_join {

    public static void main(String[] args) throws Exception {
        //获取Flink执行环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        //设置重启策略
        //env.setRestartStrategy(RestartStrategies.noRestart());
        //设置事件时间，每个事件在其生产设备上发生的时间
        //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //获取用户流
        SingleOutputStreamOperator<Tuple2<String, JSONObject>> userOutputStream = FlinkStreamDataSource.
                singleOutputStreamOperator(env, "test.user","company_id");

        //获取公司流
        SingleOutputStreamOperator<Tuple2<String, JSONObject>> companyOutputStream =FlinkStreamDataSource.
                singleOutputStreamOperator(env, "test.company","id");

        //获取公司和公司详情流
        SingleOutputStreamOperator<Tuple2<String, JSONObject>> companyAndDetailStream = AsyncDataStream
                .unorderedWait(companyOutputStream,
                        new CompanyDetailFunction(),
                        20,
                        TimeUnit.MINUTES,
                        16).name("user" + "query");
        companyAndDetailStream.print();

        //双流join
        DataStream<JSONObject> apply = userOutputStream.join(companyAndDetailStream)
                .where(new KeySelector<Tuple2<String, JSONObject>, Object>() {
                    @Override
                    public Object getKey(Tuple2<String, JSONObject> value) throws Exception {
                        return value.f0;
                    }
                })
                .equalTo(new KeySelector<Tuple2<String, JSONObject>, Object>() {
                    @Override
                    public Object getKey(Tuple2<String, JSONObject> value) throws Exception {
                        return value.f0;
                    }
                })
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .apply(new JoinFunction<Tuple2<String, JSONObject>, Tuple2<String, JSONObject>, JSONObject>() {
                    @Override
                    public JSONObject join(Tuple2<String, JSONObject> user, Tuple2<String, JSONObject> companyAndDetail) throws Exception {
                        JSONObject userJsonObject = user.f1;
                        JSONObject companyAndDetailJsonObject = companyAndDetail.f1;

                        userJsonObject.put("company",companyAndDetailJsonObject.getString("company"));
                        userJsonObject.put("address",companyAndDetailJsonObject.getString("address"));
                        return userJsonObject;
                    }
                });
        apply.addSink(new EventSink()).name("event");

        //执行任务
        env.execute("FlinkCDC");
    }





}
