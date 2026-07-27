package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editrequrie;

import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 寻源 - 项目需求信息保存所需的上下文数据
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/20
 */
@Data
@NoArgsConstructor
public class SouRequireEditContext {

    /** 寻源单信息 */
    protected SouProject project;
    /** 用户有权限的OU集合 orgCode */
    protected Map<String, Organization> authOrgMap;
    /** 物料信息集合 itemId */
    protected Map<Long, MaterialItem> itemMap;
    /** 品类信息集合 categoryCode */
    protected Map<String, PurchaseCategory> categoryMap;
    /** 现有的物料需求 souItemId */
    protected Map<Long, SouItem> existSouItemMap;
    /** 报价模板 tempId */
    protected Map<Long, SouQuoteTemp> quoteTempMap;

    private static final ThreadLocal<SouRequireEditContext> CONTEXT_HOLDER = new ThreadLocal<>();
    public static void setContextHolder(SouRequireEditContext context) {
        CONTEXT_HOLDER.set(context);
    }
    public static SouRequireEditContext getContextHolder() {
        return CONTEXT_HOLDER.get();
    }
    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
