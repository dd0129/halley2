package com.dianping.data.warehouse.halley.common;


import java.util.Map;


/**
 * Created by hongdi.tang on 14-4-1.
 */
public class Param {
    public static enum DQC_PARAM{
        taskId,scheduleTime,taskStatusId
    }

    public static enum RETURN_STR{
        code,message
    }

    public static enum CODES{
        SUCCESS(300),FAIL(301),EXCEPTION(500);
        private Integer code;
        private CODES(Integer code){
            this.code = code;
        }
        public Integer getCode(){
            return this.code;
        }
    }

    public static Map<String,String> parseRtnInfo(String rtnStr){
        //JSONObject resJson = JSONObject.fromObject(rtnStr);
        return null;
    }
}
