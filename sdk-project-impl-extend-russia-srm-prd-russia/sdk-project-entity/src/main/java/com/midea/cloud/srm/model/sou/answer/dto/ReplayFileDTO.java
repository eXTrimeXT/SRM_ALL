package com.midea.cloud.srm.model.sou.answer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel(description = "供应商-质疑澄清回复附件表")
@Data
@EqualsAndHashCode(callSuper=false)
public class ReplayFileDTO extends BaseDTO {

    @ApiModelProperty(value = "回复附件ID", example = "1", required = true)
    private Long replayFileId;

    @ApiModelProperty(value = "回复id", example = "1")
    private Long replayId;

    @ApiModelProperty(value = "回复时间", example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date replayTime;

    @ApiModelProperty(value = "备注", example = "这是一个备注")
    private String remark;

    @ApiModelProperty(value = "附件ID", example = "1")
    private Long fileId;

    @ApiModelProperty(value = "附件名称", example = "example.txt")
    private String fileName;

    @ApiModelProperty(value = "签署状态", example = "signed")
    private String signStatus;

    @ApiModelProperty(value = "签署时间", example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signTime;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "是否逻辑删除", example = "N")
    private String isDelete = "N";

}

