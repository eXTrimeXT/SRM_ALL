package com.midea.cloud.srm.model.pj.base.organization.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.SexEnum;
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
 *  修改日期: Jan 4, 2022 12:39:51 PM
 *  修改内容:
 * </pre>
 */

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_org_company_person")
@ApiModel(description = "组织设置")
@QlMatchType("OrgCompanyPerson")
public class OrgCompanyPerson extends BaseEntity {
  private static final long serialVersionUID = 313674L;

  @ApiModelProperty(value = "联系人ID")
  @TableId("COMPANY_PERSON_ID")
  private Long companyPersonId;

  @ApiModelProperty(value = "组织ID")
  @TableField("ORGANIZATION_ID")
  private Long organizationId;

  @ApiModelProperty(value = "账号")
  @TableField("USERNAME")
  private String username;

  @ApiModelProperty(value = "联系人姓名")
  @TableField("NAME")
  private String name;

  @ApiModelProperty(value = "性别(M-男,F-女)")
  @TableField("SEX")
  private SexEnum sex;

  @ApiModelProperty(value = "部门")
  @TableField("DEPARTMENT")
  private String department;

  @ApiModelProperty(value = "职位")
  @TableField("POSITION")
  private String position;

  @ApiModelProperty(value = "联系电话")
  @TableField("PHONE")
  private String phone;

  @ApiModelProperty(value = "邮箱")
  @TableField("EMAIL")
  private String email;

  @ApiModelProperty(value = "是否默认联系人,只能有一个默认(Y-是,N-否)")
  @TableField("IS_DEFAULT")
  private Enable isDefault;

  @ApiModelProperty(value = "备注")
  @TableField("REMARK")
  private String remark;

  @ApiModelProperty(value = "是否激活(Y-是,N-否)")
  @TableField("IS_ACTIVE")
  private Enable isActive;
}