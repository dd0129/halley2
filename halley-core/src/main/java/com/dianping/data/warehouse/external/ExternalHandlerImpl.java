package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.halley.common.Param;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-1.
 */

public class ExternalHandlerImpl implements ExternalExecuter{
    private static Logger logger = LoggerFactory.getLogger(ExternalHandlerImpl.class);
    public Integer execute(InstanceDO inst ,ExternalDO extTask) {
        try{
            ExternalClassloader loader = new ExternalClassloader(Const.EXTERNAL_CLASSPATH);
            Class clazz = loader.loadClass(extTask.getImplClass());

            Method method = clazz.getDeclaredMethod("run", Map.class);
            Map<String,String> paras = new HashMap<String, String>();

            paras.put(Param.DQC_PARAM.taskId.toString(),String.valueOf(inst.getTaskId()));
            paras.put(Param.DQC_PARAM.scheduleTime.toString(),String.valueOf(inst.getTriggerTime()));
            paras.put(Param.DQC_PARAM.taskStatusId.toString(),inst.getInstanceId());

            String rtnStr = (String)method.invoke(clazz.newInstance(),paras);
            JSONObject rtnJson = JSONObject.fromObject(rtnStr);
            String code = (String)rtnJson.get("code");
            String message = (String)rtnJson.get("message");
            this.writeLogFile(inst,message);
            Const.JOB_STATUS[] values = Const.JOB_STATUS.values();
            for(Const.JOB_STATUS status: values){
                if(status.getExtCode().equals(Integer.valueOf(code))){
                    return status.getValue();
                }
            }
            throw new NullPointerException(code + "is illegal return code,it not match any codes");
        }catch(Exception e){
            logger.error("external call error",e);
            return Const.JOB_STATUS.JOB_SUCCESS.getValue();
        }
    }

    private void writeLogFile(InstanceDO inst,String msg){
    }

//    private List<String> parseRtnStr(){
//        JSONPObject obj = parseRtnStr().get(Param.RETURN_STR.code.toString());
//    }
}
