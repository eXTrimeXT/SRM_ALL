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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
 *  修改日期: Jan 4, 2022 9:04:54 AM
 *  修改内容:
 * </pre>
 */

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_org_company")
@ApiModel(description = "组织设置")
@QlMatchType("OrgCompany")
public class OrgCompany extends BaseEntity {
  private static final long serialVersionUID = 821250L;

  @ApiModelProperty(value = "组织公司Id")
  @TableId("ORG_COMPANY_ID")
  private Long orgCompanyId;

  @ApiModelProperty(value = "组织ID")
  @TableField("ORGANIZATION_ID")
  private Long organizationId;

  @ApiModelProperty(value = "公司ID")
  @TableField("COMPANY_ID")
  private String companyId;

  @ApiModelProperty(value = "公司名称")
  @TableField("COMPANY_NAME")
  private String companyName;

  @ApiModelProperty(value = "税号")
  @TableField("TAX_NUMBER")
  private String taxNumber;

  @ApiModelProperty(value = "公司代码")
  @TableField("COMPANY_CODE")
  private String companyCode;

  @ApiModelProperty(value = "公司类型")
  @TableField("COMPANY_TYPE")
  private String companyType;

  @ApiModelProperty(value = "帐套ID")
  @TableField("SET_OF_BOOKS_ID")
  private Long setOfBooksId;

  @ApiModelProperty(value = "有效日期从")
  @TableField("START_DATE")
  private Date startDate;

  @ApiModelProperty(value = "有效日期到")
  @TableField("END_DATE")
  private Date endDate;

  @ApiModelProperty(value = "排序")
  @TableField("SORT")
  private Long sort;

  @ApiModelProperty(value = "启用标识")
  @TableField("ENABLED_FLAG")
  private Enable enabledFlag;

  @ApiModelProperty(value = "邮箱")
  @TableField("EMAIL")
  private String email;

}