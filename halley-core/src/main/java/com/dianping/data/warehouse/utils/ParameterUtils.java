package com.dianping.data.warehouse.utils;


import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.common.GlobalResource;
import com.dianping.data.warehouse.domain.TaskDO;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
