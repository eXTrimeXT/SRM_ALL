package com.midea.cloud.srm.model.base.notice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class PjNoticeRequestDTO extends NoticeRequestDTO{

    @ApiModelProperty("有效期")
    private Date extValidityDate;

    @ApiModelProperty("是否有效")
    private String isValidity;
}
