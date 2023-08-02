package com.atguigu.watermark;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DataStreamWatermark implements AssignerWithPeriodicWatermarks<JSONObject> {
    
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(System.currentTimeMillis() - 5000);
    }

    @Override
    public long extractTimestamp(JSONObject s, long l) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long newEventTimestamp;
        try {
            // 获取当前时间整分钟的时间戳，yyyy-MM-dd HH:mm:00
            String baseTimeStringType = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + ":00";
            Date parse = simpleDateFormat.parse(baseTimeStringType);
            Long time = parse.getTime(); //1679554920000
            //事件延迟的毫秒数
            String ts1 = s.getString("ts"); //1679554779401
            String format = simpleDateFormat.format(new Date(Long.valueOf(ts1))); //2023-03-23 14:59:39
            Date ts = simpleDateFormat.parse(format);
            Calendar newTime = Calendar.getInstance();
            newTime.setTime(ts);
            newTime.add(Calendar.SECOND,10);//日期加10秒
            Date dt1=newTime.getTime();
            String newTs = simpleDateFormat.format(dt1); //2023-03-23 14:59:49

            //事件产生时间
            Date date = simpleDateFormat.parse(newTs);
            newEventTimestamp = date.getTime();//1679555129000
            System.out.println("事件产生时间---" + newEventTimestamp);



        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return newEventTimestamp;
    }


}
