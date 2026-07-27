package com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞价openAPI - 项目信息保存
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouProjectEditDTO extends ApiSouProjectEditDTO {

    /** @see CompSouProject#getBudgetAmount */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("预算金额")
    private BigDecimal budgetAmount= BigDecimal.valueOf(0);

    /** @see CompSouProject#getMinPercent */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("最小涨/跌幅百分比")
    private BigDecimal minPercent= BigDecimal.valueOf(0);

    /** @see CompSouProject#getMinAmount */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("最小涨/跌金额")
    private BigDecimal minAmount= BigDecimal.valueOf(0);

    /** @see CompSouProject#getExtendTrigger */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("截止至前几分钟")
    private BigDecimal extendTrigger= BigDecimal.valueOf(0);

    /** @see CompSouProject#getExtendMinute */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("延长多少分钟")
    private BigDecimal extendMinute= BigDecimal.valueOf(0);

    /** @see CompSouProject#getMaxWinVendorCount */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("中标供应商数量")
    private Integer maxWinVendorCount=0;

    /** @see CompSouProject#getExchangeRateType */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType="COMPANY";

    /** @see CompSouProject#getCurrencyExchangeDate */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate=new Date();

    /** @see CompSouProject#getBondAmount */
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    /** @see CompSouProject#getBondDesc */
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    /** @see CompSouProject#getBondMethod */
    @ApiModelProperty("商务要求 -- 保证金提交方式[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    /** @see CompSouProject#getBondEndTime */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    /** @see CompSouProject#getBankAccountNum */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    /** @see CompSouProject#getBankAccountName */
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    /** @see CompSouProject#getBankBranchName */
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    /** @see CompSouProject#getRequireDesc */
    @ApiModelProperty("需求简述")
    protected String requireDesc;

}
