package com.midea.cloud.srm.model.pj.base.organization.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author huangbf3
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_org_invoice_info")
@ApiModel(description = "组织开票信息")
@QlMatchType("OrgInvoiceInfo")
public class OrgInvoiceInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("INVOICE_ID")
    private Long invoiceId;

    @ApiModelProperty(value = "组织ID")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;

    @ApiModelProperty(value = "发票类别")
    @TableField("INVOICE_TYPE")
    private String invoiceType;

    @ApiModelProperty(value = "公司名称")
    @TableField("COMPANY_NAME")
    private String companyName;

    @ApiModelProperty(value = "开户行名称")
    @TableField("OPENING_NAME")
    private String openingName;

    @ApiModelProperty(value = "开户行账号")
    @TableField("OPENING_ACCOUNT")
    private String openingAccount;

    @ApiModelProperty(value = "纳税人识别号")
    @TableField("TAXPAYER_NUM")
    private String taxpayerNum;

    @ApiModelProperty(value = "电话")
    @TableField("PHONE")
    private String phone;

    @ApiModelProperty(value = "地址")
    @TableField("ADDRESS")
    private String address;

}
