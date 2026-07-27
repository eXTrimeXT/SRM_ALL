package com.midea.cloud.srm.sou.sourcing.spi.init.starttechbids;

import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("技术标报价")
public class ExtStartTechBidEditPO extends BaseObjectX {

    @ApiModelProperty("寻源项目")
    private ExtSouProject souProject;

    @ApiModelProperty("轮次表")
    private ExtSouRound round;

    @ApiModelProperty("报价单")
    private List<ExtSouOrder> souOrderList;

    @ApiModelProperty("报价单明细")
    private List<ExtSouOrderItem> souOrderItemList;

    @ApiModelProperty("技术标评分头表")
    private List<ExtSouTechScoreHead> techScoreHeadList;

    @ApiModelProperty("技术标评分行表")
    private List<ExtSouTechScoreLine> techScoreLineList;

    @ApiModelProperty("供应商保证金")
    private List<ExtSouMargin> souMarginList;

    @ApiModelProperty("供应商清单")
    private List<ExtSouVendor> vendorList;
}
