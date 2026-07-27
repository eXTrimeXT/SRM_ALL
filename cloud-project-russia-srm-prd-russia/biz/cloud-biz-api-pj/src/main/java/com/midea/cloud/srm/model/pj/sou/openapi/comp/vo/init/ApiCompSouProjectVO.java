package com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init;

import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 竞价openAPI - 寻源单
 *
 * @author ex_yipeng@partner.midea.com
 * @since 2023/09/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouProjectVO extends SouProject {

    /** @see CompSouProject#getBudgetAmount */
    @ApiModelProperty("预算金额")
    private BigDecimal budgetAmount;

    /** @see CompSouProject#getMinPercent */
    @ApiModelProperty("最小涨/跌幅百分比")
    private BigDecimal minPercent;

    /** @see CompSouProject#getMinAmount */
    @ApiModelProperty("最小涨/跌金额")
    private BigDecimal minAmount;

    /** @see CompSouProject#getExtendTrigger */
    @ApiModelProperty("截止至前几分钟")
    private BigDecimal extendTrigger;

    /** @see CompSouProject#getExtendMinute */
    @ApiModelProperty("延长多少分钟")
    private BigDecimal extendMinute;

    /** @see CompSouProject#getMaxWinVendorCount */
    @ApiModelProperty("中标供应商数量")
    private Integer maxWinVendorCount;

    /** @see CompSouProject#getExchangeRateType */
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    /** @see CompSouProject#getCurrencyExchangeDate */
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

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

    public static List<ApiCompSouProjectVO> convertCompVO(List<SouProject> projectList) {
        if (projectList.isEmpty()) { return Collections.emptyList(); }
        List<ApiCompSouProjectVO> voList; {
            if (projectList instanceof Page) {
                voList = new Page<>();
                ((Page)voList).setTotal(((Page)projectList).getTotal());
                ((Page)voList).setPageSize(((Page)projectList).getPageSize());
                ((Page)voList).setPageNum(((Page)projectList).getPageNum());
            } else {
                voList = new ArrayList<>(projectList.size());
            }
        }
        projectList.forEach(project -> voList.add(SouObjectXUtil.convertTargetObj(project, ApiCompSouProjectVO.class)));
        return voList;
    }

}
