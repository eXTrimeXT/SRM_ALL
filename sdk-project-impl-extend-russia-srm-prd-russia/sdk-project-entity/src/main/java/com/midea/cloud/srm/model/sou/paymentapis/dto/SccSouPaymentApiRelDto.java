package com.midea.cloud.srm.model.sou.paymentapis.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/04/16/ $
 * @Description: 批量付款及自动提交审批业务关联表 实体类
 */
@Data
@ApiModel("批量付款及自动提交审批业务关联表 实体类")
public class SccSouPaymentApiRelDto extends BaseDTO {

    @ApiModelProperty("主键")
    private Long paymentApiId;
    @ApiModelProperty("业务主键")
    private Long businessId;
    @ApiModelProperty("业务代码")
    private String businessCode;
    @ApiModelProperty("关联代码")
    private String requestItemId;
    @ApiModelProperty("类型代码：BID-保证金，REQ-意向金")
    private String businessType;

}
