package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;

import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
public class TechBidOpenStatusServiceImpl implements ProjectStatusService {
    @Override
    public ApiProjectStatusRangeVo<SouBiddingProStatusEnum> currentStatus() {
        ApiProjectStatusRangeVo<SouBiddingProStatusEnum> vo = new ApiProjectStatusRangeVo<>();
        vo.setStatus(SouBiddingProStatusEnum.TECH_BID_OPEN);


        vo.setNextStatus(Arrays.asList(SouBiddingProStatusEnum.TECH_BID_EVA, SouBiddingProStatusEnum.BUS_BID));
        return vo;
    }
}
