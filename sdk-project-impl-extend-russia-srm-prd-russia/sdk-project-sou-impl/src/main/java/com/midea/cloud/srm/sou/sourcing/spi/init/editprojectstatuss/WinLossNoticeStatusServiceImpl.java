package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;

import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
public class WinLossNoticeStatusServiceImpl implements ProjectStatusService {
    @Override
    public ApiProjectStatusRangeVo<SouBiddingProStatusEnum> currentStatus() {
        ApiProjectStatusRangeVo<SouBiddingProStatusEnum> vo = new ApiProjectStatusRangeVo<>();
        vo.setStatus(SouBiddingProStatusEnum.WIN_LOSS_NOTICE);

        vo.setPreStatus(Arrays.asList(SouBiddingProStatusEnum.CONFIRM_BID, SouBiddingProStatusEnum.BUS_BID_OPEN));

        vo.setNextStatus(Arrays.asList(SouBiddingProStatusEnum.NOTICE_ING));
        return vo;
    }
}
