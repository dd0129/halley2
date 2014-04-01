package com.dianping.data.warehouse.dao;

import com.dianping.data.warehouse.domain.ExternalDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by adima on 14-3-22.
 */
@Component("externalDAO")
public interface ExternalDAO {
    public List<ExternalDO> getExternalTasksById(@Param("status") Integer status);

}
