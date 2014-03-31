package com.dianping.data.warehouse.executer;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by hongdi.tang on 14-3-31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-applicationcontext.xml")
public class ResetExecuterTest {

    @Resource(name="resetExecuter")
    private ResetExecuter service;

    @Test
    public void testExecute() throws Exception {
        service.execute();
        Assert.assertTrue(true);
    }
}
