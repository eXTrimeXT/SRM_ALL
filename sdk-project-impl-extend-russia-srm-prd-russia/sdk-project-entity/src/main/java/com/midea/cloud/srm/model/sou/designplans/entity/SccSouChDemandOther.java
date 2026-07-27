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
@ApiModel(description = "集采台账-需求信息-策划方案-其他")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_other")
public class SccSouChDemandOther extends BaseEntity<SccSouChDemandOther> {

    @ApiModelProperty("其他id")
    @TableId("OTHER_ID")
    private Long otherId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("序号")
    @TableField("OTHER_NUM")
    private String otherNum;

    @ApiModelProperty("招标区域")
    @TableField("BID_AREA")
    private String bidArea;

    @ApiModelProperty("供应商")
    @TableField("SUP")
    private String sup;

    @ApiModelProperty("年采购额")
    @TableField("PRO_MON")
    private String proMon;

    @ApiModelProperty("金额占比")
    @TableField("AMO_PRO")
    private String amoPro;

    @ApiModelProperty("现行政策")
    @TableField("CUR_POL")
    private String curPol;

    @ApiModelProperty("拟询价供方")
    @TableField("SEL_SUP")
    private String selSup;

    @ApiModelProperty("招标策略")
    @TableField("BID_STR")
    private String bidStr;

    @ApiModelProperty("目标设定")
    @TableField("TAR_SET")
    private String tarSet;
}
