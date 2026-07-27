package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;

import java.util.Arrays;
/**
 * 备注
 * @author huangbf3
 */
public class ArchiveTodoStatusServiceImpl implements ProjectStatusService {
    @Override
    public ApiProjectStatusRangeVo<SouBiddingProStatusEnum> currentStatus() {
        ApiProjectStatusRangeVo<SouBiddingProStatusEnum> vo = new ApiProjectStatusRangeVo<>();
        vo.setStatus(SouBiddingProStatusEnum.ARCHIVE_TODO);

        vo.setPreStatus(Arrays.asList(SouBiddingProStatusEnum.WIN_LOSS_NOTICE, SouBiddingProStatusEnum.BUS_BID_OPEN));

        vo.setNextStatus(Arrays.asList(SouBiddingProStatusEnum.ARCHIVE_DONE));
        return vo;
    }
}
