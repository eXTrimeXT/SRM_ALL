package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.SouItemTypeHandler;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouItemRefreshStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouItemRefreshTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 寻源核心 - 物料需求行
 * PS: 未进行物料需求变更前，不记录任何数据。
 * PS: 全量记录，新增/现有保持/删除
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@ApiModel(description = "寻源核心 - 物料需求变更记录表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "scc_sou_item_record", autoResultMap = true)
public class SouItemRecord extends BaseEntity<SouItemRecord> {

    @TableId("RECORD_ID")
    @ApiModelProperty("ID")
    private Long recordId;

    /** @see SouProject#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("BATCH_NO")
    @ApiModelProperty("批次号")
    private String batchNo;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    @TableField("REFRESH_TYPE")
    @ApiModelProperty("刷新类型(字典:SOU_ITEM_REFRESH_TYPE)")
    private SouItemRefreshTypeEnum refreshType;

    @TableField("REFRESH_STATUS")
    @ApiModelProperty("刷新状态(字典:SOU_ITEM_REFRESH_STATUS)")
    private SouItemRefreshStatusEnum refreshStatus;

    @TableField("REFRESH_ROUND")
    @ApiModelProperty("执行物料刷新的轮次")
    private Integer refreshRound;

    @TableField("REFRESH_TIME")
    @ApiModelProperty("执行物料刷新的时间")
    private Date refreshTime;

    // ----------------------------------------------------- 组织信息(冗余-方便展示) ----------------------------------------------------
    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getOrgOuId */
    @ApiModelProperty("业务实体ID")
    @TableField("ORG_OU_ID")
    private Long orgOuId;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getOrgOuCode */
    @ApiModelProperty("业务实体编码")
    @TableField("ORG_OU_CODE")
    private String orgOuCode;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getOrgOuName */
    @ApiModelProperty("业务实体名称")
    @TableField("ORG_OU_NAME")
    private String orgOuName;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getOrgInvId */
    @ApiModelProperty("库存组织ID")
    @TableField("ORG_INV_ID")
    private Long orgInvId;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getOrgInvCode */
    @ApiModelProperty("库存组织编码")
    @TableField("ORG_INV_CODE")
    private String orgInvCode;

    /** @see com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem#getOrgInvName */
    @ApiModelProperty("库存组织名称")
    @TableField("ORG_INV_NAME")
    private String orgInvName;

    /** 物料信息(冗余-方便展示) */
    @ApiModelProperty("物料ID")
    @TableField("ITEM_ID")
    private Long itemId;

    @ApiModelProperty("物料编码")
    @TableField("ITEM_CODE")
    private String itemCode;

    @ApiModelProperty("物料名称")
    @TableField("ITEM_DESC")
    private String itemDesc;

    /** @see MaterialItem#getUnit */
    @ApiModelProperty("单位")
    @TableField("UNIT")
    private String unit;

    @ApiModelProperty("品类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty("品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    @ApiModelProperty("品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

    /** 物料信息(整体缓存) */
    @ApiModelProperty("物料缓存信息")
    @TableField(value = "ITEM_INFO", typeHandler = SouItemTypeHandler.class)
    private SouItem itemInfo;

}
