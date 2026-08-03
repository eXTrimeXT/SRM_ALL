package com.midea.cloud.srm.biz.pj.sou.comp.select.service.impl;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.biz.pj.sou.comp.select.service.CompSouSelectEventWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectEventService;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouBusinessOpenDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目式询价 - 评选事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/25
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouSelectEventWebServiceImpl implements CompSouSelectEventWebService {

    @Autowired
    private SouSelectEventService souSelectEventService;
    @Autowired
    private SouControlEventService souControlEventService;

    /**
     * 智能评选
     * PS: 计算得分、将单据状态置为'评选中'
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void intelligentSelect(ApiSouIntelligentSelectDTO param) {
        ApiSouBusinessOpenDTO businessOpenDTO = new ApiSouBusinessOpenDTO(); {
            businessOpenDTO.setProjectId(param.getProjectId());
            businessOpenDTO.setCurrentUserId(AppUserUtil.getLoginAppUser().getUserId());
        }
        souControlEventService.businessOpen(businessOpenDTO, SouTypeEnum.comp.name());
        souSelectEventService.intelligentSelect(param, SouTypeEnum.comp.name());
    }

    /**
     * 入围/淘汰
     * PS: 同组合下需要级联处理
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeWinStatus(ApiSouChangeWinStatusDTO param) {
        souSelectEventService.changeWinStatus(param, SouTypeEnum.comp.name());
    }

    /**
     * 中标/落标
     * PS: 同组合下需要级联处理
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeSelectStatus(ApiSouChangeSelectStatusDTO param) {
        souSelectEventService.changeSelectStatus(param, SouTypeEnum.comp.name());
    }

    /**
     * 采购商端: 修改中标数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeWinAmount(List<ApiSouChangeWinAmountDTO> params) {
        souSelectEventService.changeWinAmount(params, SouTypeEnum.comp.name());
    }

    /**
     * 公开本轮结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void openResult(long projectId) {
        souSelectEventService.openResult(projectId, SouTypeEnum.comp.name());
    }

    /**
     * 采购商：生成价格审批单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ApprovalHeader createPricingApproval(long projectId) {
        return souSelectEventService.createPricingApproval(projectId, SouTypeEnum.comp.name());
    }

    /**
     * 根据价格审批单的审批情况更新竞价单状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changePricingResult(ApiSouSelectChangePricingResultDTO param) {
        souSelectEventService.changePricingResult(param, SouTypeEnum.comp.name());
    }

    /**
     * 归档
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void placeOnFile(ApiSouPlaceOnFileDTO param) {
        souSelectEventService.placeOnFile(param, SouTypeEnum.comp.name());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeProjectStatus(ApiSouChangeSelectStatusDTO param) {
        souSelectEventService.changeProjectStatus(param, SouTypeEnum.comp.name());
    }

}
