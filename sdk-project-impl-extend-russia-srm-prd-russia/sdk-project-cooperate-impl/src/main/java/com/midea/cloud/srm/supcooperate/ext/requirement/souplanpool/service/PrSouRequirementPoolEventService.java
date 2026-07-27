package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service;

import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementCancelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementCancelUnPassDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolAssignDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolCreateSouDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCreateSouVO;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 招标计划池 - 事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
public interface PrSouRequirementPoolEventService {

    /**
     * 批量分配/转办
     * @param param 分配数据
     */
    void batchAssign(ExtPrSouRequirementPoolAssignDTO param);

    /**
     * 编辑招标计划取消单
     * @param param 参数
     * @return 返回
     */
    ExtPrSouRequirementCancelDTO editReqCancel(ExtPrSouRequirementCancelDTO param);

    /**
     * 删除招标取消单据
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     * @return 返回
     */
    @Nullable
    ExtPrSouRequirementCancelVO removeReqCancel(long requirementCancelId);

    /**
     * 变更招标计划
     * @param param 参数
     * @return 返回
     */
    ExtPrSouRequirementHeadDTO changeSouPlan(ExtPrSouRequirementHeadDTO param);

    /**
     * 供应商推荐
     * @param params 参数
     * @return 返回
     */
    @Nullable
    ApiExtSouRecommVendorInfoDTO createVendorRecommend(List<ExtPrSouRequirementHeadDTO> params);

    /**
     * 创建寻源需求
     * @param params 参数
     * @return 返回
     */
    RecordDTO createSouReq(List<ExtPrSouRequirementHeadDTO> params);

    /**
     * 创建寻源
     * @param param 参数
     * @return 返回
     */
    ExtPrSouRequirementCreateSouVO createSou(ExtPrSouRequirementPoolCreateSouDTO param);

    /**
     * 招标计划取消审批提交后的回调处理
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     */
    void callbackAfterCancelApprovalSubmit(long requirementCancelId);

    /**
     * 招标计划取消审批通过后的回调处理
     * @param requirementCancelId {@link ExtPrSouRequirementCancel#getRequirementCancelId}
     */
    void callbackAfterCancelApprovalPass(long requirementCancelId);

    /**
     * 招标计划取消审批未通过后的回调处理
     * @param param 回调参数
     */
    void callbackAfterCancelApprovalUnPass(ExtPrSouRequirementCancelUnPassDTO param);

    /**
     * 备注
     * @param param 参数
     * @return 返回
     */
    ExtPrSouRequirementCreateSouVO createBidSou(ExtPrSouRequirementPoolCreateSouDTO param);
}
