package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/**
 * 招标计划 - 项目计划 - 事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
public interface PrSouProjectPlanEventService {

    /**
     * 编辑项目计划
     * @param param 参数
     * @return 返回
     */
    ExtPrSouProjectPlan editPlan(ExtPrSouProjectPlan param);

    /**
     * 删除项目计划
     * @param projectPlanId 参数
     * @return 返回
     */
    ExtPrSouProjectPlan removePlan(long projectPlanId);

    /**
     * 用于招标计划绑定项目计划的回调
     * @param projectPlanId 参数
     * @param requirementHeadId 参数
     */
    void bindPlan(long projectPlanId, long requirementHeadId);

    /**
     * 用于招标计划解绑项目计划的回调
     * @param projectPlanId 参数
     * @param requirementHeadId 参数
     */
    void unbindPlan(@Nullable Long projectPlanId, @Nullable Long requirementHeadId);

    /**
     * excel导入项目计划
     * @param file 参数
     * @param sceneType 参数
     * @return 返回
     */
    @Nullable
    Long importProjectPlansExcel(MultipartFile file, String sceneType);

}
