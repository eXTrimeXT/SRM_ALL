package com.midea.cloud.srm.sou.purinq.plugin.event.order;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ApiPurInqSouOrderDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ApiPurInqSouOrderItemDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderWithdrawDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderEventHandler;
import com.midea.cloud.srm.sou.sourcing.spi.order.editorder.SouOrderEditPO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouOrderEventHandler extends ApiSouOrderEventHandler {

    @Autowired
    private ExtPurInqSouOrderDAO extPurInqSouOrderDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;

    @Override
    public void doHandlerBeforeEditOrder(ApiSouOrderDTO param, String souType) {
        param.setOrderNoGenerateCode(ExtPurInqSouProject.EXT_SEQ_SOU_PURINQ_ORDER_NO);
        if (!param.isTempSave()) {
            LoginAppUser appUser = AppUserUtil.getLoginAppUser();
            param.setSubmitById(appUser.getUserId());
            param.setSubmitBy(appUser.getUsername());
            param.setSubmitByIp(IPUtil.getRemoteIpAddr(HttpServletHolder.getRequest()));
            param.setSubmitFullName(appUser.getNickname());
        }
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
        ApiPurInqSouOrderDTO inqParam = SouObjectXUtil.convertTargetObj(param, ApiPurInqSouOrderDTO.class);

        // 保存额外的询比价报价单
        ExtPurInqSouOrder extInqOrder = new ExtPurInqSouOrder(); {
            extInqOrder.setOrderId(po.getSouOrder().getOrderId());
            extInqOrder.setProjectId(param.getProjectId());
            extInqOrder.setOrderByNickname(StringUtils.trimToNull(inqParam.getOrderByNickname()));
            if (!inqParam.isTempSave()) {
                AssertUtils.notNull(extInqOrder.getOrderByNickname(), "请填写报价人");
                AssertUtils.isTrue(extInqOrder.getOrderByNickname().length() <= 50, "报价人的输入长度不能超过50");
            }
            extInqOrder.setOrderPhone(StringUtils.trimToNull(inqParam.getOrderPhone()));
            if (!inqParam.isTempSave()) {
                AssertUtils.notNull(extInqOrder.getOrderPhone(), "请填写报价电话");
                AssertUtils.isTrue(extInqOrder.getOrderPhone().length() <= 50, "报价电话的输入长度不能超过50");
            }
            extInqOrder.setOrderEmail(StringUtils.trimToNull(inqParam.getOrderEmail()));
            if (!inqParam.isTempSave()) {
                AssertUtils.notNull(extInqOrder.getOrderPhone(), "请填写报价邮箱");
                AssertUtils.isTrue(extInqOrder.getOrderPhone().length() <= 150, "报价邮箱的输入长度不能超过150");
            }
        }
        extPurInqSouOrderDAO.lambdaUpdate().eq(ExtPurInqSouOrder::getOrderId, po.getSouOrder().getOrderId()).remove();
        extPurInqSouOrderDAO.save(extInqOrder);

        // 忽略账期处理(长城这边没有账期)

        // 保存 询比价供应商报价行信息 - 这里是做数据转换
        Map<Long/* orderItemId */, SouOrderItem> orderItemMap = po.getOrderItemList().stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
        List<ExtPurInqSouOrderItem> inqOrderItemList = new ArrayList<>();
        for (ApiPurInqSouOrderItemDTO dto : inqParam.getOrderItemList()) {
            ExtPurInqSouOrderItem entity = new ExtPurInqSouOrderItem();
            inqOrderItemList.add(entity);

            SouOrderItem orderItem = orderItemMap.get(dto.getOrderItemId());

            entity.setOrderId(inqParam.getOrderId());
            entity.setOrderItemId(dto.getOrderItemId());
            entity.setProjectId(dto.getProjectId());
            entity.setSouItemId(dto.getSouItemId());

            // 发票类型
            entity.setInvoiceType(StringUtils.trimToNull(dto.getInvoiceType()));
            // 价税合计
            if (dto.getOrderTaxPrice() != null) {
                entity.setPriceTaxTotal(dto.getOrderTaxPrice().multiply(orderItem.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP));
            }
            // 供货周期
            entity.setExtLeadTime(dto.getExtLeadTime());
            AssertUtils.isTrue(param.isTempSave() || entity.getExtLeadTime() != null, "请填写供货周期");
            if (entity.getExtLeadTime() != null) {
                AssertUtils.isTrue(entity.getExtLeadTime() >= 0, "供货周期不能小于0");
            }
            // 保修期
            entity.setExtWarrantyPeriod(dto.getExtWarrantyPeriod());
            AssertUtils.isTrue(param.isTempSave() || entity.getExtWarrantyPeriod() != null, "请填写质保期");
            if (entity.getExtWarrantyPeriod() != null) {
                AssertUtils.isTrue(entity.getExtWarrantyPeriod() > 0, "质保期必须大于0");
            }
            // 是否最新报价
            entity.setLatestPriceTag(param.isTempSave() ? Enable.N : Enable.Y);
        }

        // 保存物料需求行信息
        extPurInqSouOrderItemDAO.saveOrUpdate(inqParam.getOrderId(), inqOrderItemList, ExtPurInqSouOrderItem::getOrderId);
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
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
