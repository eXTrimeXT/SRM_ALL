package com.midea.cloud.srm.model.sou.answer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/17 15:33:02
 *  修改内容:
 * </pre>
 */


@Data
@ApiModel(description = "供应商-澄清供应商列表DTO")
public class AnswerVendorDTO extends BaseDTO {

    @ApiModelProperty(value = "主键ID", example = "1")
    private Long answerVendorId;

    @ApiModelProperty(value = "澄清ID", example = "1")
    private Long answerId;

    @ApiModelProperty(value = "序号", example = "1")
    private Integer lineNum;

    @ApiModelProperty(value = "供应商ID", example = "1")
    private Long vendorId;

    @ApiModelProperty(value = "供应商编码", example = "001")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称", example = "供应商A")
    private String vendorName;

    @ApiModelProperty(value = "是否已查阅", example = "是")
    private String ifRead;

    @ApiModelProperty(value = "查询时间", example = "2021-01-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    @ApiModelProperty(value = "是否已回复", example = "否")
    private String ifReplay;

    @ApiModelProperty(value = "最新回复时间", example = "2021-01-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastReplayTime;

    @ApiModelProperty(value = "确认状态", example = "已确认")
    private String confirmStatus;

    @ApiModelProperty(value = "最新回复id", example = "1")
    private Long replayId;

}

