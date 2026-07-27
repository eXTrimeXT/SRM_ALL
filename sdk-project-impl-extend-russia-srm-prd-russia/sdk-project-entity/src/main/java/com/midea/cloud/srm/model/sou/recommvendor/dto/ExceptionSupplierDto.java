package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@ApiModel("异常供应商单名录")
public class ExceptionSupplierDto extends BaseObjectX {

    @ApiModelProperty("供应商ID")
    private Long companyId;

    @ApiModelProperty("供应商编码")
    private String companyCode;

    @ApiModelProperty("供应商名称")
    private String companyName;

    @ApiModelProperty("供应商法人")
    private String legalPerson;

    @ApiModelProperty("供应商联系人")
    private String contactName;

    @ApiModelProperty("供应商联系方式")
    private String ceeaContactMethod;

    @ApiModelProperty("供应商邮箱")
    private String email;
}
