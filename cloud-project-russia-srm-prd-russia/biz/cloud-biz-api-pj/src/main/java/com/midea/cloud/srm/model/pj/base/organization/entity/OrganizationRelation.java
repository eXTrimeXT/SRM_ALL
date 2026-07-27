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
 *  组织关系 模型
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_base_organization_rel")
@ApiModel(description = "组织关系")
@QlMatchType("OrganizationRelation")
public class OrganizationRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键关系ID")
    @TableId(value = "REL_ID")
    private Long relId;

    @ApiModelProperty(value = "组织ID")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;

    @ApiModelProperty(value = "父组织ID")
    @TableField("PARENT_ORGANIZATION_ID")
    private Long parentOrganizationId;

    @ApiModelProperty(value = "全路径唯一ID(MD加密)")
    @TableField(exist = false)
    private String fullPathId;
}
