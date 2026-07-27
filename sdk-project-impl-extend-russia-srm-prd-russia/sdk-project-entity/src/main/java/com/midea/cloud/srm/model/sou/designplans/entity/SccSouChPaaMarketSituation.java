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
@ApiModel(description = "集采台账-调价申请-市场行情")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_paa_market_situation")
public class SccSouChPaaMarketSituation extends BaseEntity<SccSouChPaaMarketSituation> {

    @ApiModelProperty("市场行情id")
    @TableId("MARKET_SITUATION_ID")
    private Long marketSituationId;

    @ApiModelProperty("调整id")
    @TableField("ADJUST_ID")
    private Long adjustId;

    @ApiModelProperty("调价申请单号")
    @TableField("ADJUST_CODE")
    private String adjustCode;

    @ApiModelProperty("调价介绍")
    @TableField("INTRODUCE")
    private String introduce;
}
