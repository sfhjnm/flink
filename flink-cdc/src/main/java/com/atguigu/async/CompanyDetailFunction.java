package com.atguigu.async;

import com.alibaba.fastjson.JSONObject;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.async.ResultFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CompanyDetailFunction extends JDBCAsyncFunction<Tuple2<String, JSONObject>,Tuple2<String, JSONObject>> {

    private Cache<String, HashMap<String,Object>> cache;


    public CompanyDetailFunction(){
        super("detail");
        //初始化缓存
        cache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void asyncInvoke(Tuple2<String, JSONObject> jsonObject, ResultFuture<Tuple2<String, JSONObject>> resultFuture) throws Exception {
        JSONObject f1 = jsonObject.f1;
        String company_detail_id = f1.getString("company_detail_id");
        if(StringUtils.isNotEmpty(company_detail_id)){
            Map<String, Object> map = getCache(company_detail_id);
            f1.put("address",map.get("address"));
        }
        resultFuture.complete(Collections.singleton(jsonObject));
    }

    private Map<String, Object> getCache(String company_detail_id) throws ExecutionException, InterruptedException {
        Map<String, Object> map=new HashMap<>();
        if(cache.getIfPresent(company_detail_id) != null){
            return cache.getIfPresent(company_detail_id);
        }
        return getBySql(company_detail_id);
    }

    private HashMap<String, Object> getBySql(String company_detail_id) throws ExecutionException, InterruptedException {

        QueryRunner queryRunner = createQueryRunner();
        Future<HashMap<String, Object>> future = asyncCall(new DynamicRelationTask(queryRunner, company_detail_id));

        HashMap<String, Object> map = future.get();

        if (map.size() > 0){
            cache.put(company_detail_id,map);
        }
        return map;

    }
}

class DynamicRelationTask implements Callable<HashMap<String,Object>> {

    private QueryRunner queryRunner;

    private String company_detail_id;

    public DynamicRelationTask(QueryRunner queryRunner, String uuid) {
        this.queryRunner = queryRunner;
        this.company_detail_id = uuid;
    }

    @Override
    public HashMap<String, Object> call() throws Exception {
        String sql = "select id,address from company_detail where id='"+Integer.valueOf(company_detail_id)+"'";
        HashMap<String, Object> map = new HashMap<>();
        Map<String, Object> query = queryRunner.query(sql, new MapHandler());

        if(null !=query){
            map.put("id",query.get("id"));
            map.put("address",query.get("address"));
        }
        return map;
    }


}
