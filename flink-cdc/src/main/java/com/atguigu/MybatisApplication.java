package com.atguigu;


import com.atguigu.entity.CompanyDetailEntity;
import com.atguigu.mapper.CompanyDetailMapper;
import com.google.common.cache.*;
import org.apache.commons.compress.utils.Lists;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;


import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MybatisApplication {

    @Test
    public void listUser() throws ExecutionException {


        LoadingCache<String, Map<Integer, CompanyDetailEntity>> userCache
                //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
                = CacheBuilder.newBuilder()
                //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(8)
                //设置写缓存后8秒钟过期
                .expireAfterWrite(1, TimeUnit.SECONDS)
                //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                .build(
                        new CacheLoader<String, Map<Integer, CompanyDetailEntity>>() {
                            @Override
                            public Map<Integer, CompanyDetailEntity> load(String key) throws Exception {
                                List<CompanyDetailEntity> list = getUserEntity();
                                Map<Integer, CompanyDetailEntity> collect = list.stream().collect(Collectors.toMap(input -> input.getId(), Function.identity()));
                                return collect;
                            }
                        }
                );
        System.out.println(userCache.get("user"));
    }


    private List<CompanyDetailEntity> getUserEntity(){
        List<CompanyDetailEntity> userEntities= Lists.newArrayList();
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            //构建sqlSession的工厂
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);

            SqlSession session= sessionFactory.openSession();
            CompanyDetailMapper mapper=session.getMapper(CompanyDetailMapper.class);
            userEntities = mapper.queryAll();

            session.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userEntities;
    }
}
