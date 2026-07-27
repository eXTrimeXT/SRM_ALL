package com.midea.cloud.srm.model.pj.scoreconfig.dto;

import com.midea.cloud.srm.model.pj.scoreconfig.entity.SccPjSouScoreConfig;
import com.midea.cloud.srm.model.pj.scoreconfigdetails.entity.SccPjSouScoreConfigDetail;
import lombok.Data;

import java.util.List;

/**
 * @author huangbf3
 */
@Data
public class SccPjSouScoreConfigDto {
    /** 保存 - SAVE   提交 - SUBMIT */
    private String type;

    private SccPjSouScoreConfig souScoreConfig;

    private List<SccPjSouScoreConfigDetail> souScoreConfigDetailList;
}
