package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.dao.TaskDAO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.domain.InstanceRelaDO;
import com.dianping.data.warehouse.domain.TaskDO;
import com.dianping.data.warehouse.domain.TaskRelaDO;
import com.dianping.data.warehouse.utils.DateUtils;
import com.dianping.data.warehouse.utils.ParameterUtils;
import com.dianping.data.warehouse.utils.TaskUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by adima on 14-3-23.
 */
public class InstanceInitExecuter {
    @Resource(name="taskDAO")
    private TaskDAO taskDAO;

    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    private Map<Integer,TaskDO> taskMap;
    private Map<Integer,List<TaskRelaDO>> relaMap;
    private Map<Integer,List<TaskRelaDO>> reverseRelaMap;

    private Logger logger = LoggerFactory.getLogger(InstanceInitExecuter.class);


    public void execute(){
        //查询所有有效任务
        List<TaskDO> list = taskDAO.getValidateTaskList(Const.TASK_VALIDATE);
        List<TaskRelaDO> relaList =  taskDAO.getTaskRelaList();
        taskMap = this.convertTaskMap(list);
        relaMap = this.convertRelaMap(relaList);
        reverseRelaMap = this.convertRelaMap(relaList);

        //查询所有依赖
        Date begin = new Date();
        Date end = new Date(begin.getTime() + 1000 * 3600 * 2);

        //循环所有任务任务
        for(TaskDO task : list){
            Date triggerTime = begin;
            CronExpression expression = null;
            if(CronExpression.isValidExpression(task.getFreq())){
                logger.error(task.getTaskId() + "(" +task.getTaskName() + ")" + " " + task.getFreq()+ "is illegal cron expression");
                InstanceDO inst = this.generateInstance(task,relaMap.get(task.getTaskId()),null);
                this.saveInstance(inst);

            }else{
                while(true){
                    triggerTime = expression.getNextInvalidTimeAfter(triggerTime);
                    InstanceDO inst = this.generateInstance(task,relaMap.get(task.getTaskId()),triggerTime);
                    if(triggerTime.getTime() > end.getTime()){
                        break;
                    }
                    //instDAO.getInstanceInfo(inst.getInstanceId()).getInstanceId()
                    if(StringUtils.isBlank(null)){
                        DynamicPriority dp = new DynamicPriority(inst.getTaskId(),inst.getPrioLvl());
                        //验证任务正确性，检查是否包含自包含
                        //调用内部类获取任务score
                        Integer score = dp.calculateScore(inst.getTaskId(),inst.getPrioLvl());
                        inst.setRunningPrio(score);
                        //将任务和依赖存储到intance表
                        this.saveInstance(inst);
                        logger.info(new StringBuilder().append(inst.getInstanceId()).append("(").
                                append(inst.getTaskName()).append(") init successful;").append("score :=")
                                .append(inst.getRunningPrio()).toString());
                    }
                }
            }

        }
    }

    private void saveInstance(InstanceDO inst){
        this.instDAO.saveInstance(inst);
        this.instDAO.saveInstanceRela(inst.getInstRelaList());
    }

    private Map<Integer,List<TaskRelaDO>> convertRelaMap(List<TaskRelaDO> relaList){
        Map<Integer,List<TaskRelaDO>> result = new HashMap<Integer,List<TaskRelaDO>>();
        for(TaskRelaDO rela : relaList ){
            if(!result.containsKey(rela.getTaskId())){
                List<TaskRelaDO> tmp = new ArrayList<TaskRelaDO>();
                tmp.add(rela);
                result.put(rela.getTaskId(),tmp);
            }else{
                List tmp = result.get(rela.getTaskId());
                tmp.add(rela);
            }
        }
        return result;
    }

    private Map<Integer,List<TaskRelaDO>> convertReverseRelaMap(){
        List<TaskRelaDO> relaList =  taskDAO.getTaskRelaList();
        Map<Integer,List<TaskRelaDO>> result = new HashMap<Integer,List<TaskRelaDO>>();
        for(TaskRelaDO rela : relaList ){
            if(!result.containsKey(rela.getPreId())){
                List<TaskRelaDO> tmp = new ArrayList<TaskRelaDO>();
                tmp.add(rela);
                result.put(rela.getPreId(),tmp);
            }else{
                List tmp = result.get(rela.getPreId());
                tmp.add(rela);
            }
        }
        return result;
    }

    private Map<Integer,TaskDO> convertTaskMap(List<TaskDO> list){
        Map<Integer,TaskDO> result = new HashMap<Integer,TaskDO>();
        for(TaskDO task : list ){
            result.put(task.getTaskId(),task);
        }
        return result;
    }

