package com.midea.cloud.srm.sou.expert.service;

import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertLatestApplyQueryDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertQueryDTO;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreQueryDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.vo.*;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 专家库 - 查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
public interface ExtSouExpertQueryService {

    /**
     * 查询专家申请详情
     * @param expertApplyId {@link ExtSouExpertApply#getExpertApplyId}
     * @return
     */
    @Nullable
    ExtSouExpertApplyVO getApplyInfoByApplyId(long expertApplyId);

    /**
     * 根据用户ID查询最新的专家申请详情
     * @param param 参数
     * @return 返回
     */
    @Nullable
    ExtSouExpertApplyVO getLatestApplyInfoByUserId(ExtSouExpertLatestApplyQueryDTO param);

    /**
     * 专家库列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtSouExpertQueryVO> queryExperts(ExtSouExpertQueryDTO queryParam);

    /**
     * 专家评审列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtSouExpertScoreQueryVO> queryExpertScores(ExtSouExpertScoreQueryDTO queryParam);

}
