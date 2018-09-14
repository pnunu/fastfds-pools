package com.fdfs.pool.factory;

import com.fdfs.pool.FdfsClient;
import com.fdfs.pool.exception.FdfsConnectionException;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description:
 *
 * @see org.apache.commons.pool2.BasePooledObjectFactory
 */
public class FdfsClientFactory implements PooledObjectFactory<FdfsClient> {

    public FdfsClientFactory() {
        /*String classPath = new File(super.getClass().getResource("/").getFile()).getCanonicalPath();
        String configFilePath = classPath + File.separator + "fdfs_client.conf";
        ClientGlobal.init(configFilePath);*/
    }

    public FdfsClientFactory(String configFilePath) throws IOException, MyException {
        ClientGlobal.init(configFilePath);
    }

    public FdfsClientFactory(final URI configFileUri) throws IOException, MyException {
        ClientGlobal.init(configFileUri.toURL().getFile());
    }

    public PooledObject<FdfsClient> makeObject() throws Exception {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        if (trackerServer == null || trackerServer.getSocket() == null || trackerServer.getSocket().isClosed()) {
            //null for fail
            throw new FdfsConnectionException("connect to server fail");
        }
        StorageServer storageServer = null;
        FdfsClient client = new FdfsClient(trackerServer, storageServer);
        return new DefaultPooledObject<FdfsClient>(client);
    }

    public void destroyObject(PooledObject<FdfsClient> pooledFdfsClient)
            throws Exception {
        final FdfsClient fastfdsClient = pooledFdfsClient.getObject();
        if (fastfdsClient == null) {
            return;
        }
        fastfdsClient.close();
    }

    public boolean validateObject(PooledObject<FdfsClient> pooledFdfsClient) {
        final FdfsClient fastfdsClient = pooledFdfsClient.getObject();
        if (fastfdsClient != null && fastfdsClient.getTrackerServer() != null) {
            try {
                return ProtoCommon.activeTest(fastfdsClient.trackerServer.getSocket());
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void activateObject(PooledObject<FdfsClient> pooledFdfsClient) throws Exception {
        FdfsClient fdfsClient = pooledFdfsClient.getObject();
        if (fdfsClient.isClose()) {
            fdfsClient.getTrackerServer().getSocket();
            fdfsClient.use();
        } else if (fdfsClient.isUseTimeOut()) {
            if(!validateObject(pooledFdfsClient)){
                try {
                    fdfsClient.getTrackerServer().close();
                }catch (Exception e){
//                e.printStackTrace();
                }
                fdfsClient.getTrackerServer().getSocket();
            }
            fdfsClient.use();
        }
    }

    @Override
    public void passivateObject(PooledObject<FdfsClient> pooledFdfsClient) throws Exception {

    }
}
