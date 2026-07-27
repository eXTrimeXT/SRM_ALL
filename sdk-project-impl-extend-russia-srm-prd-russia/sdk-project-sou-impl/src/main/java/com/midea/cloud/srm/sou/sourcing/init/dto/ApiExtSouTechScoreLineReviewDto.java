package com.midea.cloud.srm.sou.sourcing.init.dto;

import com.midea.cloud.srm.model.pj.aihelper.BidReviewResDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouTechScoreLineDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("招标评分明细")
public class ApiExtSouTechScoreLineReviewDto extends ApiExtSouTechScoreLineDto {

    @ApiModelProperty("供应商名字")
    private String vendorName;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("技术标总得分")
    private BigDecimal totalScore;

    @ApiModelProperty("技术标平均得分")
    private BigDecimal averageScore;

    @ApiModelProperty("智能评标结果")
    private List<BidReviewResDto.AnswerAndQuotation> answerAndQuotationList;
}
