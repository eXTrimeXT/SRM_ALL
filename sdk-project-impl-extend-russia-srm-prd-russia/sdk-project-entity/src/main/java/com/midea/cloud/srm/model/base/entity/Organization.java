package com.midea.cloud.srm.model.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 *  组织设置 模型
 * </pre>
 *
 * @author huanglj50@meicloud.com
 * @version 1.00.00
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_base_organization")
@ApiModel(description = "组织")
public class Organization extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("ORGANIZATION_ID")
    private Long organizationId;

    /**
     * organizationIdList
     */
    @TableField(exist = false)
    private List<Long> organizationIdList;

    @ApiModelProperty(value = "组织全路径虚拟ID")
    @TableField(exist = false)
    private String fullPathId;

    @ApiModelProperty(value = "组织类型ID")
    @TableField("ORGANIZATION_TYPE_ID")
    private Long organizationTypeId;

    @ApiModelProperty(value = "组织类型编码")
    @TableField("ORGANIZATION_TYPE_CODE")
    private String organizationTypeCode;

    @ApiModelProperty(value = "组织类型名称")
    @TableField("ORGANIZATION_TYPE_NAME")
    private String organizationTypeName;

    @ApiModelProperty(value = "组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;

    @ApiModelProperty(value = "组织编码")
    @TableField("ORGANIZATION_CODE")
    private String organizationCode;

    @ApiModelProperty(value = "父组织ID集")
    @TableField("PARENT_ORGANIZATION_IDS")
    private String parentOrganizationIds;

    @ApiModelProperty(value = "组织区域")
    @TableField("ORGANIZATION_REGION")
    private String organizationRegion;

    @ApiModelProperty(value = "是否默认库存组织")
    @TableField("DEFAULT_INV")
    private String defaultInv;

    /**
     * 生效日期
     */
    @TableField("START_DATE")
    private LocalDate startDate;

    @ApiModelProperty(value = "失效日期")
    @TableField("END_DATE")
    private LocalDate endDate;

    @ApiModelProperty(value = "是否有效（）")
    @TableField("ENABLED")
    private String enabled;

    @ApiModelProperty(value = "ERP组织ID")
    @TableField("ERP_ORG_ID")
    private String erpOrgId;
    @ApiModelProperty(value = "erp业务实体code")
    @TableField("CEEA_ERP_UNIT_CODE")
    private String ceeaErpUnitCode;
    @ApiModelProperty(value = "数据来源")
    @TableField("DATA_RESOURCE")
    private String dataResource;

    @ApiModelProperty(value = "父组织名称集(以,为分隔符拼接)")
    @TableField(exist = false)
    private String parentOrganizationNames;

    @ApiModelProperty(value = "ouId列")
    @TableField(exist = false)
    private List<Long> ouIds;

    @ApiModelProperty(value = "父Id")
    @TableField(exist = false)
    private Long fatherId;

    @ApiModelProperty(value = "ou编码(用于验收单推费控)")
    @TableField("CEEA_BUSINESS_CODE")
    private String ceeaBusinessCode;

    @ApiModelProperty(value = "是否项目公司")
    @TableField("IF_PROJECT_COMPANY")
    private String ifProjectCompany;


    @ApiModelProperty(value = "父组织ID集")
    @TableField(exist = false)
    private String parentOrganizationCodes;

    @ApiModelProperty(value = "父组织ID集")
    @TableField(exist = false)
    private List<Long> parentIdList;


    @ApiModelProperty(value = "是否全部子组织Y/N")
    @TableField(exist = false)
    private String childAll;
}