package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderItem;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("报价信息查询")
@Data
public class ApiExtSouOrderItemQueryDto extends BaseObjectX {

    @ApiModelProperty("报价单头表ID")
    private Long orderId;

    /**
     * 招标单ID
     */
    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("供应商ID(冗余字段)")
    private Long vendorId;

    @ApiModelProperty("报价轮次")
    private Integer round;
}
