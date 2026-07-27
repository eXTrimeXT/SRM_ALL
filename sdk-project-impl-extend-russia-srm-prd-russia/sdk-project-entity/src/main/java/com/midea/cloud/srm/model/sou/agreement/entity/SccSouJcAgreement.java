package com.midea.cloud.srm.model.sou.agreement.entity;

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
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采协议管理")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_jc_agreement")
public class SccSouJcAgreement extends BaseEntity<SccSouJcAgreement> {

    @ApiModelProperty("集采协议id")
    @TableId("AGREEMENT_ID")
    private Long agreementId;

    @ApiModelProperty("协议编码")
    @TableField("AGREEMENT_CODE")
    private String agreementCode;

    @ApiModelProperty("公司主体,必须带有发票信息的类型为公司的组织")
    @TableField("COMPANY_ID")
    private Long companyId;
    @ApiModelProperty("公司主体编码")
    @TableField("COMPANY_CODE")
    private String companyCode;
    @ApiModelProperty("公司主体名称")
    @TableField("COMPANY_NAME")
    private String companyName;

    @ApiModelProperty("协议名称")
    @TableField("AGREEMENT_NAME")
    private String agreementName;

    @ApiModelProperty("供应区域")
    @TableField("SUPPLY_AREA")
    private String supplyArea;

    @ApiModelProperty("付款条款")
    @TableField("PAYMENT")
    private String payment;

    @ApiModelProperty("供应商")
    @TableField("SUP_ID")
    private Long supId;
    @ApiModelProperty("供应商编码")
    @TableField("SUP_CODE")
    private String supCode;
    @ApiModelProperty("供应商名称")
    @TableField("SUP_NAME")
    private String supName;

    @ApiModelProperty("交易方式")
    @TableField("TRADING")
    private String trading;

    @ApiModelProperty("采购员id")
    @TableField("BUY_PERSON_ID")
    private Long buyPersonId;
    @ApiModelProperty("采购员编码")
    @TableField("BUY_PERSON_CODE")
    private String buyPersonCode;
    @ApiModelProperty("采购员名称")
    @TableField("BUY_PERSON_NAME")
    private String buyPersonName;

    @ApiModelProperty("定价方式")
    @TableField("PRICING_WAY")
    private String pricingWay;

    @ApiModelProperty("发票类型")
    @TableField("INVOICE_TYPE")
    private String invoiceType;

    @ApiModelProperty("付款方式")
    @TableField("PAY_WAY")
    private String payWay;

    @ApiModelProperty("采购协议状态,拟定、待执行、执行中、已终止、已失效")
    @TableField("AGREEMENT_STATUS")
    private String agreementStatus;

    @ApiModelProperty("有效开始时间")
    @TableField("EFFECTIVE_START_DATE")
    private Date effectiveStartDate;

    @ApiModelProperty("有效结束时间")
    @TableField("EFFECTIVE_END_DATE")
    private Date effectiveEndDate;

    @ApiModelProperty("币种")
    @TableField("CURRENCY_TYPE")
    private String currencyType;

    @ApiModelProperty("协议附件id")
    @TableField("AGREEMENT_FILE_ID")
    private String agreementFileId;
    @ApiModelProperty("协议附件名称")
    @TableField("AGREEMENT_FILE_NAME")
    private String agreementFileName;

    @ApiModelProperty("协议类型")
    @TableField("AGREEMENT_TYPE")
    private String agreementType;

    @ApiModelProperty("最小起订金额")
    @TableField("MIX_AMOUNT")
    private BigDecimal mixAmount;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty("变更版本")
    @TableField("CHANGE_VERSION")
    private Integer changeVersion;

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


    @ApiModelProperty("部门ID")
    @TableField("HR_DEPT_ID")
    private Long hrDeptId;


    @ApiModelProperty("部门代码")
    @TableField("HR_DEPT_CODE")
    private String hrDeptCode;


    @ApiModelProperty("部门名称")
    @TableField("HR_DEPT_NAME")
    private String hrDeptName;

    @ApiModelProperty("终止原因")
    @TableField("STOP_REASON")
    private String stopReason;

    @ApiModelProperty("协议信息")
    @TableField(exist = false)
    private List<SccSouJcAgreementInfo> sccSouJcAgreementInfoList;

    @ApiModelProperty("采购组织")
    @TableField(exist = false)
    private List<SccSouJcAgreementOrg> sccSouJcAgreementOrgList;

    @ApiModelProperty("是否默认全部组织，是Y，否N")
    @TableField(exist = false)
    private String defaultAll;

    @ApiModelProperty("废弃原因")
    @TableField("DISCARD_REASON")
    private String discardReason;
}
