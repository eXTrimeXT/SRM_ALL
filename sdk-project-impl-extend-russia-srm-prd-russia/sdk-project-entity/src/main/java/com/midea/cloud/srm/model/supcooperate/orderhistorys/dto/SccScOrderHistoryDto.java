package com.midea.cloud.srm.model.supcooperate.orderhistorys.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/03/28/ $
 * @Description: 订单历史表DTO
 */
@ApiModel("订单历史表DTO")
@Data
public class SccScOrderHistoryDto extends BaseDTO {

    @ApiModelProperty("价格趋势id")
    private Long orderHistoryId;
    @ApiModelProperty("物料编码")
    private String materialCode;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("物料描述")
    private String materialDescribe;
    @ApiModelProperty("品牌")
    private String brand;
    @ApiModelProperty("区域")
    private String areaCode;
    @ApiModelProperty("业务实体编码")
    private String orgCode;
    @ApiModelProperty("业务实体名称")
    private String orgName;
    @ApiModelProperty("未税价格")
    private BigDecimal noTaxPrice;
    @ApiModelProperty("税率")
    private BigDecimal taxRate;
    @ApiModelProperty("到货周期")
    private Date leadTime;
    @ApiModelProperty("供应商编码")
    private String supCode;
    @ApiModelProperty("供应商名称")
    private String supName;
    @ApiModelProperty("供应商联系方式")
    private String supTel;
    @ApiModelProperty("订单日期")
    private Date orderDate;

}
