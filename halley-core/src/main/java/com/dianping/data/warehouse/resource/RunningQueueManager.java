package com.dianping.data.warehouse.resource;

import com.dianping.data.warehouse.domain.InstanceDO;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adima on 14-3-24.
 */
public class RunningQueueManager {
    private static Map<String, InstanceDO> runningQueue = new ConcurrentHashMap<String, InstanceDO>();
    private RunningQueueManager(){};

    public static synchronized boolean isDuplicateTask(Integer taskId){
        for(InstanceDO inst : runningQueue.values()){
            return inst.getTaskId() == taskId;
        }
        return false;
    }

    public static boolean idDupliateInstance(String instanceId){
        return runningQueue.containsKey(instanceId);
    }

    public static void inQueue(InstanceDO inst){
        runningQueue.put(inst.getInstanceId(), inst);
    }

    public static void outQueue(InstanceDO inst){
        runningQueue.remove(inst.getInstanceId());
    }

    public static Integer size(){
        return runningQueue.size();
    }

    public static Collection<InstanceDO> values(){
        return runningQueue.values();
    }

    public static Set<Map.Entry<String, InstanceDO>> entrySet(){
        return runningQueue.entrySet();
    }
}
