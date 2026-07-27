//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.midea.cloud.srm.base.notice.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.base.notice.PjNotice;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeDetailDTO;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeRequestDTO;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeSaveRequestDTO;
import com.midea.cloud.srm.model.base.notice.entry.Notice;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface IPjNoticeService extends BaseService<PjNotice> {
    /**
     * saveOrUpdate
     * @param noticeSaveRequestDTO
     */
    void saveOrUpdate(PjNoticeSaveRequestDTO noticeSaveRequestDTO);

    /**
     * delete
     * @param noticeId
     */
    void delete(Long noticeId);

    /**
     * listPage
     * @param noticeRequestDTO
     * @return
     */
    List listPage(PjNoticeRequestDTO noticeRequestDTO);

    /**
     * getDetail
     * @param noticeId
     * @return
     */
    PjNoticeDetailDTO getDetail(Long noticeId);

    /**
     * generate
     * @param notice
     */
    void generate(PjNotice notice);
}
