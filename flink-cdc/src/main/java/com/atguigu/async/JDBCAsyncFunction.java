package com.atguigu.async;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class JDBCAsyncFunction<IN, OUT> extends RichAsyncFunction<IN, OUT> {

    private static final int DEFAULT_CAPACITY = 20;

    /**
     * 数据源配置文件路径
     * */
    private String _path;

    /**
     * 队列大小
     * */
    private int _capacity;

    /**
     * 数据源
     * */
    private HikariDataSource _datasource;

    /**
     * 线程池
     * */
    private ExecutorService _executor;

    public JDBCAsyncFunction(String path) {
        this(path, DEFAULT_CAPACITY);
    }

    public JDBCAsyncFunction(String path, int capacity) {
        this._path = path;
        this._capacity = capacity;
    }

    protected <V> Future<V> asyncCall(Callable<V> call) {
        return this._executor.submit(call);
    }

    protected QueryRunner createQueryRunner() {
        return new QueryRunner(this._datasource);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        // 初始化 JDBC 连接池
        if (this._datasource == null) {
            ResourceBundle rb = ResourceBundle.getBundle("mysql");
            String driver = rb.getString("driver");

            String insUrl = rb.getString(_path+".url");
            String insUsername = rb.getString(_path+".username");
            String insPassword = rb.getString(_path+".password");

            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(insUrl);
            config.setUsername(insUsername);
            config.setPassword(insPassword);
            config.setDriverClassName(driver);
            config.setConnectionInitSql("select 1");
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setMinimumIdle(10);
            config.setMaximumPoolSize(this._capacity);

            this._datasource = new HikariDataSource(config);
        }

        // 初始化线程池
        if (this._executor == null) {
            this._executor = Executors.newFixedThreadPool(this._capacity);
        }
    }

    @Override
    public void close() throws Exception {
        if (this._executor != null) {
            this._executor.shutdown();
            this._executor = null;
        }

        if (this._datasource != null) {
            this._datasource.close();
            this._datasource = null;
        }

        super.close();
    }

}
