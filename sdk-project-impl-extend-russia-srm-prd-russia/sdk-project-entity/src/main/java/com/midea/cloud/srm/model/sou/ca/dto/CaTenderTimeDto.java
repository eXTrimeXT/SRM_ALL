package com.midea.cloud.srm.model.sou.ca.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import com.midea.cloud.srm.model.sou.enums.CaTenderTimeTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@ApiModel(description = "定标申请投标时间DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CaTenderTimeDto extends BaseDTO {

    @ApiModelProperty(value = "定/废标申请单ID", example = "定/废标申请单ID", required = false)
    private Long caId;
    /**
     * @see CaTenderTimeTypeEnum
     */
    @ApiModelProperty(value = "工作内容，字典：CA_TENDER_TIME_TYPE", example = "工作内容，字典：CA_TENDER_TIME_TYPE", required = false)
    private String type;
    @ApiModelProperty(value = "计划完成时间", example = "计划完成时间", required = false)
    private Date planTime;
    @ApiModelProperty(value = "实际时间", example = "实际时间", required = false)
    private Date actualTime;
    @ApiModelProperty(value = "责任部门", example = "责任部门", required = false)
    private String department;
    @ApiModelProperty(value = "责任人", example = "责任人", required = false)
    private String dutyOfficer;
    @ApiModelProperty(value = "备注", example = "备注", required = false)
    private String remark;
    @ApiModelProperty(value = "排序", example = "排序", required = false)
    private Integer sortIndex;

}
