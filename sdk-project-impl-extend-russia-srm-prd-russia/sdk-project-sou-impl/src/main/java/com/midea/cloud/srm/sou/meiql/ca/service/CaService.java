package com.midea.cloud.srm.sou.meiql.ca.service;

import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaHistoryPriceDto;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
public interface CaService {
    /**
     * 备注
     * @param projectId 参数
     * @return 参数
     * @throws Exception
     */
    CaDTO add(Long projectId) throws Exception;

    /**
     * 备注
     * @param caHistoryPriceList 参数
     * @return
     */
    void saveBidHistoryPrice(List<CaHistoryPriceDto> caHistoryPriceList);
}
