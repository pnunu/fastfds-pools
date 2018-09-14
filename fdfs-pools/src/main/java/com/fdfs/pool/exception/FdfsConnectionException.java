package com.fdfs.pool.exception;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description:
 */
public class FdfsConnectionException extends FdfsException {

    public FdfsConnectionException(String message) {
        super(message);
    }

    public FdfsConnectionException(Throwable e) {
        super(e);
    }

    public FdfsConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
