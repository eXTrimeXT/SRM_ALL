package com.midea.cloud.srm.model.pj.sou.inq.dto.webapi.select;

import com.midea.cloud.srm.model.inq.inquiry.entity.InqItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

/**
 * 简易询价 - 目标价信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2021/10/28
 */
@Data
@ApiModel("用于设定目标价")
public class InqSelectTargetPriceWebDTO {

    /** @see SouItem#getSouItemId */
    @ApiModelProperty("寻源核心-物料需求行ID")
    private Long souItemId;

    /** @see InqItem#getNotaxTargetPrice */
    @ApiModelProperty("未税目标价")
    private BigDecimal notaxTargetPrice;

    /**
     * 入参格式化
     */
    public static void formatParams(List<InqSelectTargetPriceWebDTO> targetPriceList) {
        if (targetPriceList == null || targetPriceList.isEmpty()) {
            throw new IllegalArgumentException("请输入目标价信息");
        }

        int index = 0;
        for (InqSelectTargetPriceWebDTO targetPrice : targetPriceList) {
            index++;
            if (targetPrice.notaxTargetPrice == null) {
                throw new IllegalArgumentException(MessageFormat.format("第{0}行请输入目标价", index));
            } else {
                if (targetPrice.notaxTargetPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException(MessageFormat.format("第{0}行请目标价必须大于0", index));
                }
            }
        }
    }

}
