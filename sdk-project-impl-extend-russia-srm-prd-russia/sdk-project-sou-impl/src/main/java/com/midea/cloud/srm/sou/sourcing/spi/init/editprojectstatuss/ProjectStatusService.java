package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import io.swagger.annotations.ApiModel;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("单据状态接口")
public interface ProjectStatusService {
    /**
     * 备注
     * @return 返回
     */
    public ApiProjectStatusRangeVo<?> currentStatus();
}
