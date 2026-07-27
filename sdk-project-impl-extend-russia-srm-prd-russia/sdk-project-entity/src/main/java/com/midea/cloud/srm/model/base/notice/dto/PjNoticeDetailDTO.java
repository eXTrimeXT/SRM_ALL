package com.midea.cloud.srm.model.base.notice.dto;

import com.midea.cloud.srm.model.base.notice.PjNotice;
import com.midea.cloud.srm.model.base.notice.entry.NoticeVendor;
import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class PjNoticeDetailDTO extends PjNotice {

    private List<NoticeVendor> noticeVendors;
}
