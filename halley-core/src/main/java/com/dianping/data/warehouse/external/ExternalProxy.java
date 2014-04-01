package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.common.Const;
import com.dianping.data.warehouse.dao.ExternalDAO;
import com.dianping.data.warehouse.domain.ExternalDO;
import com.dianping.data.warehouse.domain.InstanceDO;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-1.
 */
public class ExternalProxy {

    private Map<String,ExternalExecuter> externalExecuterMap;

    @Resource(name="externalDAO")
    private ExternalDAO externalDAO;

    public void run(InstanceDO inst){
        List<ExternalDO> list = this.externalDAO.getExternalTasksById(Const.TASK_VALIDATE);
        for(ExternalDO extTask : list){
            ExternalExecuter executer = externalExecuterMap.get(extTask.getServiceType());
            executer.execute(extTask);
        }
    }


}
