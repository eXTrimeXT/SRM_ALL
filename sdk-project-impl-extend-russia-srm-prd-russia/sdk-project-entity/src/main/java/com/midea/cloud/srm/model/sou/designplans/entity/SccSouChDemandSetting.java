package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "集采台账-需求信息-项目策划方案")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_setting")
public class SccSouChDemandSetting extends BaseEntity<SccSouChDemandSetting> {

    @ApiModelProperty("目标设定id")
    @TableId("SETTING_ID")
    private Long settingId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("供方id")
    @TableField("SUP_ID")
    private Long supId;
    @ApiModelProperty("供方编码")
    @TableField("SUP_CODE")
    private String supCode;
    @ApiModelProperty("供方编码")
    @TableField("SUP_NAME")
    private String supName;

    @ApiModelProperty("经验性质")
    @TableField("NATURE")
    private String nature;

    @ApiModelProperty("品类Id")
    @TableField("CATEGORY_ID")
    private Long categoryId;
    @ApiModelProperty("品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;
    @ApiModelProperty("品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

    @ApiModelProperty("行业客户")
    @TableField("CUSTOMERS")
    private String customers;

    @ApiModelProperty("是否新引进Y/N")
    @TableField("IS_NEW")
    private String isNew;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;
}
