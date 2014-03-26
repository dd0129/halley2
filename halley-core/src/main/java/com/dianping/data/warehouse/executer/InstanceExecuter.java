package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.resource.ResourceManager;
import com.dianping.data.warehouse.resource.RunningQueueManager;
import com.dianping.data.warehouse.utils.ProcessUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by adima on 14-3-24.
 */
@Service("instanceExecuter")
public class InstanceExecuter {
    private static Logger logger = LoggerFactory.getLogger(InstanceExecuter.class);
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    private InstanceDO getReadyInstance() throws Exception {
        List<InstanceDO> list = this.instDAO.getReadyInstanceList(Const.JOB_STATUS.JOB_READY.getValue());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        for (InstanceDO inst : list) {
            if (inst != null && RunningQueueManager.isDuplicateTask(inst.getTaskId())) {
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") exists duplicate task");
                continue;
            } else {
                if (RunningQueueManager.idDupliateInstance(inst.getInstanceId())) {
                    logger.info("Warning!!" + inst.getInstanceId() + "(" + inst.getTaskName() + " is already in Running Queue");
                    continue;
                }
                return inst;
            }
        }
        return null;
    }

    private String[] executeTask(InstanceDO inst) throws Exception {
        logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") job starts");
        this.instDAO.updateInstnaceRunning(inst.getInstanceId(),Const.JOB_STATUS.JOB_RUNNING.getValue(),Const.JOB_STATUS.JOB_RUNNING.getDesc());
        if(inst.getType()==Const.TASK_TYPE_LOAD){
            return ProcessUtils.executeWormholeCommand(inst);
        }else if(inst.getType()==Const.TASK_TYPE_CALCULATE){
            return ProcessUtils.executeCommand(inst);
        }else{
            logger.error("illegal type parameter");
            throw new IllegalArgumentException("task's type is illegal");
        }
    }

    private void recordLog(InstanceDO inst, String[] rtn) {
        int rtnCode = Integer.valueOf(rtn[0]);
        String[] successCode = inst.getSuccessCode().split(";");

        for (int i = 0; i < successCode.length; i++) {
            if (rtnCode == Integer.valueOf(successCode[i])) {
                this.instDAO.updateInstnaceStatus(inst.getInstanceId(),Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                        Const.JOB_STATUS.JOB_SUCCESS.getDesc());
                return;
            }
        }

        if (inst.getIfWait() == Const.TASK_IF_WAIT) {
            String[] waitCodes = null;
            if (StringUtils.isNotBlank(inst.getWaitCode())) {
                waitCodes = inst.getWaitCode().split(";");
                for (int i = 0; i < waitCodes.length; i++) {
                    if (rtnCode == Integer.valueOf(waitCodes[i])) {
                        this.instDAO.updateInstnaceStatus(inst.getInstanceId(),Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                                Const.JOB_STATUS.JOB_SUCCESS.getDesc());
                        return;
                    }
                }
            } else {
                logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ")" + "  wait_code is null,please handle");
            }
        }

        this.instDAO.updateInstnaceStatus(inst.getInstanceId(),Const.JOB_STATUS.JOB_FAIL.getValue(),
                Const.JOB_STATUS.JOB_FAIL.getDesc());
    }

    public void execute() {
        InstanceDO inst = null;
        boolean flag = false;
        try {
            logger.info("the task executer thread starts");
            inst = this.getReadyInstance();

            if (inst == null) {
                return;
            } else {
                flag = ResourceManager.allocate(inst.getDatabaseSrc());
                if(!flag){
                    return;
                }
                RunningQueueManager.inQueue(inst);
                logger.info("Running Queue already run " + RunningQueueManager.size() + " tasks");
                logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + " join to Running Queue");
                String[] rtns = this.executeTask(inst);
                this.recordLog(inst, rtns);
                //this.sendEmail(ts);
            }
        } catch (Exception e) {
            logger.error("task executer thread error", e);
        } finally {
            if(inst != null){
                RunningQueueManager.outQueue(inst);
                if(flag){
                    ResourceManager.release(inst.getDatabaseSrc());
                }
            }
            logger.info("the task executer process ends");
        }
    }
}
