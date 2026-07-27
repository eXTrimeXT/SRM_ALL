package com.midea.cloud.srm.model.pj.sou.qa.dto;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class QuestionApiSouOrderQueryDTO extends ApiSouOrderQueryDTO {

    @ApiModelProperty("寻源类型")
    private String souType;
}
