package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("招标评分明细")
public class ApiExtSouTechScoreLineDto extends ExtSouTechScoreLine {

    @ApiModelProperty("供应商名字")
    private String vendorName;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("技术标总得分")
    private BigDecimal totalScore;

    @ApiModelProperty("技术标平均得分")
    private BigDecimal averageScore;
}
