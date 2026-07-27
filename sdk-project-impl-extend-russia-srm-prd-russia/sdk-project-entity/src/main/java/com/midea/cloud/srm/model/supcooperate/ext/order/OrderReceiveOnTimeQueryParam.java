package com.midea.cloud.srm.model.supcooperate.ext.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.midea.cloud.srm.model.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 100014336 ganyh19
 */
@Data
public class OrderReceiveOnTimeQueryParam extends BasePage {

    @ApiModelProperty("订单日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:dd")
    private List<Date> ceeaPurchaseOrderDate;

    @ApiModelProperty("采购员id")
    private Long ceeaEmpUserId;

    @ApiModelProperty("采购员账号")
    private String ceeaEmpNo;

    /**
     * @see ExtOrder#getCeeaEmpUserName
     */
    @ApiModelProperty("采购员名称")
    private String ceeaEmpUsername;

    @ApiModelProperty("申请单位编码")
    private String ceeaOrgCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("申请单位Id")
    private String ceeaOrgId;
}
