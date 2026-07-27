package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "寻源-简易询价物料需求行")
public class ExtInqSouItem extends BaseEntity<ExtInqSouItem> {

    @TableField("EXT_MATERIAL_MODEL")
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    @TableField("EXT_BRAND")
    @ApiModelProperty("品牌")
    private String extBrand;

    @TableField("EXT_AREA_ID")
    @ApiModelProperty("区域ID")
    private String extAreaId;

    @TableField("EXT_AREA_CODE")
    @ApiModelProperty("区域编码")
    private String extAreaCode;

    @TableField("EXT_AREA_NAME")
    @ApiModelProperty("区域名称")
    private String extAreaName;

    @TableField("EXT_SOURCE_FROM_LINE_IDS")
    @ApiModelProperty("来源单据明细ID集合(因为原表用Long类型，无法支持)")
    private String extSourceFromLineIds;

    @TableField("HAS_CLOSE")
    @ApiModelProperty("是否关闭")
    private Enable hasClose;

}