package com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao;

import com.midea.cloud.component.mphelper.mapper.CustomMapper;
import com.midea.cloud.srm.model.pj.sou.inq.vo.webapi.select.InqSouSelectCountWinningWebVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.supplier.info.dto.BidFrequency;

import java.util.List;

/**
 * 寻源.核心 - 供应商报价明细
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
public interface SouOrderItemMapper extends CustomMapper<SouOrderItem> {

    /**
     * 根据条件查询中标次数
     *
     * @param queryParams 参数
     * @return 返回值
     */
    List<BidFrequency> getByParamCountWinning(InqSouSelectCountWinningWebVO queryParams);

}
