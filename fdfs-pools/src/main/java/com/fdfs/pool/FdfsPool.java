package com.fdfs.pool;


import com.fdfs.pool.exception.FdfsException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description: fdfs池
 */
public class FdfsPool extends Pool<FdfsClient> {
    public FdfsPool() {
    }

    public FdfsPool(GenericObjectPoolConfig poolConfig, PooledObjectFactory<FdfsClient> factory) {
        super(poolConfig, factory);
    }

    @Override
    public FdfsClient getResource() {
        FdfsClient fastfdsClient = super.getResource();
//        fastfdsClient.setDataSource(this);
        return fastfdsClient;
    }


    @Override
    public void returnResource(final FdfsClient resource) {
        if (resource != null) {
            try {
                super.returnResource(resource);
            } catch (Exception e) {
                returnBrokenResource(resource);
                throw new FdfsException("Could not return the resource to the pool", e);
            }
        }
    }

}