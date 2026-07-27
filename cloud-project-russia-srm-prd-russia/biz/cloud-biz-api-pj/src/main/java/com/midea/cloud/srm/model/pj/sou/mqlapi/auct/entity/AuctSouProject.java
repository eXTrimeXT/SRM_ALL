package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouProject;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums.AuctSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums.AuctSouRuleEnum;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums.AuctSouScopeRuleEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源MQL - 竞价
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/08
 */
@Data
@TableName("scc_sou_auct_project")
@EqualsAndHashCode(callSuper = true)
public class AuctSouProject extends ExtAuctSouProject {

    /** @see SouProject#getProjectId */
    @TableId("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see AuctSouProjectStatusEnum */
    @TableField("EXT_PROJECT_STATUS")
    private String extProjectStatus;

    /** @see AuctSouRuleEnum */
    @TableField("AUCT_RULE")
    @ApiModelProperty("竞价规则(枚举类:SouAuctRuleEnum)(字典:SOU_AUCT_RULE)")
    private String auctRule;

    /** 需配合 {@link AuctSouRuleEnum#NO_ALLOW_SAME_PRICE} 使用才有效 */
    @TableField("NO_ALLOW_SAME_PRICE_COUNT")
    @ApiModelProperty("前几名不允许相同规则(需配合SouAuctRuleEnum#NO_ALLOW_SAME_PRICE使用才有效)")
    private Integer noAllowSamePriceCount;

    @TableField("MIN_PERCENT")
    @ApiModelProperty("最小涨/跌幅百分比")
    private BigDecimal minPercent;

    @TableField("MIN_AMOUNT")
    @ApiModelProperty("最小涨/跌金额")
    private BigDecimal minAmount;

    /** @see AuctSouScopeRuleEnum */
    @TableField("SCOPE_RULE")
    @ApiModelProperty("公开规则(枚举类:SouAuctScopeRuleEnum)(字典:SOU_AUCT_SCOPE_RULE)")
    private String scopeRule;

    @TableField("ALLOW_EXTEND_TIME")
    @ApiModelProperty("是否允许自动延长竞价时间(Y/N)")
    private Enable allowExtendTime;

    @TableField("EXTEND_TRIGGER")
    @ApiModelProperty("竞价延时触发点(分钟)")
    private BigDecimal extendTrigger;

    @TableField("EXTEND_MINUTE")
    @ApiModelProperty("竞价延长时间(分钟)")
    private BigDecimal extendMinute;

    @TableField("EXTEND_MAX_ORDER_COUNT")
    @ApiModelProperty("竞价延时期间最多报价次数")
    private Integer extendMaxOrderCount;

    @TableField("EXTEND_TRIGGER_COUNT")
    @ApiModelProperty("延时触发的最大次数")
    private Integer extendTriggerCount;

    @TableField("EXTEND_MAX_MINUTE")
    @ApiModelProperty("首次延时触发后最长延时时间限制")
    private BigDecimal extendMaxMinute;

    @TableField("BOND_AMOUNT")
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    @TableField("BOND_DESC")
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    @TableField("BOND_METHOD")
    @ApiModelProperty("商务要求 -- 保证金提交方式[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    @TableField("BOND_END_TIME")
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    @TableField("BANK_ACCOUNT_NUM")
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    @TableField("BANK_ACCOUNT_NAME")
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    @TableField("BANK_BRANCH_NAME")
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    @TableField("EXCLUDE_BLACK_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商")
    private Enable excludeBlackVendors;

    @TableField("EXCLUDE_NO_CURRENT_ORG_VENDORS")
    @ApiModelProperty("推荐控制 -- 排除非本业务实体供应商")
    private Enable excludeNoCurrentOrgVendors;

    @TableField("EXCLUDE_ORG_QUIT_VENDORS")
    @ApiModelProperty("推荐控制 -- 排除业务实体退出供应商")
    private Enable excludeOrgQuitVendors;

    @TableField("EXCLUDE_ORG_CATEGORY_STATUS")
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商")
    private String excludeOrgCategoryStatus;

    /**
     * 原始的报价截止时间，不会随着竞价延时机制的时间延长而改变
     * 而{@link SouProject#getOrderEndTime}会随着延时机制的实际延长而改变
     */
    @TableField("ORIGIN_ORDER_END_TIME")
    @ApiModelProperty("原始报价截止时间")
    private Date originOrderEndTime;

}
