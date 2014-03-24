package com.dianping.data.warehouse.common;

public class Const {
    public static enum JOB_STATUS{
        JOB_FAIL(-1),JOB_SUCCESS(1),JOB_INIT(0),JOB_RUNNING(2),JOB_SUSPEND(3),JOB_INIT_ERROR(4),
        JOB_WAIT(5),JOB_READY(6),JOB_TIMEOUT(7),JOB_PRE_ERROR(8),JOB_POST_ERROR(9);
        private Integer value;
        private String desc;

        private JOB_STATUS(){}
        private JOB_STATUS(Integer value){
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
        public String getDesc() {
            return desc;
        }
    }

    public final static int RUNTIME_LOGFILE_NOTFOUND = -100;
    public final static int TASK_VALIDATE = 1;
    public final static int TASK_INVALIDATE = 1;
    public final static Integer TASK_TYPE_LOAD = 1;
    public final static Integer TASK_TYPE_CALCULATE =2;

    public final static Integer TASK_EXISTS_PRE  = 1;
    public final static Integer TASK_NONEXISTS_PRE = 0;
}
