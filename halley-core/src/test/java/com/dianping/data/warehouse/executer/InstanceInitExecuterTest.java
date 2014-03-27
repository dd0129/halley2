package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.dao.TaskDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.domain.TaskDO;
import com.dianping.data.warehouse.domain.TaskRelaDO;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-applicationcontext.xml")
public class InstanceInitExecuterTest {
    @Resource(name = "taskDAO")
    private TaskDAO taskDAO;

    @Resource(name="instanceInitExecuter")
    private InstanceInitExecuter instanceInit;

    private List<TaskDO> list;
    private List<TaskRelaDO> relaList;
    private Map<Integer,List<TaskRelaDO>> relaMap;

    @Before
    public void setup() throws Exception{
        list = taskDAO.getValidateTaskList(Const.TASK_VALIDATE);
        relaList =  taskDAO.getTaskRelaList();
        Method method = InstanceInitExecuter.class.getDeclaredMethod("convertRelaMap", List.class);
        method.setAccessible(true);
        relaMap = (Map<Integer,List<TaskRelaDO>>)method.invoke(instanceInit,relaList);
    }

    @Test
    public void testExecute() throws Exception {

    }

    @Test
    public void testConvertRelaMap() throws Exception{
        Method method = InstanceInitExecuter.class.getDeclaredMethod("convertRelaMap", List.class);
        method.setAccessible(true);
        Map<Integer,List<TaskRelaDO>> output = (Map<Integer,List<TaskRelaDO>>)
                method.invoke(instanceInit,relaList);
        Assert.assertNotNull(output.size());
        Assert.assertSame(output.get(500832).size(),46);

    }

    @Test
    public void testConvertTaskMap() throws Exception{
        Method method = InstanceInitExecuter.class.getDeclaredMethod("convertTaskMap", List.class);
        method.setAccessible(true);
        Map<Integer,TaskDO> output = (Map<Integer,TaskDO>)method.invoke(instanceInit,list);
        Assert.assertNotNull(output.size());
        Assert.assertEquals(output.size(), 4827);
        Assert.assertEquals(output.get(200531).getCycle(), "D");
    }

    @Test
    public void testGenerateInstance() throws Exception{
        Method method = InstanceInitExecuter.class.getDeclaredMethod("generateInstance", TaskDO.class,List.class,Date.class);
        TaskDO task = list.get(2);
        List<TaskRelaDO> relaList = relaMap.get(task.getTaskId());
        method.setAccessible(true);
        InstanceDO output = (InstanceDO)method.invoke(instanceInit,task,relaList,new Date());

        Assert.assertNotNull(output.getInstanceId());
        Assert.assertNotNull(output.getCycle());
        Assert.assertNotNull(output.getSuccessCode());
        Assert.assertNotNull(output.getIfRecall());
        Assert.assertNotNull(output.getIfWait());
        Assert.assertNotNull(output.getIfPre());

    }


}
