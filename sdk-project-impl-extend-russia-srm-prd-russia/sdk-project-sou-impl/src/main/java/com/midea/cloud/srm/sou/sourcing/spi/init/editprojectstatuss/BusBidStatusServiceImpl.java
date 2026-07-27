package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;

import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
public class BusBidStatusServiceImpl implements ProjectStatusService {
    @Override
    public ApiProjectStatusRangeVo<SouBiddingProStatusEnum> currentStatus() {
        ApiProjectStatusRangeVo<SouBiddingProStatusEnum> vo = new ApiProjectStatusRangeVo<>();
        vo.setStatus(SouBiddingProStatusEnum.BUS_BID);

        vo.setNextStatus(Arrays.asList(SouBiddingProStatusEnum.TECH_BID_OPEN, SouBiddingProStatusEnum.BUS_BID_END, SouBiddingProStatusEnum.BUS_BID_OPEN));
        return vo;
    }
}
