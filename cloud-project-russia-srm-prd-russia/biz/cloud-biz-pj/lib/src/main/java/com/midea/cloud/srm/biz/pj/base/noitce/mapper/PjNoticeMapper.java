package com.midea.cloud.srm.biz.pj.base.noitce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.base.notice.dto.NoticeDetailDTO;
import com.midea.cloud.srm.model.base.notice.dto.NoticeRequestDTO;
import com.midea.cloud.srm.model.pj.base.notice.entity.PjNotice;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 公告表 Mapper 接口
 * </p>
 *
 * @author huangbf3
 * @since 2020-02-12
 */
public interface PjNoticeMapper extends BaseMapper<PjNotice> {

    /**
     * 备注
     * @param noticeRequestDTO
     * @return
     */
    List findList(NoticeRequestDTO noticeRequestDTO);

    /**
     * 备注
     * @param noticeId
     * @return
     */
    NoticeDetailDTO getDetail(@Param("noticeId")Long noticeId);

    /**
     * getOrgRelLastUpdateDate
     * @return
     */
    Date getOrgRelLastUpdateDate();
}