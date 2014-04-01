package com.dianping.data.warehouse.halley.external;

import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-24.
 */
public interface PigeonClient {
    public String run(Map<String,String> commands);



}
