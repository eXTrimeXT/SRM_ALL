package com.midea.cloud.srm.sup.ext.pjinfochange.mapper;

import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;

import java.util.List;
import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/04/11/ $
 * @Description: 供应商信息变更mapper
 */
public interface PjInfoChangeMapper {

    /**
     * 查询品类分工-二级分类
     * @param param
     * @return
     */
    List<Map<String, Object>> listTwoLevelWithDivisionCategory(Map<String, Object> param);
}
