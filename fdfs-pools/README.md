<a href="https://info.flagcounter.com/gnaM"><img src="https://s05.flagcounter.com/count2/gnaM/bg_FFFFFF/txt_000000/border_CCCCCC/columns_2/maxflags_10/viewers_0/labels_0/pageviews_0/flags_0/percent_0/" alt="Flag Counter" border="0"></a>

# 原作者代码提交于gitee   地址： https://gitee.com/qiujw/fdfs-pools


# fastdfs-client

https://github.com/pnunu/fastdfs-client-java.git

# fastdfs 安装教程

http://pnunu.cn/posts/20180720-fastfds-install


#### 项目介绍
fastdfs 连接池


#### 使用说明
1.spring.xml配置

    <!-- fdfs 对象连接池配置 -->
    <bean id="fdfsGenericObjectPoolConfig" class="org.apache.commons.pool2.impl.GenericObjectPoolConfig">
        <property name="maxIdle" value="${fdfs.pool.maxIdle}"/>
        <property name="maxTotal" value="${fdfs.pool.maxTotal}"/>
        <property name="minIdle" value="${fdfs.pool.minIdle}"/>
        <property name="maxWaitMillis" value="${fdfs.pool.maxWaitMillis}"/>
    </bean>


    <!-- fdfs 对象连接池 -->
    <bean id="fdfsPool" class="com.fdfs.pool.factory.spring.FastfdsPoolFactoryBean" destroy-method="destroy">
        <property name="fdfsServerHosts" value="${fdfs.fdfsServerHosts}"/>
        <property name="antiStealToken" value="${fdfs.antiStealToken}"/>
        <property name="secretKey" value="${fdfs.secretKey}"/>
        <property name="connectTimeout" value="${fdfs.connectTimeout}"/>
        <property name="networkTimeout" value="${fdfs.networkTimeout}"/>
        <property name="charset" value="${fdfs.charset}"/>
        <property name="genericObjectPoolConfig" ref="fdfsGenericObjectPoolConfig"/>

        <!-- 相当于调用com.fdfs.util.FdfsUtil.setFdfsPool() -->
        <property name="enableFdfsUtil" value="true"/>
    </bean>

  2.properties

    fdfs.pool.maxIdle=50
    fdfs.pool.maxTotal=100
    fdfs.pool.minIdle=20
    fdfs.pool.maxWaitMillis=5000
    fdfs.connectTimeout=3000
    fdfs.networkTimeout=10000
    fdfs.charset=UTF-8
    fdfs.antiStealToken=no
    fdfs.secretKey=FastDFS1234567890
    fdfs.fdfsServerHosts=ip1:22122,ip2:22122,ip3:22122

3.测试

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
        String fileId = "G1/M00/02/12/eEyJuFoEGQmAWKTOAAAAAS0C740765.txt";
        NameValuePair[] metadatas = FdfsUtil.getMetadatas(fileId);
        System.out.println(metadatas);
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
