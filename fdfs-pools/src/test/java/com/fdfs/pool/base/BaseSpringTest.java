package com.fdfs.pool.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: qiujingwang
 * Date: 2016-3-27
 * Depiction: 测试基类
 */
//指定测试用例的运行器 这里是指定了Junit4
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-context-test-local.xml")
public abstract class BaseSpringTest {

    @Before
    public void setUp() {
        init();

    }

    protected void init(){
    }
}