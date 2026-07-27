package com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.supcooperate.meiql.deliverynote.dto.DeliveryNoteDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fubiao
 */
@Data
public class QueryDeliveryNote {
    @ApiModelProperty("送货单号")
    private String deliveryNumber;
    @ApiModelProperty("送货开始时间")
    private String beginDate;
    @ApiModelProperty("送货结束时间")
    private String endDate;
    @ApiModelProperty("状态")
    private String deliveryNoteStatus;
    @ApiModelProperty("业务实体名称")
    private String orgName;
    @ApiModelProperty("业务实体名称ID")
    private String orgId;
    @ApiModelProperty("采购订单编号")
    private String extPurchaserNo;
    @ApiModelProperty("供应商")
    private String vendorName;

}
