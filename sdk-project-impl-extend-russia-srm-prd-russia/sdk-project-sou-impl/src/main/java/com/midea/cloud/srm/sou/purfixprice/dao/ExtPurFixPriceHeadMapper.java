package com.midea.cloud.srm.sou.purfixprice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouProjectVO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceInqQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceLineGroupQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.sou.purfixprice.ExtPurFixPriceDownLoadVo;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurFixPriceHeadMapper extends BaseMapper<ExtPurFixPriceHead> {

    /**
     * queryPurInq
     * @param queryParam
     * @return
     */
    List<ApiPurInqSouProjectVO> queryPurInq(ExtPurFixPriceInqQueryDTO queryParam);

    /**
     * getItemIdsForPage
     * @param queryParam
     * @return
     */
    List<Long> getItemIdsForPage(ExtPurFixPriceLineGroupQueryDTO queryParam);

    /**
     * getUnit
     * @return
     */
    List<ExtPurFixPriceDownLoadVo>getUnit();
}
