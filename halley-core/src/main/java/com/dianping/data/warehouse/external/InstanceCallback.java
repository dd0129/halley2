package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.dao.InstanceDAO;
import com.dianping.data.warehouse.halley.external.HalleyCallback;
import com.dianping.pigeon.remoting.provider.config.annotation.Service;

import javax.annotation.Resource;

/**
 * Created by adima on 14-4-2.
 */
@Service
public class InstanceCallback implements HalleyCallback{
    @Resource(name="instanceDAO")
    private InstanceDAO instDAO;
    @Override
    public void callback(String instanceId, Integer status) {
        instDAO.updateInstnaceStatus(instanceId,status,"test");
    }
}
