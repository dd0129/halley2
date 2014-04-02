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

    private Integer executeTask(InstanceDO inst){
        logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") job starts");
        this.instDAO.updateInstnaceRunning(inst.getInstanceId(),Const.JOB_STATUS.JOB_RUNNING.getValue(),Const.JOB_STATUS.JOB_RUNNING.getDesc());
        if(inst.getType()==Const.TASK_TYPE_LOAD){
            return ProcessUtils.executeWormholeCommand(inst);
        }else if(inst.getType()==Const.TASK_TYPE_CALCULATE){
            return ProcessUtils.executeCommand(inst);
        }else{
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(),Const.JOB_STATUS.JOB_INTERNAL_ERROR.getValue(),
                        Const.JOB_STATUS.JOB_INTERNAL_ERROR.getDesc());
            logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ") type "+inst.getType()+ "is illegal type");
            throw new IllegalArgumentException(inst.getInstanceId() + "(" + inst.getTaskName() + ") type "+inst.getType()+ "is illegal type");
        }
    }

    private void recordLog(InstanceDO inst, Integer rtn) {
        try{
            String[] successCodes = inst.getSuccessCode().split(";");
            for (String successCode : successCodes) {
                if (rtn == Integer.valueOf(successCode).intValue()) {
                    logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") is success");
                    this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                            Const.JOB_STATUS.JOB_SUCCESS.getDesc());
                    return;
                }
            }

            if (inst.getIfWait() == Const.TASK_IF_WAIT) {
                String[] waitCodes = null;
                if (StringUtils.isNotBlank(inst.getWaitCode())) {
                    waitCodes = inst.getWaitCode().split(";");
                    for (String waitCode : waitCodes) {
                        if (rtn == Integer.valueOf(waitCode).intValue()) {
                            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") retcode "+rtn+" is wait");
                            this.instDAO.updateInstnaceStatus(inst.getInstanceId(), Const.JOB_STATUS.JOB_SUCCESS.getValue(),
                                    Const.JOB_STATUS.JOB_SUCCESS.getDesc());
                            return;
                        }
                    }
                } else {
                    logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ")" + "  wait_code is null,please handle");
                }
            }
            logger.info(inst.getInstanceId() + "(" + inst.getTaskName() + ") retcode "+rtn+" is fail");
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(),Const.JOB_STATUS.JOB_FAIL.getValue(),
                    Const.JOB_STATUS.JOB_FAIL.getDesc());
        }catch(Throwable e){
            logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ")record log error",e);
            this.instDAO.updateInstnaceStatus(inst.getInstanceId(),Const.JOB_STATUS.JOB_INTERNAL_ERROR.getValue(),
                    Const.JOB_STATUS.JOB_INTERNAL_ERROR.getDesc());
        }

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
                this.recordLog(inst, this.executeTask(inst));
                //
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
