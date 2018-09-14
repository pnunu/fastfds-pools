package com.fdfs.util;

import com.fdfs.pool.FdfsClient;
import com.fdfs.pool.FdfsPool;
import com.fdfs.pool.exception.FdfsException;
import org.csource.common.NameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description:fdfs工具类
 */
public class FdfsUtil {

    private static FdfsPool fdfsPool;

    protected static final String MATE_INFO_FILE_NAME = "filename";

    private static final NameValuePair[] EMPTY_ARR = new NameValuePair[0];

    public static String uploadFile(File file) throws FdfsException, FileNotFoundException {
        return uploadFile(file, getFileExtName(file.getName()), file.getName());
    }

    public static String uploadFile(File file, NameValuePair[] metaList) throws FdfsException, FileNotFoundException {
        return uploadFile(file, getFileExtName(file.getName()), file.getName(), metaList);
    }

    public static String uploadFile(File file, String suffix) throws FdfsException, FileNotFoundException {
        return uploadFile(file, suffix, file.getName());
    }

    public static String uploadFile(File file, String suffix, NameValuePair[] metaList) throws FdfsException, FileNotFoundException {
        return uploadFile(file, suffix, file.getName(), metaList);
    }

    public static String uploadFile(File file, String suffix, String fileName) throws FdfsException, FileNotFoundException {
        byte[] fileBuff = getFileBuffer(file);
        return uploadFile(fileBuff, suffix, fileName);
    }

    public static String uploadFile(File file, String suffix, String fileName, NameValuePair[] metaList) throws FdfsException, FileNotFoundException {
        byte[] fileBuff = getFileBuffer(file);
        return uploadFile(fileBuff, suffix, fileName, metaList);
    }

    public static String uploadFile(byte[] fileBuff, String suffix)
            throws FdfsException {
        return upload(fileBuff, suffix, null);
    }

    public static String uploadFile(byte[] fileBuff, String suffix, NameValuePair[] metaList)
            throws FdfsException {
        return upload(fileBuff, suffix, metaList);
    }



    public static String uploadFile(byte[] fileBuff, String suffix, String fileName)
            throws FdfsException {
        NameValuePair[] metaList = null;
        if (fileName != null) {
            metaList = new NameValuePair[1];
            metaList[0] = new NameValuePair(MATE_INFO_FILE_NAME, fileName);
        }
        return upload(fileBuff, suffix, metaList);
    }

    public static String uploadFile(byte[] fileBuff, String suffix, String fileName, NameValuePair[] metaList)
            throws FdfsException {

        NameValuePair[] newMetaList = null;
        if (fileName != null && metaList != null) {
            newMetaList = new NameValuePair[metaList.length + 1];
            System.arraycopy(metaList, 0, newMetaList, 0, metaList.length);
            newMetaList[metaList.length] = new NameValuePair(MATE_INFO_FILE_NAME, fileName);
        } else if (fileName != null) {
            newMetaList = new NameValuePair[1];
            newMetaList[0] = new NameValuePair(MATE_INFO_FILE_NAME, fileName);
        } else {
            newMetaList = metaList;
        }
        return upload(fileBuff, suffix, newMetaList);
    }

    private static String upload(byte[] fileBuff, String fileExtName, NameValuePair[] metaList)
            throws FdfsException {
        String upPath = null;
        boolean ex = false;
        FdfsClient resource = fdfsPool.getResource();
        try {
            upPath = resource.upload_file1(fileBuff, fileExtName, metaList);
        } catch (Exception e) {
            ex = true;
            throw ExceptionUtil.translate(e);
        } finally {
            if (ex) {
                fdfsPool.returnBrokenResource(resource);
                if (!resource.isClose()) {
                    resource.close();
                }
            } else {
                fdfsPool.returnResource(resource);
            }
        }
        return upPath;
    }

    public static String getFileExtName(String name) {
        String extName = null;
        if (name != null && name.contains(".")) {
            extName = name.substring(name.lastIndexOf(".") + 1);
        }
        return extName;
    }

    public static byte[] getFileBuffer(File file) throws FileNotFoundException {
        byte[] fileByte = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fileByte = new byte[fis.available()];
            fis.read(fileByte);
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            throw e;
        } catch (IOException e) {
//            e.printStackTrace();
            throw ExceptionUtil.translate(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw ExceptionUtil.translate(e);
                }
            }
        }
        return fileByte;
    }

    public static boolean deleteFile(String fileId) {
        boolean result = false;
        boolean ex = false;
        FdfsClient resource = fdfsPool.getResource();
        try {
            result = resource.delete_file1(fileId) == 0 ? true : false;
        } catch (Exception e) {
            ex = true;
            throw ExceptionUtil.translate(e);
        } finally {
            if (ex) {
                fdfsPool.returnBrokenResource(resource);
                if (!resource.isClose()) {
                    resource.close();
                }
            } else {
                fdfsPool.returnResource(resource);
            }
        }
        return result;
    }

    public static byte[] download(String fileId) {
        byte[] result = null;
        boolean ex = false;
        FdfsClient resource = fdfsPool.getResource();
        try {
            result = resource.download_file1(fileId);
        } catch (Exception e) {
            ex = true;
            throw ExceptionUtil.translate(e);
        } finally {
            if (ex) {
                fdfsPool.returnBrokenResource(resource);
                if (!resource.isClose()) {
                    resource.close();
                }
            } else {
                fdfsPool.returnResource(resource);
            }
        }
        return result;
    }

    public static NameValuePair[] getMetadatas(String fileId) {
        NameValuePair[] metaList = null;
        boolean ex = false;
        FdfsClient resource = fdfsPool.getResource();
        try {
            metaList = resource.get_metadata1(fileId);
        } catch (Exception e) {
            ex = true;
            throw ExceptionUtil.translate(e);
        } finally {
            if (ex) {
                fdfsPool.returnBrokenResource(resource);
                if (!resource.isClose()) {
                    resource.close();
                }
            } else {
                fdfsPool.returnResource(resource);
            }
        }
        return metaList == null ? EMPTY_ARR : metaList;
    }

    public static void setFdfsPool(FdfsPool fdfsPool) {
        FdfsUtil.fdfsPool = fdfsPool;
    }
}
