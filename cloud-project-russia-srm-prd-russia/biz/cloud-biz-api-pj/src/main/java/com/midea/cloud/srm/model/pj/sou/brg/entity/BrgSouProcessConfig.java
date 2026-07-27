package com.midea.cloud.srm.model.pj.sou.brg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.brg.entity.ExtBrgSouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.brg.enums.BrgSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 项目式询价.流程配置表
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_brg_process_config")
@ApiModel("项目式询价.流程配置表")
public class BrgSouProcessConfig extends ExtBrgSouProcessConfig {

    /** @see SouProcessConfig#getProcessConfigId */
    @TableId("PROCESS_CONFIG_ID")
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    @TableField("BARGAIN_TYPE")
    @ApiModelProperty("询价类型")
    private BrgSouTypeEnum bargainType;

    @TableField("BOND_MANAGEMENT")
    @ApiModelProperty("保证金管理")
    protected Enable bondManagement;

}
