package com.dianping.data.warehouse.executer;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.dao.InstanceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by adima on 14-3-24.
 */
@Service("waitExecuter")
public class WaitExecuter {

    private static Logger logger = LoggerFactory.getLogger(WaitExecuter.class);
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;

    public void execute(){
        try{
            logger.info("the waitInit starts");
            this.instDAO.updateInstnaceListStatus(Const.JOB_STATUS.JOB_INIT.getValue(),Const.JOB_STATUS.JOB_INIT.getDesc(),Const.JOB_STATUS.JOB_WAIT.getValue());
        }finally{
            logger.info("the waitInit ends");
        }
    }

}
