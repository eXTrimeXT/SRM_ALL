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
@ApiModel(description = "集采台账-需求信息-策划方案-招标策略及目标设定")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_strategy")
public class SccSouChDemandStrategy extends BaseEntity<SccSouChDemandStrategy> {

    @ApiModelProperty("策略id")
    @TableId("STRATEGY_ID")
    private Long strategyId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("招标区域")
    @TableField("BID_AREA")
    private String bidArea;

    @ApiModelProperty("油品型号")
    @TableField("YP_TYPE")
    private String ypType;

    @ApiModelProperty("采购量")
    @TableField("BUY_NUM")
    private String buyNum;

    @ApiModelProperty("采购额")
    @TableField("BUY_MONEY")
    private String buyMoney;

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
