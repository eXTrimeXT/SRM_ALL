package com.midea.cloud.srm.model.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/12 10:43
 *  修改内容:
 * </pre>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_base_material_item")
@ApiModel(description = "物料维护")
public class ExtMaterialItem extends MaterialItem {

    @ApiModelProperty(value = "物料规格型号")
    @TableField("EXT_MATERIAL_MODEL")
    private String extMaterialModel;

    @ApiModelProperty(value = "物料描述")
    @TableField("EXT_MATERIAL_DESCRIPTION")
    private String extMaterialDescription;
}
