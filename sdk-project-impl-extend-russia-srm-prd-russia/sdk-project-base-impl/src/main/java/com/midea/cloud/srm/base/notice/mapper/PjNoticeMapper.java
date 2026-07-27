package com.midea.cloud.srm.base.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.base.notice.PjNotice;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeDetailDTO;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeRequestDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface PjNoticeMapper  extends BaseMapper<PjNotice> {
    /**
     * findList
     * @param noticeRequestDTO
     * @return
     */
    List findList(PjNoticeRequestDTO noticeRequestDTO);

    /**
     * getDetail
     * @param noticeId
     * @return
     */
    PjNoticeDetailDTO getDetail(@Param("noticeId") Long noticeId);
}
