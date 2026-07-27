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
@TableName("scc_base_org_collect_info")
@ApiModel(description = "组织收票信息")
@QlMatchType("OrgCollectInfo")
public class OrgCollectInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("COLLECT_ID")
    private Long collectId;

    @ApiModelProperty(value = "组织ID")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;

    @ApiModelProperty(value = "收票人")
    @TableField("COLLECT_PERSON")
    private String collectPerson;

    @ApiModelProperty(value = "收票人电话")
    @TableField("TEL")
    private String tel;

    @ApiModelProperty(value = "收票地址")
    @TableField("COLLECT_ADDRESS")
    private String collectAddress;

    @ApiModelProperty(value = "是否默认,只能有一个默认(Y-是,N-否)")
    @TableField("IS_DEFAULT")
    private String isDefault;

    @ApiModelProperty(value="收票人邮箱")
    @TableField("COLLECT_MAIL")
    private String collectMail;

}
