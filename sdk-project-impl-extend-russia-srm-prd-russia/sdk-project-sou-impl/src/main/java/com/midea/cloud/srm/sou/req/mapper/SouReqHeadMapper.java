package com.midea.cloud.srm.sou.req.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
* 寻源需求单头表
*
* @author xiaym13 xiaym13@meicloud.com
* @since 1.0.0 2023-10-04
*/
@Mapper
public interface SouReqHeadMapper extends BaseMapper<SouReqHead> {

    /**
     * 统计寻源需求创建供应商推荐单数量
     * @param param
     * @return
     */
    Integer countRecommPublic(Map<String, Object> param);

    /**
     * 统计招标计划池创建供应商推荐单数量
     * @param param
     * @return
     */
    Integer countRecommWithoutPublic(Map<String, Object> param);

}
