package com.dianping.data.warehouse.utils;


import com.dianping.data.warehouse.common.GlobalResource;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-5.
 */
public class ParameterUtils {
    public static String resourceParamHandle(String para){
        for(Map.Entry<String,String> entry : GlobalResource.ENV_PROPS.entrySet()){
            para = StringUtils.replace(para,entry.getKey(), entry.getValue());
        }
        return para;
    }
}
