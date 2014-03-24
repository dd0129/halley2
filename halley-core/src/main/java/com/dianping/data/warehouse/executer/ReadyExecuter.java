package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by adima on 14-3-23.
 */
public class ReadyExecuter {
    private static final Logger logger = LoggerFactory.getLogger(ReadyExecuter.class);

    @Resource(name = "instanceDAO")
    private InstanceDAO instDAO;

    public void execute(){
        List<InstanceDO> list = instDAO.getReadyTaskList(Const.JOB_STATUS.JOB_INIT.getValue(),System.currentTimeMillis());
        for(InstanceDO inst : list){
            try{
                boolean flag = this.updateTask(inst);
                if(flag){
                    this.instDAO.updateTaskReady(inst.getInstanceId(),Const.JOB_STATUS.JOB_READY.getValue());
                }
            }catch(Exception e){
                logger.error(inst.getInstanceId() + "(" + inst.getTaskName() + ") update ready error",e);
            }
        }
    }

    private boolean updateTask(InstanceDO inst){
        if(inst.getIfPre() == Const.TASK_NONEXISTS_PRE){
            return true;
        }else if(inst.getIfPre() ==Const.TASK_EXISTS_PRE){
            List<InstanceDO> list = this.instDAO.getRelaInstanceList(inst.getInstanceId());
            for(InstanceDO preInst: list){
                if(preInst.getStatus() == null){
                    logger.info(inst.getInstanceId()+ "("+inst.getTaskName() + ") job is not ready,pre job "
                            + preInst.getInstanceId() + "(" + preInst.getTaskName() + ")" + " does not have initialization ");
                    return false;
                }else if(preInst.getStatus() != Const.JOB_STATUS.JOB_SUCCESS.getValue()){
                    logger.info(inst.getInstanceId()+"(" + inst.getTaskName() + ") job is not ready,pre job "
                            + preInst.getInstanceId() + "(" + preInst.getTaskName() + ") status is " );
                    return false;
                }else{
                    logger.error(inst.getInstanceId()+"(" + inst.getTaskName() + ") unknow error");
                    return false;
                }
            }
            return true;
        }else{
            logger.error(inst.getInstanceId()+"(" + inst.getTaskName() + ") if_pre is illegal");
            return false;
        }
    }
}
