package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouItemDTO extends SouItem {

    @ApiModelProperty("阶梯信息")
    private List<SouItemLadder> ladderList;

}
