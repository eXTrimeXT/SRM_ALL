package com.midea.cloud.srm.model.sou.answer.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "供应商-质疑澄清回复表")
@Data
@EqualsAndHashCode(callSuper=false)
public class ReplayDTO extends BaseDTO {

    @ApiModelProperty(value = "回复id", example = "1")
    private Long replayId;


    @ApiModelProperty(value = "澄清ID", example = "1")
    private Long answerId;

    @ApiModelProperty(value = "澄清供应商ID", example = "1")
    private Long answerVendorId;

    @ApiModelProperty(value = "回复内容", example = "测试")
    private String replayContent;

    List<ReplayFileDTO> sceneFiles;
}
