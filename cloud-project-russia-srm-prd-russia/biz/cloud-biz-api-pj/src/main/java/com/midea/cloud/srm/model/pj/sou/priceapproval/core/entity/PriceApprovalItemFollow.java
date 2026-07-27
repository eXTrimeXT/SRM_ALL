package com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.pj.sou.model.entity.ExtPriceApprovalItemFollow;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.enums.PriceApprovalItemFollowStatusEnum;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.enums.PriceApprovalItemFollowTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 价格审批单 - 中标行后续单据记录表
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/01
 */
@Data
@ApiModel("价格审批单-中标行后续单据记录表")
@TableName("scc_price_approval_item_follow")
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalItemFollow extends ExtPriceApprovalItemFollow {

    /**
     * 考虑场景: 多个价格审批单明细生成一个合同、如果价格审批单明细的中标数量1w，出现指定数量分批次创建合同 TODO
      */
    @TableId("APPROVAL_ITEM_FOLLOW_ID")
    private Long approvalItemFollowId;

    /** @see PriceApprovalItem#getApprovalItemId */
    @TableField("APPROVAL_ITEM_ID")
    @ApiModelProperty("价格审批单明细ID")
    private Long approvalItemId;

    /** @see PriceApprovalItem#getApprovalId */
    @TableField("APPROVAL_ID")
    @ApiModelProperty("价格审批单ID")
    private Long approvalId;

    /** @see PriceApprovalItemFollowTypeEnum */
    @TableField("FOLLOW_TYPE")
    @ApiModelProperty("后续单据类型")
    private String followType;

    /** @see PriceApprovalItemFollowStatusEnum */
    @TableField("FOLLOW_STATUS")
    @ApiModelProperty("后续单据状态")
    private String followStatus;

    @TableField("FOLLOW_ID")
    @ApiModelProperty("后续单据ID")
    private String followId;

    @TableField("FOLLOW_NO")
    @ApiModelProperty("后续单据编号")
    private String followNo;

    @TableField("FOLLOW_NAME")
    @ApiModelProperty("后续单据名称")
    private String followName;

    @TableField("FOLLOW_LINE_ID")
    @ApiModelProperty("后续单据明细ID")
    private String followLineId;

    @TableField("FOLLOW_QUANTITY")
    @ApiModelProperty("后续单据明细分配数量")
    private BigDecimal followQuantity;

}
