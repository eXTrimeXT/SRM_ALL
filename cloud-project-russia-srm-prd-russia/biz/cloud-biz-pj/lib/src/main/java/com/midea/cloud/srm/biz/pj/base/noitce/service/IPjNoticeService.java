package com.midea.cloud.srm.biz.pj.base.noitce.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.base.notice.dto.NoticeRequestDTO;
import com.midea.cloud.srm.model.pj.base.notice.entity.PjNotice;

import java.util.List;

/**
 * <pre>
 *  公告表 服务类
 * </pre>
 *
 * @author huangbf3
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020/4/8 16:43
 *  修改内容:
 * </pre>
 */
public interface IPjNoticeService extends BaseService<PjNotice> {
    /**
     * 备注
     * @param noticeRequestDTO
     * @return
     */
    List listPage(NoticeRequestDTO noticeRequestDTO);

    /**
     * 备注
     * @param pjNotice
     */
    void generate(PjNotice pjNotice);
}
