package com.fdfs.pool.exception;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description:
 */
public class FdfsException extends RuntimeException {
    private static final long serialVersionUID = -2946266495682282677L;

    public FdfsException(String message) {
        super(message);
    }

    public FdfsException(Throwable e) {
        super(e);
    }

    public FdfsException(String message, Throwable cause) {
        super(message, cause);
    }
}
