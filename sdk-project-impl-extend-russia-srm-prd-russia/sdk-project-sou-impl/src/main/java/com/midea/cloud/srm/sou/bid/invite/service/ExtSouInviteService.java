package com.midea.cloud.srm.sou.bid.invite.service;

import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtSouInviteService {

    /**
     * 更新是否投标
     * @param project
     * @param vendorId
     * @param isBid
     */
    public void updateIsBid(ExtSouProject project, Long vendorId, String isBid);

    /**
     * 更新是否废标
     * @param project
     * @param vendorId
     * @param isInvalidBid
     */
    public void updateIsInvalidBid(ExtSouProject project, Long vendorId, String isInvalidBid);

    /**
     * 是否中标
     * @param project
     * @param souInviteItems
     */
    public void updateIsSuccBidBatch(ExtSouProject project, List<SouInviteItem> souInviteItems);

    /**
     * 保存不参与原因
     * @param project
     * @param souInviteItems
     */
    public void updateNotParticipatingReasonBatch(ExtSouProject project, List<SouInviteItem> souInviteItems);

    /**
     * 保存得分
     * @param project
     * @param souInviteItems
     */
    public void updateScoreBatch(ExtSouProject project, List<SouInviteItem> souInviteItems);
}
