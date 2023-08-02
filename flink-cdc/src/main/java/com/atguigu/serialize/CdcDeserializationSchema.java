package com.atguigu.serialize;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.source.SourceRecord;

import org.apache.kafka.connect.data.Struct;

import java.util.List;

public class CdcDeserializationSchema implements DebeziumDeserializationSchema<JSONObject> {


    public CdcDeserializationSchema() {
    }


    @Override
    public void deserialize(SourceRecord record, Collector<JSONObject> collector) throws Exception {
        Struct dataRecord  =  (Struct)record.value();

        Struct afterStruct = dataRecord.getStruct("after");
        Struct beforeStruct = dataRecord.getStruct("before");
        /*
          todo 1，同时存在 beforeStruct 跟 afterStruct数据的话，就代表是update的数据
               2,只存在 beforeStruct 就是delete数据
               3，只存在 afterStruct数据 就是insert数据
         */
        JSONObject logJson = new JSONObject();

        String op = "";
        List<Field> fieldsList = null;
        if(afterStruct !=null && beforeStruct !=null){
            op = "update";
            fieldsList = afterStruct.schema().fields();
            //字段与值
            for (Field field : fieldsList) {
                String fieldName = field.name();
                Object fieldValue = afterStruct.get(fieldName);
                logJson.put(fieldName,fieldValue);
            }
        }else if (afterStruct !=null){
            op = "insert";
            fieldsList = afterStruct.schema().fields();
            //字段与值
            for (Field field : fieldsList) {
                String fieldName = field.name();
                Object fieldValue = afterStruct.get(fieldName);
                logJson.put(fieldName,fieldValue);
            }
        }else if (beforeStruct !=null){
            op = "detele";
            fieldsList = beforeStruct.schema().fields();
            //字段与值
            for (Field field : fieldsList) {
                String fieldName = field.name();
                Object fieldValue = beforeStruct.get(fieldName);
                logJson.put(fieldName,fieldValue);
            }
        }

        //拿到databases table信息
        Struct source = dataRecord.getStruct("source");
        Object db = source.get("db");
        Object table = source.get("table");
        Object ts_ms = source.get("ts_ms");

        logJson.put("db",db);
        logJson.put("tabelName",table);
        logJson.put("ts",ts_ms);
        logJson.put("op",op);

//        //主键字段
//        Struct pk = (Struct)record.key();
//        List<Field> pkFieldList = pk.schema().fields();
//        int partitionerNum = 0 ;
//        for (Field field : pkFieldList) {
//            Object pkValue= pk.get(field.name());
//            partitionerNum += pkValue.hashCode();
//
//        }
//        int hash = Math.abs(partitionerNum) % 3;
//        logJson.put("id",hash);
        collector.collect(logJson);
    }

    @Override
    public TypeInformation<JSONObject> getProducedType() {
        return BasicTypeInfo.of(JSONObject.class);
    }
}
