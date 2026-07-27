package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import io.swagger.annotations.ApiModel;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("招标状态流转工具类")
public class ApiProjectStatusFactory {

    public static ApiProjectStatusRangeVo<SouBiddingProStatusEnum> projectStatus(String projectStatus) {
        if(SouBiddingProStatusEnum.DRAW_UP.getCode().equals(projectStatus)) {
            return new DrawUpStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.TECH_BID.getCode().equals(projectStatus)) {
            return new TechBidStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.TECH_BID_END.getCode().equals(projectStatus)) {
            return new TechBidEndStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.TECH_BID_OPEN.getCode().equals(projectStatus)) {
            return new TechBidOpenStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.TECH_BID_EVA.getCode().equals(projectStatus)) {
            return new TechBidEvaStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.BUS_BID.getCode().equals(projectStatus)) {
            return new BusBidStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.BUS_BID_END.getCode().equals(projectStatus)) {
            return new BusBidEndStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.BUS_BID_OPEN.getCode().equals(projectStatus)) {
            return new BusBidOpenStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.CONFIRM_BID.getCode().equals(projectStatus)) {
            return new ConfirmBidStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.WIN_LOSS_NOTICE.getCode().equals(projectStatus)) {
            return new WinLossNoticeStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.NOTICE_ING.getCode().equals(projectStatus)) {
            return new NoticeIngStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.ARCHIVE_TODO.getCode().equals(projectStatus)) {
            return new ArchiveTodoStatusServiceImpl().currentStatus();
        }
        if(SouBiddingProStatusEnum.ARCHIVE_DONE.getCode().equals(projectStatus)) {
            return new ArchiveDoneStatusServiceImpl().currentStatus();
        }
        return null;
    }
}