    private InstanceDO generateInstance(TaskDO task,List<TaskRelaDO> relaList,Date triggerTime){
        String instanceId =null,cycle=null,para1=null,
               para2=null,para3=null,logPath=null,calDt = null;
        Integer status = Const.JOB_STATUS.JOB_INIT.getValue();
        String desc = Const.JOB_STATUS.JOB_INIT.getDesc();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currTime = formatter.format(new Date());
        instanceId = TaskUtils.generateInstanceID(task.getTaskId(),task.getCycle(),triggerTime);

        para1 = ParameterUtils.resourceParamHandle(DateUtils.getReplaceCal(task.getPara1(), task.getOffsetType(), task.getOffset(), triggerTime))
                .replace("${task_id}", String.valueOf(task.getTaskId()))
                .replace("${instance_id}", instanceId)
                .replace("${unix_timestamp}", String.valueOf(triggerTime.getTime() / 1000));
        para2 = ParameterUtils.resourceParamHandle(DateUtils.getReplaceCal(task.getPara2(), task.getOffsetType(), task.getOffset(), triggerTime))
                .replace("${task_id}", String.valueOf(task.getTaskId()))
                .replace("${instance_id}", instanceId)
                .replace("${unix_timestamp}", String.valueOf(triggerTime.getTime() / 1000));
        para3 = ParameterUtils.resourceParamHandle(DateUtils.getReplaceCal(task.getPara3(), task.getOffsetType(), task.getOffset(), triggerTime))
                .replace("${task_id}", String.valueOf(task.getTaskId()))
                .replace("${instance_id}", instanceId)
                .replace("${unix_timestamp}", String.valueOf(triggerTime.getTime() / 1000));
        try{
            cycle = DateUtils.getDay10(triggerTime);
            String lastDay = DateUtils.getLastDay10(triggerTime);


            logPath = new StringBuilder(ParameterUtils.resourceParamHandle(task.getLogHome()))
                    .append(File.separator).append(task.getLogFile().trim()).append(".")
                    .append(instanceId).append(".").append(DateUtils.getDay8()).toString();
            calDt = DateUtils.get_cal_dt(lastDay, task.getOffsetType(), task.getOffset());
        }catch(Exception e){
            logger.error(task.getTaskId() + " init error",e);
            status =  Const.JOB_STATUS.JOB_INIT_ERROR.getValue();
            desc = Const.JOB_STATUS.JOB_INIT_ERROR.getDesc();
        }

        InstanceDO inst = new InstanceDO();
        inst.setInstanceId(instanceId);
        inst.setTaskId(task.getTaskId());
        inst.setTaskGroupId(task.getTaskGroupId());
        inst.setTaskName(task.getTaskName());
        String taskObj = ParameterUtils.resourceParamHandle(task.getTaskObj());
        inst.setTaskObj(taskObj);
        inst.setRunningPrio(null);

        inst.setPara1(para1);
        inst.setPara2(para2);
        inst.setPara3(para3);
        inst.setLogPath(logPath);
        inst.setCycle(task.getCycle());
        inst.setTimeId(cycle);
        inst.setStatus(status);
        inst.setPrioLvl(task.getPrioLvl());
        inst.setRunNum(0);
        inst.setType(task.getType());
        inst.setDatabaseSrc(task.getDatabaseSrc());
        inst.setTableName(task.getTableName());
        inst.setFreq(task.getFreq());
        inst.setCalDt(calDt);
        inst.setIfPre(task.getIfPre());
        inst.setStsDesc(desc);
        inst.setRecallNum(0);
        inst.setOwner(task.getOwner());
        inst.setTriggerTime(triggerTime.getTime());
        inst.setRecallCode(task.getRecallCode());
        inst.setSuccessCode(task.getSuccessCode());
        inst.setIfWait(task.getIfWait());
        inst.setIfRecall(task.getIfRecall());
        inst.setWaitCode(task.getWaitCode());
        inst.setTimeout(task.getTimeout());
        inst.setRecallLimit(task.getRecallLimit());
        inst.setRecallInterval(task.getRecallInterval());

        List<TaskRelaDO> list =  relaMap.get(task.getTaskId());
        for(TaskRelaDO relaDO : list){
            InstanceRelaDO instRela = new InstanceRelaDO();
            instRela.setInstanceId(instanceId);
            instRela.setTaskId(task.getTaskId());
            String preInstanceId = DateUtils.generateRelaInstanceID(relaDO.getPreId(), triggerTime.getTime(),
                    relaDO.getCycleGap());
            instRela.setPreInstanceId(preInstanceId);
            instRela.setPreId(relaDO.getPreId());
        }
        inst.setInstRelaList(null);
        return inst;
    }


    private class DynamicPriority {
        DynamicPriority(Integer baseId,Integer level) {
            this.rootNodeId = baseId;
            if (level == 1) {
                point = midLimit + 1;
                limit = highLimit;
            } else if (level == 2) {
                point = lowLimit + 1;
                limit = midLimit;
            } else {
                point = 1;
                limit = lowLimit;
            }
        }

        private double point;
        private int limit;
        private int quotiety = 1;
        private int highLimit = 400;
        private int midLimit = 200;
        private int lowLimit = 50;
        private Integer rootNodeId = null;

        private Map<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();


        int calculateScore(int task_id, int prioLvl) {
            if (prioLvl < 1) {
                return 401;
            }
            if (prioLvl > 3) {
                return 0;
            }
            for (Integer prio : scoreMap.values()) {
                if (prio != null) {
                    point = point + 1d / prio * this.quotiety;
                }
            }
            if (point >= this.limit) {
                return this.limit;
            }
            return new Long(Math.round(point)).intValue();
        }

        private void generateScoreMap(Integer pk) {
            for (TaskRelaDO rela: reverseRelaMap.get(pk)) {
                Integer childId = rela.getTaskId();
                if (this.rootNodeId == childId) {
                    return;
                }
                if (scoreMap.containsKey(childId)) {
                    return;
                }
                Integer prioLvl = taskMap.get(childId).getPrioLvl();
                this.scoreMap.put(childId, prioLvl);
                this.generateScoreMap(childId);
            }
        }
    }

}
