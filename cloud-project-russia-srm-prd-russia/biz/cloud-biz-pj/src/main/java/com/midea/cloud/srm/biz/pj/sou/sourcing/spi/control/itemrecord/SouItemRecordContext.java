package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrecord;

import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 寻源 - 项目需求信息保存所需的上下文数据
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/20
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SouItemRecordContext extends BaseObjectX {

    @ApiModelProperty("寻源单信息")
    protected SouProject project;
    @ApiModelProperty("现有的有效的物料集合")
    protected List<SouItem> existItemList;
    @ApiModelProperty("用户有权限的OU集合")
    /** orgCode */
    protected Map<String, Organization> authOrgMap;
    @ApiModelProperty("物料信息集合")
    /** itemId */
    protected Map<Long, MaterialItem> itemMap;
    @ApiModelProperty("品类信息集合")
    /** categoryCode */
    protected Map<String, PurchaseCategory> categoryMap;
    @ApiModelProperty("刷新批次号")
    protected String newBatchNo;

    private static final ThreadLocal<SouItemRecordContext> CONTEXT_HOLDER = new ThreadLocal<>();
    public static void setContextHolder(SouItemRecordContext context) {
        CONTEXT_HOLDER.set(context);
    }
    public static SouItemRecordContext getContextHolder() {
        return CONTEXT_HOLDER.get();
    }
    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
