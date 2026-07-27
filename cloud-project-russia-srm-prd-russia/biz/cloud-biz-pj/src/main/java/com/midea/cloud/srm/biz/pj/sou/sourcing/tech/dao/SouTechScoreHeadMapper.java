package com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressReviewQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.ApiSouTechProgressReviewVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 寻源核心 - 技术评分头信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/21
 */
public interface SouTechScoreHeadMapper extends BaseMapper<SouTechScoreHead> {

    /**
     * 工作小组成员: 查询需要技术评分的寻源单信息
     * @param queryParam
     * @param souType
     * @return
     */
    List<ApiSouTechProgressReviewVO> queryTechProgressReview(@Param("queryParam") ApiSouTechProgressReviewQueryDTO queryParam, @Param("souType") String souType);

}
