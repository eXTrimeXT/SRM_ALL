package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("组织报价")
@Data
public class ApiExtRoundDto extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("报价截止时间")
    private Date orderEndTime;

    @ApiModelProperty("组织报价原因")
    private String extOrderReason;

    @ApiModelProperty("选择供应商")
    private List<ExtSouVendor> vendorList;

    @ApiModelProperty("是否更新报价数量")
    private String extPriceFlag;

    /**
     * 报价信息
     */
    private List<ExtSouItem> itemList;
}
