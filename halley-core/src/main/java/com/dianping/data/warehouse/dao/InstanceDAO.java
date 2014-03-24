package com.dianping.data.warehouse.dao;

import com.dianping.data.warehouse.domain.InstanceDO;
import com.dianping.data.warehouse.domain.InstanceRelaDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by adima on 14-3-23.
 */
@Component("instanceDAO")
public interface InstanceDAO {
    public void saveInstance(InstanceDO inst);

    public void saveInstanceRela(List<InstanceRelaDO> list);

    public InstanceDO getInstanceInfo(@Param("instanceId") String instanceId);

    public List<InstanceDO> getReadyTaskList(@Param("status") Integer status,@Param("triggerTime") Long triggerTime);

    public Integer updateTaskReady(@Param("instanceId")String instanceId,@Param("status") Integer status);

    public List<InstanceDO> getRelaInstanceList(@Param("instanceId") String instanceId);
}
