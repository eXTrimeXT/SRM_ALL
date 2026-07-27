package com.midea.cloud.srm.model.base.notice.dto;

import com.midea.cloud.srm.model.base.notice.PjNotice;
import com.midea.cloud.srm.model.base.notice.entry.NoticeVendor;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
public class PjNoticeSaveRequestDTO {

    private PjNotice notice;
    private List<NoticeVendor> noticeVendors;
    private List<Long> deleteNoticeVendorIds;

    public PjNoticeSaveRequestDTO() {
    }

    public PjNotice getNotice() {
        return this.notice;
    }

    public List<NoticeVendor> getNoticeVendors() {
        return this.noticeVendors;
    }

    public List<Long> getDeleteNoticeVendorIds() {
        return this.deleteNoticeVendorIds;
    }

    public void setNotice(final PjNotice notice) {
        this.notice = notice;
    }

    public void setNoticeVendors(final List<NoticeVendor> noticeVendors) {
        this.noticeVendors = noticeVendors;
    }

    public void setDeleteNoticeVendorIds(final List<Long> deleteNoticeVendorIds) {
        this.deleteNoticeVendorIds = deleteNoticeVendorIds;
    }

    @Override
    public String toString() {
        return "NoticeSaveRequestDTO(notice=" + this.getNotice() + ", noticeVendors=" + this.getNoticeVendors() + ", deleteNoticeVendorIds=" + this.getDeleteNoticeVendorIds() + ")";
    }
}
