package com.midea.cloud.srm.sou.purinq.plugin.event.init.editrequire;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.sou.sourcing.spi.init.editrequrie.ApiSouRequireEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editrequrie.SouRequireEditContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
public class ExtPurInqSouRequireEditHandler extends ApiSouRequireEditHandler {

    @Override
    protected void formatAndValidateItems(@Nullable List<ApiSouItemDTO> params, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isEmpty(params)) {
            AssertUtils.isTrue(isTempSave || isCopy, "缺少物料需求信息");
            return;
        }
        SouProject project = SouRequireEditContext.getContextHolder().getProject();
        Map<String/* orgCode */, Organization> authOrgMap = SouRequireEditContext.getContextHolder().getAuthOrgMap();
        Map<Long/* itemId */, MaterialItem> itemMap = SouRequireEditContext.getContextHolder().getItemMap();
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = SouRequireEditContext.getContextHolder().getCategoryMap();

        int index = 0;
        for (ApiSouItemDTO param : params) {
            index++;
            // 1: ID(略)
            // 2: 业务实体
            param.setOrgOuCode(null);
            // 3: 库存组织
            param.setOrgInvCode(null);
            // 9: 物料组合
            if (SouOrderWayEnum.COMBINED.equals(project.getOrderWay())) {
                param.setItemGroup(StringUtils.trimToNull(param.getItemGroup()));
                AssertUtils.isTrue(param.getItemGroup() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料组合"), index);
                if (param.getItemGroup() != null) {
                    AssertUtils.isTrue(param.getItemGroup().length() <= 30, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料组合的长度不能超过30"), index);
                }
            } else {
                param.setItemGroup(null);
            }
            // 10: 是否无料号物料
            if (param.getNoCodeItem() == null) {
                param.setNoCodeItem(Enable.N);
            }
            // 11: 物料ID
            boolean isNoCodeItem = Enable.Y.equals(param.getNoCodeItem());
            if (isNoCodeItem) {
                param.setItemId(null);
            } else {
                if (param.getItemId() == null) {
                    AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请选择物料"), index);
                } else {
                    MaterialItem item = itemMap.get(param.getItemId());
                    AssertUtils.notNull(item, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行的物料")+"[{1}]"+LocaleHandler.getLocaleMsg("不存在"), index, param.getItemId());
                }
            }
            // 13: 物料名称
            if (isNoCodeItem) {
                param.setItemDesc(StringUtils.trimToNull(param.getItemDesc()));
                AssertUtils.isTrue(param.getItemDesc() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入物料名称"), index);
                if (param.getItemDesc() != null) {
                    AssertUtils.isTrue(param.getItemDesc().length() <= 200, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行物料名称的长度不能超过200"), index);
                }
            } else {
                param.setItemDesc(null);
            }
            // 14: 品类ID(置空)
            param.setCategoryId(null);
            // 15: 品类编码
            if (isNoCodeItem) {
                param.setCategoryCode(StringUtils.trimToNull(param.getCategoryCode()));
                AssertUtils.isTrue(param.getCategoryCode() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第{0}行请选择品类"), index);
                if (param.getCategoryCode() != null) {
                    AssertUtils.isTrue(categoryMap.containsKey(param.getCategoryCode()), LocaleHandler.getLocaleMsg("品类[{0}]不存在"), param.getCategoryCode());
                }
            }  else {
                param.setCategoryCode(null);
            }
            // 16: 品类名称(置空)
            param.setCategoryName(null);
            // 17: 需求数量
            AssertUtils.isTrue(param.getRequireQuantity() != null || isTempSave, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行请输入需求数量"), index);
            if (param.getRequireQuantity() != null) {
                AssertUtils.isTrue(param.getRequireQuantity().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行需求数量必须大于0"), index);
            }
            // 18: 需求时间(略 - 无限制)
            // 19: 是否阶梯价
            if (SouOrderTypeEnum.SIMPLE.equals(project.getOrderType())) {
                // 普通报价
                if (param.getIsLadder() == null) {
                    param.setIsLadder(Enable.N);
                }
            } else {
                param.setIsLadder(Enable.N);
            }
            // 20: 预计采购金额
            if (param.getBuyAmount() != null) {
                AssertUtils.isTrue(param.getBuyAmount().compareTo(BigDecimal.ZERO) >= 0, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行预计采购金额不能小于0"), index);
            }
            // 21: 价格有效期范围
            param.setPriceStartTime(null);
            param.setPriceEndTime(null);
            // 25: 备注
            param.setRemark(StringUtils.trimToNull(param.getRemark()));
            if (param.getRemark() != null) {
                AssertUtils.isTrue(param.getRemark().length() <= 300, LocaleHandler.getLocaleMsg("物料需求列表第")+"{0}"+LocaleHandler.getLocaleMsg("行备注长度不能超过300"), index);
            }
            // 26: 来源类型
            param.setSourceFromType(project.getSourceFromType());
            // 27: 来源单据ID/号
            if (!SouSourceFromTypeEnum.PURCHASE_REQ.name().equals(param.getSourceFromType())) {
                param.setSourceFromId(project.getSourceFromId());
                param.setSourceFromNo(project.getSourceFromNo());
            }
            // 28: 来源单据行ID/号
            if (SouSourceFromTypeEnum.HAND_MAKE.name().equals(param.getSourceFromType())) {
                param.setSourceFromLineId(null);
                param.setSourceFromLineNo(null);
            } else {
                SouItem existSouItem = SouRequireEditContext.getContextHolder().getExistSouItemMap().get(param.getSouItemId());
                if (existSouItem != null) {
                    param.setSourceFromLineId(existSouItem.getSourceFromLineId());
                    param.setSourceFromLineNo(existSouItem.getSourceFromLineNo());
                }
            }
            // 29: 排序
            param.setSortIndex(index);
        }
    }

    @Override
    protected List<SouItem> doConvertItems(long projectId, List<ApiSouItemDTO> params, boolean isCopy) {
        List<SouItem> itemList = super.doConvertItems(projectId, params, isCopy);
        itemList.forEach(item -> {
            if (item.getItemId() != null) {
                MaterialItem mi = SouRequireEditContext.getContextHolder().getItemMap().get(item.getItemId());
                if (mi != null) {
                    item.setUnit(mi.getUnit());
                }
            }
        });
        return itemList;
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
