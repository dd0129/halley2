package com.dianping.data.warehouse.common;

public class Const {
    public static enum JOB_STATUS{
        JOB_FAIL(-1,"FAIL"),JOB_SUCCESS(1,"SUCCESS"),JOB_INIT(0,"INIT"),JOB_RUNNING(2,"RUNNING"),JOB_SUSPEND(3,"SUSPEND"),
        JOB_INTERNAL_ERROR(4,"INTERNAL_ERROR"),JOB_WAIT(5,"WAIT"),JOB_READY(6,"READY"),JOB_TIMEOUT(7,"TIMEOUT"),
        JOB_PRE_ERROR(8,"PRE_ERROR"),JOB_POST_ERROR(9,"POST_ERROR");
        private Integer value;
        private String desc;

        private JOB_STATUS(Integer value,String desc){
            this.value = value;
            this.desc = desc;
        }

        public Integer getValue() {
            return value;
        }
        public String getDesc() {
            return desc;
        }
    }

    public static enum TASK_TYPE{
        WORMHOLE(1),CALCULATE(2);

        private Integer value;
        private TASK_TYPE(Integer value){
            this.value = value;
        }
        public Integer getValue(){
            return this.value;
        }
    }

    public static enum TASK_CYCLE{
        M,D,W,H,mi;
    }

    public static enum TASK_IFWAIT{
        WAITED(1),UNWAITED(0);
        private Integer value;
        private TASK_IFWAIT(Integer value){
            this.value = value;
        }
        public Integer getValue(){
            return this.value;
        }
    }

    public static enum TASK_IFPRE{
        EXISTS_PRE(1),NON_PRE(0);
        private Integer value;
        private TASK_IFPRE(Integer value){
            this.value = value;
        }
        public Integer getValue(){
            return this.value;
        }
    }

    public static enum TASK_IFRECALL{
        RECALL(1),NON_RECALL(0);
        private Integer value;
        private TASK_IFRECALL(Integer value){
            this.value = value;
        }
        public Integer getValue(){
            return this.value;
        }
    }

    public final static String regex = "return code-";
    public final static int INTERNAL_EXECUTE_ERROR = -100;
    public final static int TASK_VALIDATE = 1;
    public final static int TASK_INVALIDATE = 1;
    public final static Integer TASK_TYPE_LOAD = 1;
    public final static Integer TASK_TYPE_CALCULATE =2;

    public final static Integer TASK_EXISTS_PRE  = 1;
    public final static Integer TASK_NONEXISTS_PRE = 0;

    public final static Integer TASK_IF_WAIT = 1;
    public final static String EMPTH_STRING = "";
    public final static Integer DEFAULT_TASK_JOBCODE = -1;

    public final static Integer PRE_HOUR = 2 * 60 * 60 * 1000;

    public final static Integer WAIT_INTERVAL = 5 * 60 * 1000;

    public final static String EXTERNAL_CLASSPATH= "d:/data/deploy";

}
