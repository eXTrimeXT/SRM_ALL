package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@ApiModel(description = "集采台账")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_ledger")
public class SccSouChLedger extends BaseEntity<SccSouChLedger> {

    @ApiModelProperty("台账id")
    @TableId("LEDGER_ID")
    private Long ledgerId;

    @ApiModelProperty("项目名称")
    @TableField("PROJECT_NAME")
    private String projectName;

    @ApiModelProperty("合同日期从")
    @TableField("CONTRACT_START_DATE")
    private Date contractStartDate;

    @ApiModelProperty("合同日期到")
    @TableField("CONTRACT_END_DATE")
    private Date contractEndDate;

    @ApiModelProperty("负责人")
    @TableField("HEAD_PERSON")
    private String headPerson;

    @ApiModelProperty("项目总金额（万元）")
    @TableField("PROJECT_TOTAL_MONEY")
    private BigDecimal projectTotalMoney;

    @ApiModelProperty("项目金额到")
    @TableField(exist = false)
    private BigDecimal projectTotalMoneyEnd;

    @ApiModelProperty("延期原因")
    @TableField("DELAY_REASON")
    private String delayReason;

    @ApiModelProperty("下轮项目建议及注意事项")
    @TableField("NEXT_SUGGEST")
    private String nextSuggest;

    @ApiModelProperty("本次新增项目数")
    @TableField("ADD_NUM")
    private Integer addNum;

    @ApiModelProperty("原临采年采购额(万元)")
    @TableField("ADD_BEFORE_MONEY")
    private BigDecimal addBeforeMoney;

    @ApiModelProperty("集采后年采购额(万元)")
    @TableField("ADD_AFTER_MONEY")
    private BigDecimal addAfterMoney;

    @ApiModelProperty("本次递减金额(万元)")
    @TableField("ADD_DECREMENT_MONEY")
    private BigDecimal addDecrementMoney;

    @ApiModelProperty("成本递减比例(%)")
    @TableField("ADD_DECREMENT_RATIO")
    private BigDecimal addDecrementRatio;

    @ApiModelProperty("上期/集采前采购额(万元)")
    @TableField("ABO_BEFORE_MONEY")
    private BigDecimal aboBeforeMoney;

    @ApiModelProperty("集采后年采购额(万元)")
    @TableField("ABO_AFTER_MONEY")
    private BigDecimal aboAfterMoney;

    @ApiModelProperty("本次递减金额(万元)")
    @TableField("ABO_DECREMENT_MONEY")
    private BigDecimal aboDecrementMoney;

    @ApiModelProperty("成本递减比例(%)")
    @TableField("ABO_DECREMENT_RATIO")
    private BigDecimal aboDecrementRatio;

    @ApiModelProperty("项目轮次")
    @TableField(exist = false)
    private Integer num;

    @ApiModelProperty("到期月份")
    @TableField(exist = false)
    private String expirationMonth;

    @ApiModelProperty("数据来源")
    @TableField("DATA_SOURCE")
    private String dataSource;

    @ApiModelProperty("创建人公司ID")
    @TableField("HR_COMPANY_ID")
    private Long hrCompanyId;


    @ApiModelProperty("创建人公司代码")
    @TableField("HR_COMPANY_CODE")
    private String hrCompanyCode;


    @ApiModelProperty("创建人板块名称")
    @TableField("HR_COMPANY_NAME")
    private String hrCompanyName;


    @ApiModelProperty("创建人板块ID")
    @TableField("HR_SECTOR_ID")
    private Long hrSectorId;


    @ApiModelProperty("创建人板块代码")
    @TableField("HR_SECTOR_CODE")
    private String hrSectorCode;


    @ApiModelProperty("创建人板块名称")
    @TableField("HR_SECTOR_NAME")
    private String hrSectorName;


    @ApiModelProperty("创建人部门ID")
    @TableField("HR_DEPT_ID")
    private Long hrDeptId;


    @ApiModelProperty("创建人部门代码")
    @TableField("HR_DEPT_CODE")
    private String hrDeptCode;


    @ApiModelProperty("创建人部门名称")
    @TableField("HR_DEPT_NAME")
    private String hrDeptName;


}
