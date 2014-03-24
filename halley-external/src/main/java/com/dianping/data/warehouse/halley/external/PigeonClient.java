package com.dianping.data.warehouse.halley.external;

import java.util.List;

/**
 * Created by hongdi.tang on 14-3-24.
 */
public interface PigeonClient {
    public String run(List<String> command);
}
