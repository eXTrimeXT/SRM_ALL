package com.midea.cloud.srm.model.sup.association.dto;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "关联供应商信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSupAssociationDTO extends BaseObjectX {


    @ApiModelProperty("ID")
    private Long associationId;

    @ApiModelProperty("A供应商ID")
    private Long vendorIdA;

    @ApiModelProperty("A供应商编码")
    private String vendorCodeA;

    @ApiModelProperty("A供应商名称")
    private String vendorNameA;


    @ApiModelProperty("B供应商ID")
    private Long vendorIdB;


    @ApiModelProperty("B供应商CODE")
    private String vendorCodeB;


    @ApiModelProperty("B供应商NAME")
    private String vendorNameB;


    @ApiModelProperty("类型")
    private String associationType;


    @ApiModelProperty("关联关系备注")
    private String associationRemark;

    @ApiModelProperty("供应商A唯一社会信用代码")
    private String socialCreditCodeA;
    
    @ApiModelProperty("供应商B唯一社会信用代码")
    private String socialCreditCodeB;
}
