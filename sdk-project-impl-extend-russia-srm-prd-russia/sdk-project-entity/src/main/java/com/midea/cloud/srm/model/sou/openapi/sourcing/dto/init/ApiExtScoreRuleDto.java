package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtScoreRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 备注
 *
 * @author huangbf3
 */
@Data
@ApiModel("招标评分项")
public class ApiExtScoreRuleDto extends ExtScoreRule {


    @ApiModelProperty("供应商评分列表")
    private List<ApiExtSouTechScoreLineDto> vendorScoreList;

    @ApiModelProperty("技术标总得分")
    private List<BigDecimal> totalScoreList;

    @ApiModelProperty("技术标平均得分")
    private List<BigDecimal> averageScoreList;

    @ApiModelProperty("评委ID")
    private Long groupId;

    @ApiModelProperty("评委成员账号")
    private String userName;

    @ApiModelProperty("评委成员姓名")
    private String fullName;

    @ApiModelProperty("招标编号")
    private String extProjectNo;

    @ApiModelProperty("招标名称")
    private String souName;

    public List<BigDecimal> getAverageScoreList() {
        if (Objects.isNull(averageScoreList)) {
            averageScoreList = new ArrayList<>();
        }
        return averageScoreList;
    }

    public List<BigDecimal> getTotalScoreList() {
        if (Objects.isNull(totalScoreList)) {
            totalScoreList = new ArrayList<>();
        }
        return totalScoreList;
    }

    public List<ApiExtSouTechScoreLineDto> getVendorScoreList() {
        if (Objects.isNull(vendorScoreList)) {
            vendorScoreList = new ArrayList<>();
        }
        return vendorScoreList;
    }
}
