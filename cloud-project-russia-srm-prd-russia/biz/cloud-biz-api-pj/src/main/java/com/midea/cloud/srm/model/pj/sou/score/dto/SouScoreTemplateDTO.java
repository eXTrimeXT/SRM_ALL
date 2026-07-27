package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.midea.cloud.srm.model.pj.sou.score.dto.SouScoreDimensionDTO;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreDimension;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreDimensionDetails;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("评分模板")
public class SouScoreTemplateDTO extends SouScoreTemplate {

    @ApiModelProperty("评分维度")
    private List<SouScoreDimensionDTO> souScoreDimensionDTOList;

    public static SouScoreTemplateDTO init(SouScoreTemplate scoreTemplate,
                                          List<SouScoreDimension> souScoreDimensionList,
                                          Map<Long, List<SouScoreDimensionDetails>> souScoreDimensionDetailsMap) {
        SouScoreTemplateDTO rsp = new SouScoreTemplateDTO();
        BeanUtils.copyProperties(scoreTemplate, rsp);
        if (CollectionUtils.isNotEmpty(souScoreDimensionList)){
            List<SouScoreDimensionDTO> collect = souScoreDimensionList.stream().map(s -> {
                return SouScoreDimensionDTO.init(s, souScoreDimensionDetailsMap.get(s.getScoreDimensionId()));
            }).collect(Collectors.toList());
            rsp.setSouScoreDimensionDTOList(collect);
        }
        return rsp;
    }
}
