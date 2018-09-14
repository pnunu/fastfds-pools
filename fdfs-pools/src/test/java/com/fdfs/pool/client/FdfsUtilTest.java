package com.fdfs.pool.client;

import com.fdfs.pool.FdfsPool;
import com.fdfs.pool.base.BaseSpringTest;
import com.fdfs.util.FdfsUtil;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/3/12
 * Description:
 */
public class FdfsUtilTest extends BaseSpringTest {


    @Autowired
    private FdfsPool fdfsPool;

    @Test
    public void testDelete(){
        String fileId = "group1/M00/00/0A/wKjwCldSvzWASFkUAAACAXo-UOE395.txt";
        NameValuePair[] metadatas = FdfsUtil.getMetadatas(fileId);
        byte[] bytes = FdfsUtil.download(fileId);
        System.out.println(bytes != null ? bytes.length : 0);
        boolean del = FdfsUtil.deleteFile(fileId);
        System.out.println("del="+del);
        bytes = FdfsUtil.download(fileId);
        System.out.println(bytes != null ? bytes.length : 0);
    }

    @Test
    public void testUpload() {
        System.out.println("--------------start upload--------------");
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 1; i++) {
            try{
                NameValuePair[] metaList = new NameValuePair[2];
                metaList[0] = new NameValuePair("属性1", "1111");
                metaList[1] = new NameValuePair("属性2", "2222");
                String fileId = FdfsUtil.uploadFile(new byte[i], "txt", null, metaList);
                System.out.println(fileId);
                /*System.out.println(Thread.currentThread().getName() + " active=" + fdfsPool.getNumActive() +
                        " idle=" + fdfsPool.getNumIdle() +
                        " wait=" + fdfsPool.getNumWaiters() +
                        " fileId=" + fileId
                );*/
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        long endTime = System.currentTimeMillis();
        System.out.println("cost times >> "+(endTime - startTime));
        /*try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        System.out.println(Thread.currentThread().getName() + " active=" + fdfsPool.getNumActive() +
                        " idle=" + fdfsPool.getNumIdle() +
                        " wait=" + fdfsPool.getNumWaiters()
        );
    }

    @Test
    public void testGetMateInfo(){
        String fileId = "group1/M00/00/00/wKgBgltRXG2AMugWAAAAAdIC740108.txt";
        NameValuePair[] metadatas = FdfsUtil.getMetadatas(fileId);
        System.out.println(metadatas);
        for (NameValuePair nameValuePair: metadatas) {
            System.out.println(nameValuePair.getName() + nameValuePair.getValue());
        }
    }

    @Test
    public void testUploadNotUsePool() throws IOException, MyException {

        System.out.println("--------------start upload not use pool --------------");
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 100; i++) {
            String fileId = uploadFile(new byte[i], "txt");
            /*System.out.println(fileId);
            System.out.println(Thread.currentThread().getName() + " active=" + fdfsPool.getNumActive() +
                            " idle=" + fdfsPool.getNumIdle() +
                            " wait=" + fdfsPool.getNumWaiters() +
                            " fileId=" + fileId
            );*/
        }
        long endTime = System.currentTimeMillis();
        System.out.println("=============cost times >> "+(endTime - startTime));

        System.out.println(Thread.currentThread().getName() + " active=" + fdfsPool.getNumActive() +
                        " idle=" + fdfsPool.getNumIdle() +
                        " wait=" + fdfsPool.getNumWaiters()
        );
    }

    private String uploadFile(byte[] fileBuff, String fileExtName) throws IOException, MyException {
        //建立连接
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);

        //设置元信息
        NameValuePair[] metaList = null;
       /*
       metaList = new NameValuePair[3];
       metaList[0] = new NameValuePair("fileName", "tempFileName");
        metaList[1] = new NameValuePair("fileExtName", "fileExtName");
        metaList[2] = new NameValuePair("fileLength", String.valueOf(i));*/

        //上传文件
        return client.upload_file1(fileBuff, fileExtName, metaList);
    }

    @Test
    public void testUploadInThread() {
        System.out.println("--------------start upload not use pool --------------");
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 100; i++) {
            final int tempIndex = i;
            TestThread testThread = new TestThread(fdfsPool, new Call() {
                @Override
                public void invoke() {
                    try{
                        String fileId = FdfsUtil.uploadFile(new byte[tempIndex], "txt");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    /*System.out.println(Thread.currentThread().getName() + " active=" + fdfsPool.getNumActive() +
                                    " idle=" + fdfsPool.getNumIdle() +
                                    " wait=" + fdfsPool.getNumWaiters() +
                                    " fileId=" + fileId
                    );*/
                }
            });
            try {
                testThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            testThread.start();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("=============cost times >> "+(endTime - startTime));

        System.out.println(Thread.currentThread().getName() + " active=" + fdfsPool.getNumActive() +
                        " idle=" + fdfsPool.getNumIdle() +
                        " wait=" + fdfsPool.getNumWaiters()
        );
    }

    interface Call {
        public void invoke();
    }

    class TestThread extends Thread {

        FdfsPool fdfsPool;
        Call call;

        public TestThread(FdfsPool fdfsPool, Call call) {
            this.fdfsPool = fdfsPool;
            this.call = call;
        }

        /** */
        /**
         * 线程的run方法，它将和其他线程同时运行
         */
        @Override
        public void run() {
            call.invoke();
        }
    }
}
