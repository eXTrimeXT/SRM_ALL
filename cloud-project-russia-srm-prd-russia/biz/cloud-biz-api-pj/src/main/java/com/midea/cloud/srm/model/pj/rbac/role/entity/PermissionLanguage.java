package com.midea.cloud.srm.model.pj.rbac.role.entity;

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
*  <pre>
 *  权限多语言名称 模型
 * </pre>
*
* @author huanghb14@meicloud.com
* @version 1.00.00
*
*  <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-03-10 17:30:00
 *  修改内容:
 * </pre>
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_rbac_permission_language")
@ApiModel(description = "权限多语言名称")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@QlMatchType("PermissionLanguage")
public class PermissionLanguage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "权限多语言ID")
    @TableId("PERMISSION_LANGUAGE_ID")
    private Long permissionLanguageId;

    @ApiModelProperty(value = "权限ID")
    @TableField("PERMISSION_ID")
    private Long permissionId;

    @ApiModelProperty(value = "语言标识：在字典表定义语言标识, 参考showdoc国际语言标识, GEN：通用")
    @TableField("LANGUAGE")
    private String language;

    @ApiModelProperty(value = "权限名称")
    @TableField("PERMISSION_NAME")
    private String permissionName;










































}
