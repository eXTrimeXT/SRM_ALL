package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreDimension;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreDimensionDetails;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("评分维度")
public class SouScoreDimensionDTO extends SouScoreDimension {

    @ApiModelProperty("评分维度明细")
    private List<SouScoreDimensionDetails> souScoreDimensionDetailsList;

    public static SouScoreDimensionDTO init(SouScoreDimension souScoreDimension, List<SouScoreDimensionDetails> souScoreDimensionDetailsList) {
        SouScoreDimensionDTO rsp = new SouScoreDimensionDTO();
        BeanUtils.copyProperties(souScoreDimension,rsp);
        rsp.setSouScoreDimensionDetailsList(souScoreDimensionDetailsList);
        return rsp;
    }
}
