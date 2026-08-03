package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.score;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 寻源核心 - 技术评分实体承载
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/26
 */
@Data
public class SouTechEditPO {

    @ApiModelProperty("评分头信息")
    private SouTechScoreHead scoreHead;
    @ApiModelProperty("评分明细")
    private List<SouTechScoreLine> scoreLineList;

}
