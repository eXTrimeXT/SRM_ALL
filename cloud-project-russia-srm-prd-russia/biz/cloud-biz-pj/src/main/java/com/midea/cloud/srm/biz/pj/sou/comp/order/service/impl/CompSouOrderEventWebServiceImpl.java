package com.midea.cloud.srm.biz.pj.sou.comp.order.service.impl;

import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.biz.pj.sou.comp.order.domain.CompSouOrderDomainService;
import com.midea.cloud.srm.biz.pj.sou.comp.order.service.CompSouOrderEventWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.service.SouOrderEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderJudgeHandler;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.order.ApiCompSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderWithdrawDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 竞价 - 报价业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouOrderEventWebServiceImpl implements CompSouOrderEventWebService {

    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private CompSouOrderDomainService compSouOrderDomainService;
    @Autowired
    private SouOrderEventService souOrderEventService;
    @Autowired
    private SouControlEventService souControlEventService;

    /**
     * 提供一个计算公式报价结果的接口，让界面体验更友好
     *
     * @param souItemId     物料需求行ID
     * @param currency      供应商选择的报价币种
     * @param taxKey        供应商选择的税率
     * @param formulaResult 公式报价
     * @param vendorId      供应商ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ApiCompSouOrderItemVO computeFormulaPrice(long souItemId, String currency, String taxKey, String formulaResult, long vendorId) {
        SouItem souItem = souItemDao.getById(souItemId);
        AssertUtils.notNull(souItem, LocaleHandler.getLocaleMsg("物料需求信息[{0}]不存在"), souItemId);
        // 1. 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(SouTypeEnum.comp.name(), ApiSouOrderJudgeHandler.class)
                .judgeGetProjectAuth(souItem.getProjectId(), vendorId, SouTypeEnum.comp.name());
        // 2. 构造数据
        ApiCompSouOrderItemVO orderItem = new ApiCompSouOrderItemVO(); {
            orderItem.setSouItemId(souItemId);
            orderItem.setOrderCurrency(currency);
            orderItem.setTaxKey(taxKey);
            orderItem.setFormulaResult(formulaResult);
        }
        // 3. 计算公式得到未税价信息
        compSouOrderDomainService.computeFormulaPrice(souItem.getProjectId(), Collections.singletonList(orderItem),
                null, null, vendorId);
        // 4. 得到含税价信息
        compSouOrderDomainService.computeTaxPriceAndStandardPrice(souItem.getProjectId(), Collections.singletonList(orderItem),
                null, null);

        // 5. 返回数据
        return orderItem;
    }

    /**
     * 暂存/提交报价
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public long/* orderId */ editOrder(ApiCompSouOrderDTO param) {
        // 0: 刷新数据
        if (param.getProjectId() != null) {
            souControlEventService.refreshProjectBySouTime(param.getProjectId());
        }
        // 1: 入参格式化
        param.formatParams();
        // 2: 提交人信息填补
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        param.setSubmitById(param.isTempSave() ? null : user.getUserId());
        param.setSubmitBy(param.isTempSave() ? null : user.getUsername());
        param.setSubmitByIp(param.isTempSave() ? null : IPUtil.getRemoteIpAddr(HttpServletHolder.getRequest()));
        param.setSubmitFullName(param.isTempSave() ? null : user.getNickname());
        // 3: 保存报价数据
        ApiSouOrderDTO apiParam = SouObjectXUtil.convertTargetObj(param, ApiSouOrderDTO.class);
        apiParam.setOrderNoGenerateCode(SequenceCodeConstant.SOU.SEQ_COMP_ORDER_NO);
        try {
            souOrderEventService.editOrder(apiParam, SouTypeEnum.comp.name());
        } catch (IllegalArgumentException e) {
            String text = "物料需求第";
            if (e.getMessage().startsWith(text)) {
                throw new IllegalArgumentException(e.getMessage().substring(7), e);
            } else {
                throw e;
            }
        }

        return apiParam.getOrderId();
    }

    @Override
    public long initOrder(ApiCompSouOrderDTO param) {

        // 1: 入参格式化
        param.formatParams();
        // 2: 保存报价数据
        ApiSouOrderDTO apiParam = SouObjectXUtil.convertTargetObj(param, ApiSouOrderDTO.class);
        apiParam.setOrderNoGenerateCode(SequenceCodeConstant.SOU.SEQ_COMP_ORDER_NO);
        try {
            souOrderEventService.initOrder(apiParam);
        } catch (IllegalArgumentException e) {
            String text = "物料需求第";
            if (e.getMessage().startsWith(text)) {
                throw new IllegalArgumentException(e.getMessage().substring(7), e);
            } else {
                throw e;
            }
        }

        return apiParam.getOrderId();
    }

    /**
     * 供应商：撤回报价
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void withdrawOrder(ApiSouOrderWithdrawDTO param) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(param.getProjectId());
        // 1: 撤回报价
        souOrderEventService.withdrawOrder(param, SouTypeEnum.comp.name());
    }

    /**
     * 作废报价
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelOrder(ApiSouOrderCancelDTO param) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(param.getProjectId());
        // 1: 作废报价
        souOrderEventService.cancelOrder(param, SouTypeEnum.comp.name());
    }

}
