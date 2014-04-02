package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.halley.common.Param;
import org.codehaus.jackson.map.util.JSONPObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-1.
 */

public class ExternalHandlerImpl implements ExternalExecuter{

    @Override
    public boolean execute(InstanceDO inst ,ExternalDO extTask) {
        try{
            ExternalClassloader loader = new ExternalClassloader(Const.EXTERNAL_CLASSPATH);
            Class clazz = loader.loadClass(extTask.getImplClass());

            Method method = clazz.getDeclaredMethod("run", Map.class);
            Map<String,String> paras = new HashMap<String, String>();

            paras.put(Param.DQC_PARAM.taskId.toString(),String.valueOf(inst.getTaskId()));
            paras.put(Param.DQC_PARAM.scheduleTime.toString(),String.valueOf(inst.getTriggerTime()));
            paras.put(Param.DQC_PARAM.taskStatusId.toString(),inst.getInstanceId());
            String rtnStr = (String)method.invoke(clazz.newInstance(),paras);

        }catch(Exception e){

        }
        return false;
    }

    private List<String> parseRtnStr(){
        JSONPObject obj = parseRtnStr().get(Param.RETURN_STR.code.toString());
    }
}
