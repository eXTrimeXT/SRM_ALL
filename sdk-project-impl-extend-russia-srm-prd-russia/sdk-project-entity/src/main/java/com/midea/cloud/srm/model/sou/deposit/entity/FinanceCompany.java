package com.midea.cloud.srm.model.sou.deposit.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ex_liuxy46
 */
@ApiModel(description = "财务-公司信息")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_finance_company")
public class FinanceCompany extends BaseEntity<FinanceCompany> {

    @ApiModelProperty("主键")
    @TableId("FC_ID")
    private Long fcId;

    @ApiModelProperty("利润中心代码")
    @TableField("PROFIT_CENTER_CODE")
    private String profitCenterCode;

    @ApiModelProperty("利润中心名称")
    @TableField("PROFIT_CENTER_NAME")
    private String profitCenterName;

    @ApiModelProperty("公司名称")
    @TableField("COMPANY_NAME")
    private String companyName;

    @ApiModelProperty("公司编码（SAP）")
    @TableField("COMPANY_CODE")
    private String companyCode;

    @ApiModelProperty("板块编码")
    @TableField("BU_CODE")
    private String buCode;

    @ApiModelProperty("开户户名")
    @TableField("ACCOUNT_NAME")
    private String accountName;

    @ApiModelProperty("账号")
    @TableField("ACCOUNT")
    private String account;

    @ApiModelProperty("成本中心代码")
    @TableField("CENTER_CODE")
    private String centerCode;

    @ApiModelProperty("成本中心名称")
    @TableField("CENTER_NAME")
    private String centerName;

    @ApiModelProperty("司库部门")
    @TableField("SK_DEPT")
    private String skDept;

    @ApiModelProperty("司库部门名称")
    @TableField("SK_DEPT_NAME")
    private String skDeptName;


}
