package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

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
        InstanceDO inst = null;
        try {
            logger.info("the SynchronizeExecuter thread starts");
//            for (Map.Entry<String, InstanceDO> entry : StandardExecute.runningQueue.entrySet()) {
//                inst = entry.getValue();
//                Long inQueueTime = inst.getInQueueTimeMillis();
//                logger.info("current time " + System.currentTimeMillis() + "; " + inst.getInstanceId() + "(" + inst.getTaskName() +
//                        ") push queue time " + inst.getInQueueTimeMillis());
//                Integer status = instDAO.getInstanceId(inst);
//                logger.info("task status " + status);
//                if (System.currentTimeMillis() - inQueueTime > interval * 1000) {
//                    if (status != SystemConstant.JOB_RUNNING && status != SystemConstant.JOB_TIMEOUT) {
//                        logger.info("the task " + inst.getTask_status_id() + "(" + inst.getTask_name() + ") push queue at " + String.valueOf(inst.getInQueueTimeMillis()) + " has been kicked");
//                        StandardExecute.runningQueue.remove(entry.getKey());
//                        flag = true;
//                    }
//                }
//            }
        } finally {
            if(inst!= null && flag){
                ResourceManager.release(inst.getDatabaseSrc());
            }
            logger.info("the SynchronizeExecuter thread ends");
        }
    }

}
