package com.dianxian.testframework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Listeners;

import java.util.Random;

/**
 * Created by XuWenHao on 4/19/2016.
 */
@Listeners(value = {LogSeperateTestListener.class})
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:dataSource.xml", "classpath:applicationContext-test.xml"})
public abstract class AbstractServiceTest extends AbstractTestNGSpringContextTests {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    Random rand = new Random();

    public Long randomLong() {
        return rand.nextLong();
    }

    public void notNull(Object value) {
        Assert.assertNotNull(value);
    }
}
