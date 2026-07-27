package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * MQL - 技术标评标进度详情
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouTechProgressDetailQueryVO extends BaseObjectX {

    /** @see SouTechScoreHead#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;
    @ApiModelProperty("供应商名称")
    private String vendorName;
    /** @see SouOrder#getOrderId */
    @ApiModelProperty("报价单ID")
    private Long orderId;
    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("报价单号")
    private String orderNo;
    /** @see SouTechScoreHead#getScoreStatus */
    @ApiModelProperty("技术评标进度")
    private SouTechScoreStatusEnum scoreStatus;
    /** @see SouTechScoreHead#getTotalScore */
    @ApiModelProperty("技术得分(平均分)")
    private BigDecimal techScore;

}
