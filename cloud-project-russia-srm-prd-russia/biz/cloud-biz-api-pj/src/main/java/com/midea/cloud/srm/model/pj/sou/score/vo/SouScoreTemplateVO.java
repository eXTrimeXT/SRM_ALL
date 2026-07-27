package com.midea.cloud.srm.model.pj.sou.score.vo;

import com.midea.cloud.srm.model.pj.sou.score.dto.SouScoreTemplateDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("评分模板")
public class SouScoreTemplateVO extends SouScoreTemplateDTO {

    public static SouScoreTemplateVO init (SouScoreTemplateDTO souScoreTemplateDTO){
        SouScoreTemplateVO rsp = new SouScoreTemplateVO();
        BeanUtils.copyProperties(souScoreTemplateDTO, rsp);
        return rsp;
    }



}
