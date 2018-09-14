package com.fdfs.pool;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description: ip和port
 */
public class HostAndPort implements Serializable {
    private static final long serialVersionUID = -519876229978427751L;

    public static final String LOCALHOST_STR = "localhost";

    private String host;
    private int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HostAndPort) {
            HostAndPort hp = (HostAndPort) obj;

            String thisHost = convertHost(host);
            String hpHost = convertHost(hp.host);
            return port == hp.port && thisHost.equals(hpHost);

        }

        return false;
    }

    @Override
    public int hashCode() {
        return 31 * convertHost(host).hashCode() + port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    private String convertHost(String host) {
        if (host.equals("127.0.0.1")) return LOCALHOST_STR;
        else if (host.equals("::1")) return LOCALHOST_STR;

        return host;
    }
}
