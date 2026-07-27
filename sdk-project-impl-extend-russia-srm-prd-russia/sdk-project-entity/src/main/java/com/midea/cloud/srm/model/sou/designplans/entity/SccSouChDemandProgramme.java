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
@TableName("scc_sou_ch_demand_programme")
public class SccSouChDemandProgramme extends BaseEntity<SccSouChDemandProgramme> {

    @ApiModelProperty("策划方案id")
    @TableId("PROGRAMME_ID")
    private Long programmeId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("使用单位金额分析Y/N")
    @TableField("UNIT_FLAG")
    private String unitFlag;

    @ApiModelProperty("供方采购金额分析Y/N")
    @TableField("BUY_FLAG")
    private String buyFlag;

    @ApiModelProperty("按物资品类分析Y/N")
    @TableField("CATEGORY_FLAG")
    private String categoryFlag;
}
