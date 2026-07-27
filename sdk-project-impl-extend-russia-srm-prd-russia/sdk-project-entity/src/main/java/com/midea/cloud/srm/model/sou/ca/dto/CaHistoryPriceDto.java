
package com.midea.cloud.srm.model.sou.ca.dto;

/**
 * <pre>
 *
 * </pre>
 *
 * @author fu
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/08/52 10:49:18
 *  修改内容:
 * </pre>
 */

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "定标历史价格库DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CaHistoryPriceDto extends BaseDTO {

    @ApiModelProperty("主键ID")
    private Long caPriceId;

    @ApiModelProperty("定标申请单ID")
    private Long caId;

    @ApiModelProperty("招标价格库ID")
    private Long bidPriceId;

    @ApiModelProperty("项目名称")
    private String souName;

    @ApiModelProperty("项目编号")
    private String projectNo;

    @ApiModelProperty("名称")
    private String itemDesc;

    @ApiModelProperty("规格型号")
    private String specification;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("含税单价（万元）")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "固定含税单价（万元）")
    private BigDecimal fixedPriceTax;

    @ApiModelProperty(value = "区域")
    private String region;

    @ApiModelProperty(value = "招标负责人ID")
    private Long souPrincipalUserId;

    @ApiModelProperty(value = "招标负责人账号")
    private String souPrincipalUserName;

    @ApiModelProperty(value = "招标负责人")
    private String souPrincipal;

    @ApiModelProperty(value = "定标时间")
    private Date bidDate;

    public CaHistoryPriceDto(){
        this.bidPriceId=0L;
    }

}

