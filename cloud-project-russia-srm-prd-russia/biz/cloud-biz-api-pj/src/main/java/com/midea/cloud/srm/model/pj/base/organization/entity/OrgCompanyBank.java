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
 *  修改日期: Jan 4, 2022 12:39:38 PM
 *  修改内容:
 * </pre>
 */

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_org_company_bank")
@ApiModel(description = "组织设置")
@QlMatchType("OrgCompanyBank")
public class OrgCompanyBank extends BaseEntity {
  private static final long serialVersionUID = 266763L;

  @ApiModelProperty(value = "公司账户ID")
  @TableId("COMPANY_BANK_ID")
  private Long companyBankId;

  @ApiModelProperty(value = "组织ID")
  @TableField("ORGANIZATION_ID")
  private Long organizationId;

  @ApiModelProperty(value = "银行编号")
  @TableField("BANK_NUM")
  private String bankNum;

  @ApiModelProperty(value = "银行名称")
  @TableField("BANK_NAME")
  private String bankName;

  @ApiModelProperty(value = "分行编号")
  @TableField("BRANCH_BANK_NUM")
  private String branchBankNum;

  @ApiModelProperty(value = "开户行名称")
  @TableField("BRANCH_BANK_NAME")
  private String branchBankName;

  @ApiModelProperty(value = "账户名称")
  @TableField("ACCOUNT_NAME")
  private String accountName;

  @ApiModelProperty(value = "银行账号")
  @TableField("BANK_ACCOUNT")
  private String bankAccount;

  @ApiModelProperty(value = "是否主账号,只能有一个主账号(Y-是,N-否)")
  @TableField("IS_MAIN")
  private Enable isMain;

  @ApiModelProperty(value = "是否激活(Y-是,N-否)")
  @TableField("IS_ACTIVE")
  private Enable isActive;

}