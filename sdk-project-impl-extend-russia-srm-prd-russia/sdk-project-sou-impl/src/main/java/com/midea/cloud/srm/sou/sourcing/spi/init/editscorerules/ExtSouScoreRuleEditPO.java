
package com.midea.cloud.srm.sou.sourcing.spi.init.editscorerules;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtScoreRule;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("评分规则")
public class ExtSouScoreRuleEditPO extends BaseObjectX {

    @ApiModelProperty("评分规则项")
    private List<ExtScoreRule> scoreRuleList;

}
