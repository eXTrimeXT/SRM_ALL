package com.midea.cloud.srm.biz.pj.sou.comp.select.service;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.*;

import java.util.List;

/**
 * 竞价 - 评选事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
public interface CompSouSelectEventWebService {

    /**
     * 智能评选
     * PS: 计算得分、将单据状态置为'评选中'
     * @param param
     */
    void intelligentSelect(ApiSouIntelligentSelectDTO param);

    /**
     * 入围/淘汰
     * PS: 同组合下需要级联处理
     * @param param
     */
    void changeWinStatus(ApiSouChangeWinStatusDTO param);

    /**
     * 中标/落标
     * PS: 同组合下需要级联处理
     * @param param
     */
    void changeSelectStatus(ApiSouChangeSelectStatusDTO param);

    /**
     * 修改中标数量
     * @param params
     */
    void changeWinAmount(List<ApiSouChangeWinAmountDTO> params);

    /**
     * 公开本轮结果
     * @param projectId
     */
    void openResult(long projectId);

    /**
     * 采购商：生成价格审批单
     * @param projectId
     * @return
     */
    ApprovalHeader createPricingApproval(long projectId);

    /**
     * 根据价格审批单的审批情况更新竞价单状态
     * @param param
     */
    void changePricingResult(ApiSouSelectChangePricingResultDTO param);

    /**
     * 归档
     * @param param
     */
    void placeOnFile(ApiSouPlaceOnFileDTO param);

    /**
     * 备注
     * @param param
     */
    void changeProjectStatus(ApiSouChangeSelectStatusDTO param);
}
