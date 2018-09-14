package com.fdfs.pool;

import com.fdfs.pool.exception.FdfsConnectionException;
import com.fdfs.pool.exception.FdfsException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.ClientGlobal;

import java.io.Closeable;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description:连接池
 */
public abstract class Pool<T> implements Closeable {

    protected GenericObjectPool<T> internalPool;

    /**
     * Using this constructor means you have to set and initialize the internalPool yourself.
     */
    public Pool() {
    }

    public Pool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
        initPool(poolConfig, factory);
    }

    public void initPool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {

        if (this.internalPool != null) {
            try {
                closeInternalPool();
            } catch (Exception e) {
            }
        }

        this.internalPool = new GenericObjectPool<T>(factory, poolConfig);
        this.addObjects(ClientGlobal.g_tracker_group.tracker_servers.length);
    }

    public T getResource() {
        try {
            return this.internalPool.borrowObject();
        } catch (Exception e) {
            throw new FdfsConnectionException("Could not get a resource from the pool", e);
        }
    }

    public void returnResource(final T resource) {
        if (resource != null) {
            if (resource == null) {
                return;
            }
            try {
                this.internalPool.returnObject(resource);
            } catch (Exception e) {
                throw new FdfsException("Could not return the resource to the pool", e);
            }
        }
    }

    public void returnBrokenResource(final T resource) {
        if (resource != null) {
            try {
                this.internalPool.invalidateObject(resource);
            } catch (Exception e) {
                throw new FdfsException("Could not return the resource to the pool", e);
            }
        }
    }

    public void destroy() {
        closeInternalPool();
    }

    @Override
    public void close() {
        destroy();
    }

    public boolean isClosed() {
        return this.internalPool.isClosed();
    }

    protected void closeInternalPool() {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new FdfsException("Could not destroy the pool", e);
        }
    }

    public int getNumActive() {
        if (poolInactive()) {
            return -1;
        }

        return this.internalPool.getNumActive();
    }

    public int getNumIdle() {
        if (poolInactive()) {
            return -1;
        }

        return this.internalPool.getNumIdle();
    }

    public int getNumWaiters() {
        if (poolInactive()) {
            return -1;
        }

        return this.internalPool.getNumWaiters();
    }

    public long getMeanBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }

        return this.internalPool.getMeanBorrowWaitTimeMillis();
    }

    public long getMaxBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }

        return this.internalPool.getMaxBorrowWaitTimeMillis();
    }

    private boolean poolInactive() {
        return this.internalPool == null || this.internalPool.isClosed();
    }

    public void addObjects(int count) {
        try {
            for (int i = 0; i < count; i++) {
                this.internalPool.addObject();
            }
        } catch (Exception e) {
            throw new FdfsException("Error trying to add idle objects", e);
        }
    }
}