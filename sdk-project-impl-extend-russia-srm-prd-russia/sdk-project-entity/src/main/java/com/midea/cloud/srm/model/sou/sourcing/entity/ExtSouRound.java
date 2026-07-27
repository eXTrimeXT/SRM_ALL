package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("寻源核心-轮次表")
@TableName("scc_sou_round")
public class ExtSouRound extends SouRound{

    @ApiModelProperty("组织报价原因")
    private String extOrderReason;

    @ApiModelProperty("是否更新报价数量")
    private String extPriceFlag;

    @ApiModelProperty("是否总价比价")
    private String extTotalCompare;
}
