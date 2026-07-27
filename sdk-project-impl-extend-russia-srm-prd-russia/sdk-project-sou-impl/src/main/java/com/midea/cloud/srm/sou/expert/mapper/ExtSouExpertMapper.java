package com.midea.cloud.srm.sou.expert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertQueryDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertQueryVO;

import java.util.List;

/**
 * 寻源 - 专家库
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
public interface ExtSouExpertMapper extends BaseMapper<ExtSouExpert> {

    /**
     * 专家库列表查询
     * @param queryParam 参数
     * @return 返回
     */
    List<ExtSouExpertQueryVO> queryExperts(ExtSouExpertQueryDTO queryParam);

}
