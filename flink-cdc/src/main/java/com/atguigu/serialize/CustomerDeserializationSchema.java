package com.atguigu.serialize;



import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

public class CustomerDeserializationSchema implements DebeziumDeserializationSchema<String> {

    /**
     * {
     *     "db":"",
     *     "tableName":"",
     *     "before":{"id":"1001","name":""...}
     *     "after":{"id":"1001","name":""...}
     *     "op": ""
     * }
     */


    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
        //创建JSON对象用于封装结果数据
        JSONObject result = new JSONObject();

        //获取库名&表名
        String topic = sourceRecord.topic();

        Struct valueStruct = (Struct)sourceRecord.value();
        Struct sourceStruct = valueStruct.getStruct("source");
        String database = sourceStruct.getString("db");
        String table = sourceStruct.getString("table");
        result.put("db",database);
        result.put("tabelName",table);

        Struct value = (Struct) sourceRecord.value();
        Struct before = value.getStruct("before");
        JSONObject beforeObject = new JSONObject();
        if(before != null){
            //获取列信息
            Schema schema = before.schema();
            for (Field field : schema.fields()) {
                beforeObject.put(field.name() , before.get(field));
            }
        }
        result.put("before",beforeObject);


        Struct after = value.getStruct("after");
        JSONObject afterObject = new JSONObject();
        if(after != null){
            //获取列信息
            Schema schema = after.schema();
            for (Field field : schema.fields()) {
                afterObject.put(field.name() , after.get(field));
            }
        }
        result.put("after",afterObject);

        //获取操作类型
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        result.put("op",operation);

        //输出数据
        collector.collect(result.toString());
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
