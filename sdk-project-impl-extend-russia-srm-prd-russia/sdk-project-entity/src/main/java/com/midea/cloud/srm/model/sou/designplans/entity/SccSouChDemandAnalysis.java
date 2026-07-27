package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 备注
 *
 * @author huangbf3
 */
@ApiModel(description = "集采台账-需求信息-策划方案-使用单位金额分析、按物流品类分析")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_analysis")
public class SccSouChDemandAnalysis extends BaseEntity<SccSouChDemandAnalysis> {

    @ApiModelProperty("分析id")
    @TableId("ANALYSIS_ID")
    private Long analysisId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("类型,1.使用单位金额分析、2.按物资金额分析,3.按供应商金额分析")
    @TableField("TYPE")
    private Integer type;

    @ApiModelProperty("组织id")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;
    @ApiModelProperty("组织编码")
    @TableField("ORGANIZATION_CODE")
    private String organizationCode;
    @ApiModelProperty("组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;

    @ApiModelProperty("金额")
    @TableField("MAT_MONEY")
    private BigDecimal matMoney;

    @ApiModelProperty("占比")
    @TableField("MAT_RATE")
    private BigDecimal matRate;

    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;

    @ApiModelProperty("物资id")
    @TableField("MATERIAL_ID")
    private Long materialId;
    @ApiModelProperty("物资编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;
    @ApiModelProperty("物资名称")
    @TableField("MATERIAL_NAME")
    private String materialName;
}
