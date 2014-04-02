package com.dianping.data.warehouse.halley.external;

/**
 * Created by adima on 14-4-2.
 */
public interface HalleyCallback {
    public void callback(String InstanceId,Integer status);
}
