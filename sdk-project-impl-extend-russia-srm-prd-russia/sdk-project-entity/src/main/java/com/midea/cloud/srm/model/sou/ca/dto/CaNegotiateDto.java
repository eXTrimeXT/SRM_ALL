package com.midea.cloud.srm.model.sou.ca.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
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
public class CaNegotiateDto extends BaseDTO {

    @ApiModelProperty(value = "主键ID", example = "主键ID", required = false)
    private Long negotiateId;
    @ApiModelProperty(value = "定/废标申请单ID", example = "定/废标申请单ID", required = false)
    private Long caId;
    @ApiModelProperty(value = "供应商ID", example = "供应商ID", required = false)
    private Long vendorId;
    @ApiModelProperty(value = "供应商编码", example = "供应商编码", required = false)
    private String vendorCode;
    @ApiModelProperty(value = "供应商名称", example = "供应商名称", required = false)
    private String vendorName;
    @ApiModelProperty(value = "谈判", example = "谈判", required = false)
    private String negotiate;
    @ApiModelProperty(value = "排序", example = "排序", required = false)
    private Integer sortIndex;


}
