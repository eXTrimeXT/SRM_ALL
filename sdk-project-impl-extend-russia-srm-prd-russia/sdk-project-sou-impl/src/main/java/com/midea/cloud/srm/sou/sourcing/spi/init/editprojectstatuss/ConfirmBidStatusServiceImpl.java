package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;

import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
public class ConfirmBidStatusServiceImpl implements ProjectStatusService {
    @Override
    public ApiProjectStatusRangeVo<SouBiddingProStatusEnum> currentStatus() {
        ApiProjectStatusRangeVo<SouBiddingProStatusEnum> vo = new ApiProjectStatusRangeVo<>();
        vo.setStatus(SouBiddingProStatusEnum.CONFIRM_BID);

        vo.setPreStatus(Arrays.asList(SouBiddingProStatusEnum.BUS_BID_OPEN));

        vo.setNextStatus(Arrays.asList(SouBiddingProStatusEnum.WIN_LOSS_NOTICE));
        return vo;
    }
}
