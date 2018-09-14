package com.fdfs.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description: fdfs连接池配置
 */
public class FdfsPoolConfig extends GenericObjectPoolConfig {

    public FdfsPoolConfig() {
//        setNumTestsPerEvictionRun(-1);
        setNumTestsPerEvictionRun(Integer.MAX_VALUE); // always test all idle objects
        setTimeBetweenEvictionRunsMillis(5 * 60000L); //-1不启动。默认5min一次
        setMinEvictableIdleTimeMillis(10 * 60000L); //可发呆的时间,10mins
        setTestWhileIdle(false);  //发呆过长移除的时候是否test一下先
    }

}