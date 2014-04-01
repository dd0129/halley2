package com.dianping.data.warehouse.external;

import com.dianping.data.warehouse.domain.ExternalDO;

/**
 * Created by hongdi.tang on 14-4-1.
 */

public interface ExternalExecuter {
    boolean execute(ExternalDO extTask);
}
