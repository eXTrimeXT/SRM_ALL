package com.midea.cloud.srm.model.sou.ca.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "供应商投标DTO")
public class CaOrderDTO extends BaseDTO {
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long caOrderId;

    @ApiModelProperty(value = "序号", example = "1")
    private Integer lineNum;

    @ApiModelProperty(value = "*定标申请ID", example = "1")
    private Long caId;

    @ApiModelProperty(value = "*报价行ID", example = "1")
    private Long orderItemId;

    @ApiModelProperty(value = "*报价单ID", example = "1")
    private Long orderId;

    @ApiModelProperty(value = "供应商ID", example = "1")
    private Long vendorId;

    @ApiModelProperty(value = "供应商编码", example = "V001")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称", example = "供应商1")
    private String vendorName;
    @ApiModelProperty(value = "供应商属性", example = "供应商属性")
    private String extVendorAttr;

    @ApiModelProperty(value = "投标状态", example = "投标状态")
    private String orderStatus;

    @ApiModelProperty(value = "投标包名", example = "投标包名")
    private String tenderPackageName;

    @ApiModelProperty(value = "撤回原因", example = "不参与原因")
    private String withdrawReason;

    @ApiModelProperty("不参与原因")
    private String extNotjoinReason;

    @ApiModelProperty(value = "废标原因", example = "废标原因")
    private String rejectReason;
}
