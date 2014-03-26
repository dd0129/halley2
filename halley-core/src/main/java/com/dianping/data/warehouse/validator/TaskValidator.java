package com.dianping.data.warehouse.validator;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.domain.TaskDO;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hongdi.tang on 14-3-26.
 */
public class TaskValidator {

    private static String[] validateOffsetType(String offsetType,String offset){
        String regex = "[D|M][\\d]{1,3}";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(offset);
        boolean flag = "offset".equals(offsetType) && matcher.matches();
        String[] rtn = new String[2];
        rtn[0] = flag ? "1" : "0";
        if(!flag){
            rtn[1] = "offset "+offset+ " or offsetType "+ offsetType+" is illegal";
        }
        return rtn;
    }

    private static String[] validateWait(Integer ifWait,String waitCode){
        boolean flag = false;
        Const.TASK_IFWAIT[] codes = Const.TASK_IFWAIT.values();
        for (Const.TASK_IFWAIT code : codes) {
            if (ifWait == code.getValue()) {
                flag = true;
            }
        }
        if(flag && ifWait == Const.TASK_IFWAIT.WAITED.getValue()){
            flag = validateCodes(waitCode)[0].equals("1");
        }

        String[] rtn = new String[2];
        rtn[0] = flag ? "1" : "0";
        if(!flag) {
            rtn[1] = "ifWait " + ifWait + " or waitCode " + waitCode + " is illegal";
        }
        return rtn;
    }

    private static String[] validateIfPre(Integer ifPre){
        boolean flag = false;
        Const.TASK_IFPRE[] codes = Const.TASK_IFPRE.values();
        for(int i =0;i<codes.length;i++){
            if(ifPre == codes[i].getValue()){
                flag = true;
            }
        }
        String[] rtn = new String[2];
        rtn[0] = flag ? "1" : "0";
        if(!flag){
            rtn[1] = "ifPre "+ifPre +" is illegal";
        }
        return rtn;
    }


    private static String[] validateTaskType(Integer taskType){
        boolean flag = false;
        Const.TASK_TYPE[] codes = Const.TASK_TYPE.values();
        for(int i =0;i<codes.length;i++){
            if(taskType == codes[i].getValue()){
                flag = true;
            }
        }
        String[] rtn = new String[2];
        rtn[0] = flag ? "1" : "0";
        if(!flag){
            rtn[1] = "taskType "+taskType +" is illegal";
        }
        return rtn;
    }

    private static String[] validateRecall(Integer ifRecall,Integer recallLimit){
        boolean flag = false;
        Const.TASK_IFPRE[] codes = Const.TASK_IFPRE.values();
        for (Const.TASK_IFPRE code : codes) {
            if (ifRecall == code.getValue()) {
                flag = true;
            }
        }
        if(flag && ifRecall == Const.TASK_IFRECALL.RECALL.getValue()){
            flag = recallLimit <= 10;
        }
        String[] rtn = new String[2];
        rtn[0] = flag ? "1" : "0";
        if(!flag){
            rtn[1] = "ifRecall "+ifRecall +" recallLimit "+recallLimit+" is illegal";
        }
        return rtn;
    }

    private static String[] validateCodes(String codes){
        String regex = "(\\d+;?)+";
        //String regex = "^\\d[[\\d]*[;]?]+";
        boolean flag = Pattern.matches(regex,codes);
        String[] rtn = new String[2];
        rtn[0] = flag ? "1" : "0";
        if(!flag){
            rtn[1] = "codes "+codes +" is illegal";
        }
        return rtn;
    }

    private static String[] validateCycle(String cycle){
        boolean flag = false;
        Const.TASK_CYCLE[] codes = Const.TASK_CYCLE.values();
        for (Const.TASK_CYCLE code : codes) {
            if (cycle.equals(code.toString())) {
                flag = true;
            }
        }
        String[] rtn = new String[2];
        rtn[0] = flag ? "1" : "0";
        if(!flag){
            rtn[1] = "cycle "+cycle +" is illegal";
        }
        return rtn;
    }


    public static String[] validateTask(TaskDO task){
        StringBuilder builder = new StringBuilder();

        String[] rtn1 = validateCodes(task.getSuccessCode());
        String[] rtn2 = validateRecall(task.getIfRecall(),task.getRecallLimit());
        String[] rtn3 = validateWait(task.getIfWait(),task.getWaitCode());
        String[] rtn4 = validateTaskType(task.getType());
        String[] rtn5 = validateIfPre(task.getIfPre());
        String[] rtn6 = validateOffsetType(task.getOffsetType(), task.getOffset());
        String[] rtn7 = validateCycle(task.getCycle());

        if(rtn1[1] != null){
            builder.append(rtn1[1]);
        }
        if(rtn2[1] != null){
            builder.append(rtn2[1]);
        }
        if(rtn3[1] != null){
            builder.append(rtn3[1]);
        }
        if(rtn4[1] != null){
            builder.append(rtn4[1]);
        }
        if(rtn5[1] != null){
            builder.append(rtn5[1]);
        }
        if(rtn6[1] != null){
            builder.append(rtn6[1]);
        }
        if(rtn7[1] != null){
            builder.append(rtn7[1]);
        }
        String msg = null;
        if(!StringUtils.isBlank(builder.toString())){
            msg = builder.toString();
        }

        boolean flag =  rtn1[0].equals("1") &&
                rtn2[0].equals("1") &&
                rtn3[0].equals("1") &&
                rtn4[0].equals("1") &&
                rtn5[0].equals("1") &&
                rtn6[0].equals("1") &&
                rtn7[0].equals("1");
        return new String[]{flag?"1":"0",msg};
    }
}
