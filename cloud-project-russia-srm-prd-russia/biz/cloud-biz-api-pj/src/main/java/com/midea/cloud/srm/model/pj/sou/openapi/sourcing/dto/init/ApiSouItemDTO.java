package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源openAPI - 物料需求
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouItemDTO extends ApiSouItemEditDTO {

    @ApiModelProperty("阶梯价模板信息")
    private List<SouItemLadder> ladderList;

}
