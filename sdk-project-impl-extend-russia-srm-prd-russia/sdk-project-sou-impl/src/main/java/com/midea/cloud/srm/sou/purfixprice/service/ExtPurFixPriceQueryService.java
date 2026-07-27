package com.midea.cloud.srm.sou.purfixprice.service;

import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouProjectVO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceInqQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceLineGroupQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceQueryDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.vo.ExtPurFixPriceLineGroupVO;
import com.midea.cloud.srm.model.sou.purfixprice.vo.ExtPurFixPriceVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurFixPriceQueryService {

    /**
     * 定价列表查询
     * @param queryParam
     * @return
     */
    List<ExtPurFixPriceHead> listFixPrices(ExtPurFixPriceQueryDTO queryParam);

    /**
     * 查询可用的集采询比价
     * @param queryParam
     * @return
     */
    List<ApiPurInqSouProjectVO> queryPurInq(ExtPurFixPriceInqQueryDTO queryParam);

    /**
     * 查询定价单详情
     * @param purFixPriceHeadId
     * @return
     */
    ExtPurFixPriceVO getFixPrice(long purFixPriceHeadId);

    /**
     * 查询集采询比价中标明细
     * @param queryParam
     * @return
     */
    ExtPurFixPriceLineGroupVO getPurInqOrderItems(ExtPurFixPriceLineGroupQueryDTO queryParam);

    /**
     * 导出明细
     * @param purFixPriceHeadId
     * @param response
     * @throws IOException
     */
    void downloadExcel(long purFixPriceHeadId, HttpServletResponse response) throws IOException;
}
