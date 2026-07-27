package com.midea.cloud.srm.model.sou.ca.dto;


import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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
 *  修改日期: 2023/10/5 10:57:34
 *  修改内容:
 * </pre>
 */
@Data
@ApiModel(description = "供应商选定结果DTO")
public class CaSelectionResultDTO extends BaseDTO {

    @ApiModelProperty(value = "供应商选定结果ID", example = "1")
    private Long selectionResultId;

    @ApiModelProperty(value = "序号", example = "1")
    private Integer lineNum;

    @ApiModelProperty(value = "*定标申请ID", example = "1")
    private Long caId;

    @ApiModelProperty(value = "*", example = "1")
    private String orderItemId;

    @ApiModelProperty(value = "*报价单ID", example = "1")
    private Long orderId;

    @ApiModelProperty(value = "供应商ID", example = "1")
    private Long vendorId;

    @ApiModelProperty(value = "供应商编码", example = "V001")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称", example = "供应商1")
    private String vendorName;

    @ApiModelProperty(value = "是否中标(枚举值：YesOrNo)", example = "Y")
    private String isWin;

    @ApiModelProperty(value = "中标范围", example = "A区")
    private String winRange;

    @ApiModelProperty(value = "中/落标原因", example = "价格过高")
    private String winReason;

    @ApiModelProperty(value = "中/落标原因", example = "价格过高")
    private String applicantNo;
}

