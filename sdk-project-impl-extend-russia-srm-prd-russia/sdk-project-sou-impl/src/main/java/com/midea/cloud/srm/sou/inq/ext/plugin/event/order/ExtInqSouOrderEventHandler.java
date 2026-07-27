package com.midea.cloud.srm.sou.inq.ext.plugin.event.order;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.order.ApiInqSouOrderDTO;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.order.ApiInqSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderWithdrawDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.inq.spi.order.InqSouOrderEventHandler;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.order.editorder.SouOrderEditPO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouOrderEventHandler extends InqSouOrderEventHandler {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private ExtPjInqSouOrderDAO extPjInqSouOrderDAO;

    @Override
    public void doHandlerBeforeEditOrder(ApiSouOrderDTO param, String souType) {
        super.doHandlerBeforeEditOrder(param, souType);

        if (CollectionUtils.isNotEmpty(param.getOrderItemList())) {
            for (ApiSouOrderItemDTO orderItem : param.getOrderItemList()) {
                orderItem.setOrderCurrency("RMB");
            }
        }
    }

    @Override
    @ApiOperation("暂存/提交报价单后的额外处理")
    public void doHandlerAfterEditOrder(ApiSouOrderDTO param, String souType, SouOrderEditPO po) {
        ApiInqSouOrderDTO inqParam = SouObjectXUtil.convertTargetObj(param, ApiInqSouOrderDTO.class);

        // 保存额外的询比价报价单
        ExtPjInqSouOrder extInqOrder = new ExtPjInqSouOrder(); {
            extInqOrder.setOrderId(po.getSouOrder().getOrderId());
            extInqOrder.setProjectId(param.getProjectId());
            extInqOrder.setPriceActiveDay(inqParam.getPriceActiveDay());
            if (!inqParam.getIsTempSave()) {
                AssertUtils.notNull(extInqOrder.getPriceActiveDay(), "请填写报价有效期");
            }
            extInqOrder.setExtOrderByNickname(StringUtils.trimToNull(inqParam.getExtOrderByNickname()));
            if (!inqParam.getIsTempSave()) {
                AssertUtils.notNull(extInqOrder.getExtOrderByNickname(), "请填写报价人");
                AssertUtils.isTrue(extInqOrder.getExtOrderByNickname().length() <= 50, "报价人的输入长度不能超过50");
            }
            extInqOrder.setExtOrderPhone(StringUtils.trimToNull(inqParam.getExtOrderPhone()));
            if (!inqParam.getIsTempSave()) {
                AssertUtils.notNull(extInqOrder.getExtOrderPhone(), "请填写报价电话");
                AssertUtils.isTrue(extInqOrder.getExtOrderPhone().length() <= 50, "报价电话的输入长度不能超过80");
            }
        }
        extPjInqSouOrderDAO.lambdaUpdate().eq(ExtPjInqSouOrder::getOrderId, po.getSouOrder().getOrderId()).remove();
        extPjInqSouOrderDAO.save(extInqOrder);

        // 忽略账期处理(长城这边没有账期)

        // 保存 询比价供应商报价行信息 - 这里是做数据转换
        List<InqSouOrderItem> inqOrderItemList = new ArrayList<>();
        for (ApiInqSouOrderItemDTO dto : inqParam.getOrderItemList()) {
            InqSouOrderItem entity = new InqSouOrderItem();
            inqOrderItemList.add(entity);

            entity.setOrderId(inqParam.getOrderId());
            entity.setOrderItemId(dto.getOrderItemId());
            entity.setIsFormula(Enable.N);
            entity.setFormulaAttrValues("");

            // 发票类型
            entity.setInvoiceType(StringUtils.trimToNull(dto.getInvoiceType()));
            // 价税合计
            if (dto.getOrderTaxPrice() != null) {
                entity.setPriceTaxTotal(dto.getOrderTaxPrice().multiply(new BigDecimal(dto.getX(SouObjectXUtil.getFieldByLambda(SouOrderItem::getRequireQuantity)).toString()))
                        .setScale(4, RoundingMode.HALF_UP));
            }
            // 预付款说明
            entity.setAdvancePaymentRemark(dto.getAdvancePaymentRemark());
            if (entity.getAdvancePaymentRemark() == null) {
                entity.setAdvancePaymentRemark(Enable.N);
            }
            // 特殊付款说明
            entity.setSpecialPaymentRemark(StringUtils.trimToNull(dto.getSpecialPaymentRemark()));
            // 供货周期
            entity.setExtLeadTime(dto.getExtLeadTime());
            AssertUtils.isTrue(param.isTempSave() || entity.getExtLeadTime() != null, "请填写供货周期");
            if (entity.getExtLeadTime() != null) {
                AssertUtils.isTrue(entity.getExtLeadTime() >= 0, "供货周期不能小于0");
            }
            // 保修期
            entity.setExtWarrantyPeriod(dto.getExtWarrantyPeriod());
            AssertUtils.isTrue(param.isTempSave() || entity.getExtWarrantyPeriod() != null, "请填写质保期");
            /*if (entity.getExtWarrantyPeriod() != null) {
                AssertUtils.isTrue(entity.getExtWarrantyPeriod() > 0, "质保期必须大于0");
            }*/
            // 是否最新报价
            entity.setLatestPriceTag(param.isTempSave() ? Enable.N : Enable.Y);
        }

        // 保存物料需求行信息
        inqSouOrderItemDAO.saveOrUpdate(inqParam.getOrderId(), inqOrderItemList, InqSouOrderItem::getOrderId);
    }

    @Override
    @ApiOperation("撤回报价后的额外处理")
    public void doHandlerAfterWithdrawOrder(ApiSouOrderWithdrawDTO param, String souType) {
        super.doHandlerAfterWithdrawOrder(param, souType);
        // 报价单撤回后，当前轮次报价设置未非最新报价
        SouProject souProject = souProjectDAO.getById(param.getProjectId());
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, param.getProjectId())
                .eq(SouOrderItem::getVendorId, param.getVendorId())
                .list();
        Set<Long> currentRoundOrderItemIds = orderItemList.stream().filter(e -> e.getRound().equals(souProject.getCurrentRound())).map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());
        if (!currentRoundOrderItemIds.isEmpty()) {
            inqSouOrderItemDAO.lambdaUpdate()
                    .set(InqSouOrderItem::getLatestPriceTag, Enable.N)
                    .in(InqSouOrderItem::getOrderItemId, currentRoundOrderItemIds)
                    .update();
        }
    }

    @Override
    @ApiOperation("作废报价后的额外处理")
    public void doHandlerAfterCancelOrder(ApiSouOrderCancelDTO param, String souType) {
        super.doHandlerAfterCancelOrder(param, souType);
        // 报价单作废后，当前轮次报价设置未非最新报价
        SouProject souProject = souProjectDAO.getById(param.getProjectId());
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, param.getProjectId())
                .eq(SouOrderItem::getVendorId, param.getVendorId())
                .list();
        Set<Long> currentRoundOrderItemIds = orderItemList.stream().filter(e -> e.getRound().equals(souProject.getCurrentRound())).map(SouOrderItem::getOrderItemId).collect(Collectors.toSet());
        if (!currentRoundOrderItemIds.isEmpty()) {
            inqSouOrderItemDAO.lambdaUpdate()
                    .set(InqSouOrderItem::getLatestPriceTag, Enable.N)
                    .in(InqSouOrderItem::getOrderItemId, currentRoundOrderItemIds)
                    .update();
        }
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
