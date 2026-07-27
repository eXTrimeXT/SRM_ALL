package com.midea.cloud.srm.model.supcooperate.ext.order;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 100014336 ganyh19
 */
@Data
public class OrderReceivePerEmp {


    private Long orderId;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("供应商代码")
    private String vendorCode;

    @ApiModelProperty("业务实体ID(longi)")
    private Long ceeaOrgId;

    @ApiModelProperty("业务实体编码(longi)")
    private String ceeaOrgCode;

    @ApiModelProperty("业务实体名称(longi)")
    private String ceeaOrgName;

    @ApiModelProperty("账户工号(采购员员工工号)")
    private String ceeaEmpNo;

    @ApiModelProperty("采购员id")
    private Long ceeaEmpUseId;

    @ApiModelProperty("采购员名称")
    private String ceeaEmpUsername;

    @ApiModelProperty("接收数量")
    private BigDecimal receiveSum;

    @ApiModelProperty("订单数量")
    private BigDecimal orderNum;

    @ApiModelProperty("到货及时率")
    private BigDecimal receiveOnTime;



}
