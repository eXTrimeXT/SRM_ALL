package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞价 - 流程配置
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_comp_process_config")
@ApiModel("竞价.流程配置表")
public class CompSouProcessConfig extends ExtCompSouProcessConfig {

    /** @see SouProcessConfig#getProcessConfigId */
    @TableId("PROCESS_CONFIG_ID")
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    @TableField("BOND_MANAGEMENT")
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;

    @TableField("COMP_HALL")
    @ApiModelProperty("竞价大厅")
    private Enable compHall;

}
