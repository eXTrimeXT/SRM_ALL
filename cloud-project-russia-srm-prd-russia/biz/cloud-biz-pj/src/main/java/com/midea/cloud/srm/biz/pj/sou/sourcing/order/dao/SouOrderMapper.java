package com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 寻源.核心 - 供应商报价头信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public interface SouOrderMapper extends BaseMapper<SouOrder> {

    /**
     * 供应商报价列表查询
     * @param queryParam
     * @param souType
     * @return
     */
    List<ApiSouOrderQueryVO> listOrders(@Param("queryParam") ApiSouOrderQueryDTO queryParam, @Param("souType") String souType);

}
