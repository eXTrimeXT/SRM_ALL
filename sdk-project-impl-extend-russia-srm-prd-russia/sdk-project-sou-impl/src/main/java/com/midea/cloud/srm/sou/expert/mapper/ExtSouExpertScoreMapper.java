package com.midea.cloud.srm.sou.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreQueryDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertScoreQueryVO;

import java.util.List;

/**
 * 寻源 - 专家评审
 *
 * @author zhangwk12@meicloud.com
 */
public interface ExtSouExpertScoreMapper extends BaseMapper<ExtSouExpertScore> {

    /**
     * 专家评审列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtSouExpertScoreQueryVO> queryExpertScores(ExtSouExpertScoreQueryDTO queryParam);

}
