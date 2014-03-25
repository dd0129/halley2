package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.resource.ResourceManager;
import com.dianping.data.warehouse.resource.RunningQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-24.
 */
public class SynchronizeExecuter {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizeExecuter.class);

    @Resource(name = "instanceDAO")
    private InstanceDAO instDAO;

    private Integer interval;
    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void execute() {
        boolean flag = false;
        try {
            logger.info("the SynchronizeExecuter thread starts");
            for (Map.Entry<String, InstanceDO> entry : RunningQueueManager.entrySet()) {
                InstanceDO inst = entry.getValue();
                Long inQueueTime = inst.getInQueueTimeMillis();
                logger.info("current time " + System.currentTimeMillis() + "; " + inst.getInstanceId() + "(" + inst.getTaskName() +
                        ") push queue time " + inst.getInQueueTimeMillis());
                Integer status = instDAO.getInstanceInfo(inst.getInstanceId()).getStatus();
                logger.info("task status " + status);
                try{
                    if (System.currentTimeMillis() - inQueueTime > interval * 1000) {
                        if (status != Const.JOB_STATUS.JOB_RUNNING.getValue() && status != Const.JOB_STATUS.JOB_RUNNING.getValue()) {
                            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") status is "+inst.getStatus() +" in queue "+ String.valueOf(inst.getInQueueTimeMillis()) + " has been kicked");
                            RunningQueueManager.outQueue(inst);
                            flag = true;
                        }
                    }
                }finally{
                    if(inst!= null && flag){
                        ResourceManager.release(inst.getDatabaseSrc());
                    }
                }
            }
        } finally {
            logger.info("the SynchronizeExecuter thread ends");
        }
    }

}
