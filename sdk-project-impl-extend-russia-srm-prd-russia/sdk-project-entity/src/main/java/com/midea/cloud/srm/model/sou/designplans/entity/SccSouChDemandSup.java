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
 * @author ex_liuxy46
 */
@ApiModel(description = "集采台账-需求信息-需求-供应商信息")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_sup")
public class SccSouChDemandSup extends BaseEntity<SccSouChDemandSup> {

    @ApiModelProperty("需求供应商信息id")
    @TableId("DEMAND_SUP_ID")
    private Long demandSupId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("供应商id")
    @TableField("SUP_ID")
    private String supId;
    @ApiModelProperty("供应商编码")
    @TableField("SUP_CODE")
    private String supCode;
    @ApiModelProperty("供应商名称")
    @TableField("SUP_NAME")
    private String supName;

    @ApiModelProperty("性质")
    @TableField("NATURE")
    private String nature;

    @ApiModelProperty("到货及时率")
    @TableField("ARRIVAL_RATE")
    private String arrivalRate;

    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;
}
