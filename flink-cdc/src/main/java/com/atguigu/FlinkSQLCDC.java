package com.atguigu;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * FlinkCDC 用dataStream的方式可以一次读多库多表
 * FlinkSQL 只能一次读一张表
 */
public class FlinkSQLCDC {

    public static void main(String[] args) throws Exception {

        //1、构建执行环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);//表环境

        //2、使用FlinkSQL DDL模式构建CDC表
        tableEnv.executeSql("CREATE TABLE user (" +
                " id STRING primary key, " +
                " name STRING, " +
                " sex STRING " +
                ") WITH (" +
                " 'connector' = 'mysql-cdc'," +
                " 'scan.startup.mode' = 'latest-offset'," +   //启动模式
                " 'hostname' = 'localhost'," +
                " 'port' = '3306'," +
                " 'username' = 'root'," +
                " 'password' = '12345678'," +
                " 'database-name' = 'test'," +
                " 'table-name' = 'user'" +
                ")");




        //3、查询数据并转换为流输出
        Table table = tableEnv.sqlQuery("select * from user_info");
        DataStream<Tuple2<Boolean, Row>> retractStream = tableEnv.toRetractStream(table, Row.class);
        retractStream.print();

        //4、启动
        env.execute("FlinkSQLCDC");


    }
}
