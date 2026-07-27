package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto.ExtPrSouProjectPlanQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;

import java.util.List;

/**
 * 招标计划 - 项目计划 - 查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
public interface PrSouProjectPlanQueryService {

    /**
     * 列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtPrSouProjectPlan> pagePlans(ExtPrSouProjectPlanQueryDTO queryParam);

    /**
     * 查询项目计划详情
     * @param projectPlanId 参数
     * @return 返回
     */
    ExtPrSouProjectPlan getPlan(long projectPlanId);

}
