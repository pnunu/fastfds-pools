package com.fdfs.util;

import com.fdfs.pool.exception.FdfsException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/3/5
 * Description:
 */
public abstract class ExceptionUtil {

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
        }
        return throwable;
    }

    public static List<Throwable> getCausalChain(Throwable throwable) {
        List<Throwable> causes = new ArrayList<Throwable>(4);
        while (throwable != null) {
            causes.add(throwable);
            throwable = throwable.getCause();
        }
        return Collections.unmodifiableList(causes);
    }

    public static String getAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter(50);
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 转换成fdfs异常
     *
     * @return
     */
    public static FdfsException translate(Exception e) {
        return e instanceof FdfsException ? (FdfsException) e : new FdfsException(e);
    }
}

