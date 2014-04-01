package com.dianping.data.warehouse.external.impl;

import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.external.ExternalExecuter;
import com.dianping.data.warehouse.halley.external.PigeonClient;
import com.dianping.data.warehouse.utils.JacksonHelper;
import com.dianping.pigeon.remoting.provider.config.annotation.Service;
import javassist.ClassPool;
import javassist.CtClass;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-1.
 */
@Service
public class PigeonHandler implements ExternalExecuter{

    @Override
    public boolean execute(ExternalDO extTask) {
        try{
            String s = null;

            Class clazz = Class.forName(extTask.getImplClass());

            Method method = clazz.getDeclaredMethod("run", Map.class);
            List<String> list = JacksonHelper.jsonToPojo(extTask.getParameterMap(),Map.class);

            String rtnStr = (String)method.invoke(PigeonClient.class, list);

            CtClass ctClass = ClassPool.getDefault().get(extTask.getImplClass());
            ctClass.getDeclaredMethod("run",new CtClass[]{ClassPool.getDefault().get("java.util.Map")});

        }catch(Exception e){

        }
        return false;
    }
}
