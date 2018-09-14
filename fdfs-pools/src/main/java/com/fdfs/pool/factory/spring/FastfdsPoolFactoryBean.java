package com.fdfs.pool.factory.spring;

import com.fdfs.util.FdfsUtil;
import com.fdfs.pool.FdfsPool;
import com.fdfs.pool.FdfsPoolConfig;
import com.fdfs.pool.HostAndPort;
import com.fdfs.pool.factory.FdfsClientFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerGroup;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016/6/3
 * Description:
 */
public class FastfdsPoolFactoryBean implements FactoryBean<FdfsPool>, InitializingBean {

    /****************
     * 默认配置
     ********************/
    public static final int CONNECT_TIMEOUT = 2000;//单位：毫秒
    public static final int NETWORK_TIMEOUT = 3000;//单位：毫秒
    public static final String CHARSET = "ISO8859-1";
    public static final int HTTP_TRACKER_HTTP_PORT = 8080;
    public static final String HTTP_ANTI_STEAL_TOKEN = "no";
    public static final String HTTP_SECRET_KEY = "FastDFS1234567890";

    private String fdfsServerHosts;//ip:port

    private FdfsPool fdfsPool;

    private int connectTimeout = CONNECT_TIMEOUT;//毫秒
    private int networkTimeout = NETWORK_TIMEOUT;//毫秒
    private int trackerHttpPort = HTTP_TRACKER_HTTP_PORT;
    private String charset = CHARSET;
    private String antiStealToken = HTTP_ANTI_STEAL_TOKEN;
    private String secretKey = HTTP_SECRET_KEY;

    //是否设置 com.fdfs.util.FdfsUtil.setFdfsPool()
    private boolean enableFdfsUtil = true;

    private GenericObjectPoolConfig genericObjectPoolConfig;
    private FdfsClientFactory fdfsClientFactory;

    //IP:PORT
    private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

    @Override
    public FdfsPool getObject() throws Exception {
        return fdfsPool;
    }

    @Override
    public Class<? extends FdfsPool> getObjectType() {
        return (this.fdfsPool != null ? this.fdfsPool.getClass() : FdfsPool.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Set<HostAndPort> parseHostAndPort() throws Exception {
        if (fdfsServerHosts == null || fdfsServerHosts.trim().length() == 0) {
            throw new IllegalArgumentException("fdfsServerHosts 不能为空，格式为：ip1:port1,ip2:port2");
        }
        try {
            String[] ipPorts = fdfsServerHosts.split(",");
            final Set<String> sentinelHosts = new HashSet<>(Arrays.asList(ipPorts));
            Set<HostAndPort> haps = new HashSet<HostAndPort>();
            for (String host : sentinelHosts) {

                boolean isIpPort = p.matcher(host).matches();

                if (!isIpPort) {
                    throw new IllegalArgumentException("ip 或 port 不合法");
                }
                String[] ipAndPort = host.split(":");

                HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                haps.add(hap);
            }
            if (haps.isEmpty()) {
                throw new IllegalArgumentException("fdfsServerHosts 不能为空，格式为：ip1:port1,ip2:port2");
            }

            return haps;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception("解析 fdfs 配置文件失败", ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<HostAndPort> haps = this.parseHostAndPort();
        initClientGlobal(haps);
        if (genericObjectPoolConfig == null) {
            genericObjectPoolConfig = new FdfsPoolConfig();
        }
        if (fdfsClientFactory == null) {
            fdfsClientFactory = new FdfsClientFactory();
        }
        fdfsPool = new FdfsPool(genericObjectPoolConfig, fdfsClientFactory);
        //初始化池对象
        fdfsPool.addObjects(genericObjectPoolConfig.getMinIdle());
        if (enableFdfsUtil) {
            FdfsUtil.setFdfsPool(fdfsPool);
        }
    }

    private void initClientGlobal(Set<HostAndPort> haps) {
        InetSocketAddress[] trackerServers = new InetSocketAddress[haps.size()];
        Iterator<HostAndPort> iterator = haps.iterator();
        int index = 0;
        for (; iterator.hasNext(); ) {
            HostAndPort hostAndPort = iterator.next();
            trackerServers[index++] = new InetSocketAddress(hostAndPort.getHost(), hostAndPort.getPort());
        }
        ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));
        // 连接超时的时限，单位为毫秒
        ClientGlobal.setG_connect_timeout(connectTimeout);
        // 网络超时的时限，单位为毫秒
        ClientGlobal.setG_network_timeout(networkTimeout);
        boolean b_anti_steal_token = antiStealToken.equalsIgnoreCase("yes")
                || antiStealToken.equalsIgnoreCase("true")
                || antiStealToken.equals("1");
        if (b_anti_steal_token && secretKey != null && !"".equals(secretKey)) {
            ClientGlobal.setG_anti_steal_token(b_anti_steal_token);
            ClientGlobal.setG_secret_key(secretKey);
        }
        // 字符集
        if (charset == null || charset.length() == 0) {
            charset = CHARSET;
        }
        ClientGlobal.setG_charset(charset);
    }


    public void destroy() {
        fdfsPool.destroy();
    }

    public GenericObjectPoolConfig getGenericObjectPoolConfig() {
        return genericObjectPoolConfig;
    }

    public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
        this.genericObjectPoolConfig = genericObjectPoolConfig;
    }

    public void setFdfsServerHosts(String fdfsServerHosts) {
        this.fdfsServerHosts = fdfsServerHosts;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setNetworkTimeout(int networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public void setTrackerHttpPort(int trackerHttpPort) {
        this.trackerHttpPort = trackerHttpPort;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setAntiStealToken(String antiStealToken) {
        this.antiStealToken = antiStealToken;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setEnableFdfsUtil(boolean enableFdfsUtil) {
        this.enableFdfsUtil = enableFdfsUtil;
    }

    public void setFdfsClientFactory(FdfsClientFactory fdfsClientFactory) {
        this.fdfsClientFactory = fdfsClientFactory;
    }
}


