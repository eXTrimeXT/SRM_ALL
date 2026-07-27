package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.analysis;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-23
 */
@Data
@ApiModel("评分分析-供应商信息实体类")
public class ScoreAnalysisVendorDto extends BaseObjectX {

    @ApiModelProperty("供应商名字")
    private String vendorName;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("技术标总得分")
    private BigDecimal totalScore;

    @ApiModelProperty("技术标平均得分")
    private BigDecimal averageScore;

    @ApiModelProperty("差异率：（技术标总得分 - 技术标平均得分）/技术标平均得分")
    private BigDecimal differenceRatio;
}
