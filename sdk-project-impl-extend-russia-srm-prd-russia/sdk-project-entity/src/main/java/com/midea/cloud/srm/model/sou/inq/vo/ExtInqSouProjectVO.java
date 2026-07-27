package com.midea.cloud.srm.model.sou.inq.vo;

import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouProjectVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ExtInqSouProjectVO extends ApiInqSouProjectVO {

    @ApiModelProperty("单据状态")
    private String extProjectStatus;
}
