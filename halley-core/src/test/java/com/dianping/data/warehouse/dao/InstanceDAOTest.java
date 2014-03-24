package com.dianping.data.warehouse.dao;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.domain.InstanceDO;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by adima on 14-3-23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-applicationcontext.xml")
public class InstanceDAOTest {

    private static Logger logger = LoggerFactory.getLogger(InstanceDAOTest.class);

    @Resource(name="instanceDAO")
    private InstanceDAO dao;
    @Test
    public void testSaveInstance() throws Exception {

    }

    @Test
    public void testSaveInstanceRela() throws Exception {

    }

    @Test
    public void testGetInstanceInfo() throws Exception {
//        String s = "1000120120509";
//        List<InstanceDO> inst = dao.getInstanceInfo(s);
//        System.out.println(inst.get(0).getInstanceId());
//        Assert.assertEquals(s,inst.get(0).getInstanceId());
    }

    @Test
    public void testGetReadyTaskList() throws Exception {
        List<InstanceDO> list = dao.getReadyTaskList(Const.JOB_STATUS.JOB_INIT.getValue(),1556723123242L);
        System.out.println(list.size());
        Assert.assertNotNull(list);
    }
    @Test
    public void testUpdateTaskReay() throws Exception {
        Integer flag = this.dao.updateTaskReady("1000120120514", Const.JOB_STATUS.JOB_READY.getValue());
        System.out.println(flag);
        Assert.assertEquals(flag, Integer.valueOf(1));
    }
    @Test
    public void testGetRelaInstanceList() throws Exception {
        List<InstanceDO> list = this.dao.getRelaInstanceList("5001022013082100");
        logger.info(String.valueOf(list.size()));
        Assert.assertNotNull(list.size());
    }






}
