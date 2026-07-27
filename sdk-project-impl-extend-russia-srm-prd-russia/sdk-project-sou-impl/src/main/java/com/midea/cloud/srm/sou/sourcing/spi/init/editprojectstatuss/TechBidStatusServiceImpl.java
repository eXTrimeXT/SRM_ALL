package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;

import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
public class TechBidStatusServiceImpl implements ProjectStatusService {
    @Override
    public ApiProjectStatusRangeVo<SouBiddingProStatusEnum> currentStatus() {
        ApiProjectStatusRangeVo<SouBiddingProStatusEnum> vo = new ApiProjectStatusRangeVo<>();
        vo.setStatus(SouBiddingProStatusEnum.TECH_BID);

        vo.setNextStatus(Arrays.asList(SouBiddingProStatusEnum.TECH_BID_END, SouBiddingProStatusEnum.TECH_BID_OPEN));
        return vo;
    }
}
