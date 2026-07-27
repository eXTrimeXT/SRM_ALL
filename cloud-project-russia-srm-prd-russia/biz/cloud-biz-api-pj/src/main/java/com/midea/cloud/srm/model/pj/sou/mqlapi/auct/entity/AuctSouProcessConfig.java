package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.enums.AuctSouProcessNodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价MQL - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_auct_process_config")
@ApiModel("竞价.流程配置表")
public class AuctSouProcessConfig extends ExtAuctSouProcessConfig {

    /** @see SouProcessConfig#getProcessConfigId */
    @TableId("PROCESS_CONFIG_ID")
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    /** @see AuctSouProcessNodeEnum#bondManagement */
    @TableField("BOND_MANAGEMENT")
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;

    /** @see AuctSouProcessNodeEnum#auctHall */
    @TableField("AUCT_HALL")
    @ApiModelProperty("竞价大厅")
    private Enable auctHall;

}
