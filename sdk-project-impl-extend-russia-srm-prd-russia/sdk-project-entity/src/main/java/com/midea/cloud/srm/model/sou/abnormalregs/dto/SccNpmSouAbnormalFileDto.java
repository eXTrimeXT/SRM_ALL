package com.midea.cloud.srm.model.sou.abnormalregs.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "AnswerDTO", description = "异常登记")
public class SccNpmSouAbnormalFileDto extends BaseDTO {
    @ApiModelProperty("异常登记附件表主键ID")
    private Long abnormalFileId;
    @ApiModelProperty("异常登记表主键ID")
    private Long regId;
    @ApiModelProperty("附件ID")
    private Long fileId;
    @ApiModelProperty("附件名称")
    private String fileName;

}
