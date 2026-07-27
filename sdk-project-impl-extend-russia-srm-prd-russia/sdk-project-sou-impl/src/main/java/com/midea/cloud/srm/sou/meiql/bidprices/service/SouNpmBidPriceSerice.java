package com.midea.cloud.srm.sou.meiql.bidprices.service;

import com.midea.cloud.srm.model.sou.bidprices.dto.BidPriceDto;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface SouNpmBidPriceSerice {

    /**
     * generateBidPrice
     * @param caDTO
     * @return
     */
    @ApiOperation("定标申请审批通过生成招标价格库")
    List<BidPriceDto> generateBidPrice(CaDTO caDTO);

    /**
     * generateBidPriceById
     * @param caId
     * @return
     */
    @ApiOperation("定标申请审批通过生成招标价格库")
    List<BidPriceDto> generateBidPriceById(Long caId);
}
