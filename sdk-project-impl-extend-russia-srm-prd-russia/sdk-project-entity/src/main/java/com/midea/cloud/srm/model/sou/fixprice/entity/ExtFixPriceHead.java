package com.midea.cloud.srm.model.sou.fixprice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 定价单 - 头表
 * @author huangbf3
 */
@Data
@TableName("scc_npm_sou_fix_price_head")
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceHead extends BaseEntity<ExtFixPriceHead> {

    @TableId("FIX_PRICE_HEAD_ID")
    @ApiModelProperty("ID")
    private Long fixPriceHeadId;

    @TableField("FIX_PRICE_NO")
    @ApiModelProperty("定价单号")
    private String fixPriceNo;

    @TableField("FIX_PRICE_DATE")
    @ApiModelProperty("申请日期")
    private LocalDate fixPriceDate;

    /**
     * 前段传递，谁创建就是谁
     */
    @TableField("ORG_DEP_ID")
    @ApiModelProperty("采购部门ID")
    private Long orgDepId;

    @TableField("ORG_DEP_CODE")
    @ApiModelProperty("采购部门编码")
    private String orgDepCode;

    @TableField("ORG_DEP_NAME")
    @ApiModelProperty("采购部门名称")
    private String orgDepName;

    /** @see ExtFixPriceStatusEnum */
    @TableField("FIX_PRICE_STATUS")
    @ApiModelProperty("定价状态")
    private String fixPriceStatus;

    @TableField("TOTAL_NOTAX_PRICE")
    @ApiModelProperty("总金额(未税)")
    private BigDecimal totalNotaxPrice;

    @TableField("TOTAL_TAX_PRICE")
    @ApiModelProperty("价税合计(含税)")
    private BigDecimal totalTaxPrice;

    @TableField("HIGHEST_TAX_PRICE")
    @ApiModelProperty("最高含税单价")
    private BigDecimal highestTaxPrice;

    @TableField("REMARK")
    @ApiModelProperty("备注")
    private String remark;

    @TableField("CAN_SUBMIT")
    @ApiModelProperty("是否可以提交")
    private Enable canSubmit;

    @TableField("CREATE_USER_ORG_OU_ID")
    @ApiModelProperty("创建人所在公司ID")
    private Long createUserOrgOuId;

    @TableField("CREATE_USER_ORG_OU_CODE")
    @ApiModelProperty("创建人所在公司编码")
    private String createUserOrgOuCode;

    @TableField("CREATE_USER_ORG_OU_NAME")
    @ApiModelProperty("创建人所在公司名称")
    private String createUserOrgOuName;

    @TableField("CREATE_USER_ORG_BU_ID")
    @ApiModelProperty("创建人所在板块ID")
    private Long createUserOrgBuId;

    @TableField("CREATE_USER_ORG_BU_CODE")
    @ApiModelProperty("创建人所在板块编码")
    private String createUserOrgBuCode;

    @TableField("CREATE_USER_ORG_BU_NAME")
    @ApiModelProperty("创建人所在板块名称")
    private String createUserOrgBuName;

    @TableField("DC")
    @ApiModelProperty("是否代采")
    private String dc;

    @TableField("START_BPM_USERNAME")
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;

}
