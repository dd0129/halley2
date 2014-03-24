package com.dianping.data.warehouse.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hongdi.tang on 14-3-15.
 */
public class Resource{
    private Logger logger = LoggerFactory.getLogger(Resource.class);

    private String resourceName;
    private Integer capability;
    private Integer runningNum;

    public Resource(String resourceName, Integer capability, Integer runningNum) {
        this.resourceName = resourceName;
        this.capability = capability;
        this.runningNum = runningNum;
    }

    public Integer getCapability() {
        return capability;
    }

    public void setCapability(Integer capability) {
        this.capability = capability;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }



    public Integer getRunningNum() {
        return runningNum;
    }

    public void setRunningNum(Integer runningNum) {
        this.runningNum = runningNum;
    }

    public void addRunningNum() {
        ++this.runningNum ;
    }

    public void minusRunningNum() {
        --this.runningNum ;
    }

    public Integer add(){
        if(runningNum<capability){
            logger.info(resourceName.concat(" resouce equal" ).concat(String.valueOf(runningNum)));
            return ++runningNum;
        }else{
            logger.info(resourceName.concat(" resouce is full; limit :=" ).concat(String.valueOf(capability)));
            return capability;
        }
    }

    public Integer remove(){
        return --runningNum;
    }
}
