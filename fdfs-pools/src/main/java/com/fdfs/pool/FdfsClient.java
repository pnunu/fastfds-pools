package com.fdfs.pool;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description:
 */
public class FdfsClient extends StorageClient1 {
    public TrackerServer trackerServer;
    public StorageServer storageServer;

    private volatile boolean close = false;

    private volatile long lastUseTime = System.currentTimeMillis();

    public FdfsClient() {
    }

    public FdfsClient(TrackerServer trackerServer, StorageServer storageServer) {
        super(trackerServer, storageServer);
        this.trackerServer = trackerServer;
        this.storageServer = storageServer;
    }

    public TrackerServer getTrackerServer() {
        return this.trackerServer;
    }

    public StorageServer getStorageServer() {
        return this.storageServer;
    }


    public boolean isUseTimeOut() {
        return System.currentTimeMillis() >= (lastUseTime + ClientGlobal.getG_network_timeout());
    }

    public void use() {
        close = false;
        this.lastUseTime = System.currentTimeMillis();
    }

    public boolean isClose() {
        return close;
    }

    public synchronized void close() {
        if (storageServer != null) {
            try {
                storageServer.close();
            } catch (IOException e) {
            }
        }
        if (trackerServer != null) {
            try {
                trackerServer.close();
            } catch (IOException e) {
            }
        }
        close = true;
    }
}