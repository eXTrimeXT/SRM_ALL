package com.midea.cloud.srm.model.pj.sou.bid.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.bid.entity.ExtBidSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 招投标.流程配置表
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_bid_process_config")
@ApiModel("招投标.流程配置表")
public class BidSouProcessConfig extends ExtBidSouProcessConfig {

    /** @see SouProcessConfig#getProcessConfigId */
    @TableId("PROCESS_CONFIG_ID")
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    @TableField("BARGAIN_TYPE")
    @ApiModelProperty("询价类型")
    private BidSouTypeEnum bargainType;

    @TableField("BOND_MANAGEMENT")
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;

}
