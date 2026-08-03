package com.midea.cloud.srm.biz.pj.sou.sourcing.select.service;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.*;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto.PriceApprovalDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

import java.util.List;
import java.util.Set;

/**
 * 寻源核心 - 评选事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/12
 */
public interface SouSelectEventService {

    /**
     * 智能评选
     * @param param 智能评选信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void intelligentSelect(ApiSouIntelligentSelectDTO param, String souType);

    /**
     * 入围/淘汰
     * @param param 需要操作的数据
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return 受影响的报价行数据 {@link SouOrderItem#getOrderItemId}
     */
    Set<Long/* orderItemId */> changeWinStatus(ApiSouChangeWinStatusDTO param, String souType);

    /**
     * 中标/落标
     * PS: 同组合下需要级联处理
     * @param param
     * @param souType
     * @return 受影响的报价行数据 {@link SouOrderItem#getOrderItemId}
     */
    Set<Long/* orderItemId */> changeSelectStatus(ApiSouChangeSelectStatusDTO param, String souType);

    /**
     * 修改中标数量
     * @param params 中标数量信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void changeWinAmount(List<ApiSouChangeWinAmountDTO> params, String souType);

    /**
     * 公开本轮结果
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void openResult(long projectId, String souType);

    /**
     * 生成价格审批单
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return
     */
    ApprovalHeader createPricingApproval(long projectId, String souType);

    /**
     * 新的生成价格审批单(对接回迁SRM的价格审批单)
     * @param param
     * @return
     */
    PriceApprovalDTO createPricingApprovalNew(ApiSouCreatePricingApprovalDTO param);

    /**
     * 根据价格审批单的审批情况更新寻源单状态
     * @param param
     * @param souType
     */
    void changePricingResult(ApiSouSelectChangePricingResultDTO param, String souType);

    /**
     * 归档
     * @param param
     * @param name
     */
    void placeOnFile(ApiSouPlaceOnFileDTO param, String name);

    /**
     * 中标通知
     * @param param
     * @param name
     */
    void changeProjectStatus(ApiSouChangeSelectStatusDTO param, String name);
}
