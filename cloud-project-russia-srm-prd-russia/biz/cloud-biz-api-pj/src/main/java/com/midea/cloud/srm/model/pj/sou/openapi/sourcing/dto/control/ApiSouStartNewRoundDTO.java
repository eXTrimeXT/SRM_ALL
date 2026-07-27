package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 寻源openAPI - 发起新一轮
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/02
 */
@Data
@ApiModel(description = "发起新一轮参数")
@EqualsAndHashCode(callSuper = true)
public class ApiSouStartNewRoundDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;

    /** @see SouRound#getOrderStartTime */
    @ApiModelProperty("新一轮报价开始时间")
    protected Date orderStartTime;

    /** @see SouRound#getOrderEndTime */
    @ApiModelProperty("新一轮报价截止时间")
    protected Date orderEndTime;

    @SuppressWarnings({"AlibabaPojoNoDefaultValue", "AlibabaPojoMustUsePrimitiveField"})
    @ApiModelProperty("是否立即开始")
    protected boolean startNow = true;

    /** @see SouRound#getEarliestBusinessOpenTime */
    @ApiModelProperty("最早开标时间")
    protected Date earliestBusinessOpenTime;

    @ApiModelProperty("新一轮中新邀请的供应商")
    protected List<ApiSouVendorDTO> newVendors;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (orderStartTime != null && orderStartTime.before(new Date())) {
            startNow = true;
            orderStartTime = new Date();
        }
        if (orderEndTime == null) {
            throw new IllegalArgumentException("请选择报价截止时间");
        } else {
            if (orderStartTime != null && !orderStartTime.before(orderEndTime)) {
                throw new IllegalArgumentException("报价开始时间必须早于报价截止时间");
            }
        }
        if (startNow) {
            orderStartTime = new Date();
        } else {
            if (orderStartTime == null) {
                throw new IllegalArgumentException("请选择报价开始时间");
            }
        }
        if (newVendors != null) {
            for (ApiSouVendorDTO vendor : newVendors) {
                if (vendor.getVendorId() == null) {
                    throw new IllegalArgumentException("缺少vendorId信息");
                }
                if (vendor.getAuthList() != null) {
                    for (ApiSouVendorAuthEditDTO auth : vendor.getAuthList()) {
                        if (auth.getSouItemId() == null) {
                            throw new IllegalArgumentException("缺少souItemId参数");
                        }
                    }
                }
            }
        }
    }

}
