package com.midea.cloud.srm.sou.sourcing.spi.init.editendtimes;


import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouRound;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * 备注
 * @author huangbf3
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtSouEndTimePo extends BaseObjectX {

    @ApiModelProperty("轮次表")
    private SouRound souRound;

    @ApiModelProperty("招标计划表")
    private ExtSouPlan souPlan;
}
