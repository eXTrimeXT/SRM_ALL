package com.midea.cloud.srm.model.pj.base.organization.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  组织设置 模型
 * </pre>
 *
 * @author huanglj50@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: Jan 4, 2022 12:39:23 PM
 *  修改内容:
 * </pre>
 */

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_org_company_address")
@ApiModel(description = "组织设置")
@QlMatchType("OrgCompanyAddress")
public class OrgCompanyAddress extends BaseEntity {
  private static final long serialVersionUID = 317843L;

  @ApiModelProperty(value = "地址ID")
  @TableId("COMPANY_ADDRESS_ID")
  private Long companyAddressId;

  @ApiModelProperty(value = "组织ID")
  @TableField("ORGANIZATION_ID")
  private Long organizationId;

  @ApiModelProperty(value = "国家")
  @TableField("COUNTRY")
  private String country;

  @ApiModelProperty(value = "地区")
  @TableField("AREA")
  private String area;

  @ApiModelProperty(value = "地区")
  @TableField("CITY")
  private String city;

  @ApiModelProperty(value = "详细地址")
  @TableField("ADDRESS")
  private String address;

  @ApiModelProperty(value = "联系电话")
  @TableField("PHONE")
  private String phone;

  @ApiModelProperty(value = "邮政编码")
  @TableField("POSTAL_CODE")
  private String postalCode;

  @ApiModelProperty(value = "地址备注")
  @TableField("REMARK")
  private String remark;

  @ApiModelProperty(value = "是否激活(Y-是,N-否)")
  @TableField("IS_ACTIVE")
  private String isActive;

  @ApiModelProperty(value = "地址区域")
  @TableField("ADDRESS_REGION")
  private String addressRegion;

  @ApiModelProperty(value = "是否默认(Y-是,N-否)")
  @TableField("IS_DEFAULT")
  private String isDefault;
}