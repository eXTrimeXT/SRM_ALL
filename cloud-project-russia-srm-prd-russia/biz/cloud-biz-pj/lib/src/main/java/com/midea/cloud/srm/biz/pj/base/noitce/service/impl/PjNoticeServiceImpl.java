package com.midea.cloud.srm.biz.pj.base.noitce.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.biz.pj.base.noitce.mapper.PjNoticeMapper;
import com.midea.cloud.srm.biz.pj.base.noitce.service.IPjNoticeService;
import com.midea.cloud.srm.model.base.notice.dto.NoticeRequestDTO;
import com.midea.cloud.srm.model.pj.base.notice.entity.PjNotice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *  公告表 服务实现类
 * </pre>
 *
 * @author huangbf3
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020/4/8 16:45
 *  修改内容:
 * </pre>
 */
@Service
public class PjNoticeServiceImpl extends BaseServiceImpl<PjNoticeMapper, PjNotice> implements IPjNoticeService {

    @Override
    public List<PjNotice> listPage(NoticeRequestDTO noticeRequestDTO) {
        return this.getBaseMapper().findList(noticeRequestDTO);
    }

    @Override
    public void generate(PjNotice notice) {
        Long sourceFormId = notice.getSourceFormId();
        String noticeSource = notice.getNoticeSource();
        PjNotice dbNotice = this.getBaseMapper().selectOne(Wrappers.lambdaQuery(PjNotice.class)
                .eq(PjNotice::getNoticeSource, noticeSource)
                .eq(PjNotice::getSourceFormId, sourceFormId)
        );
        if (Objects.isNull(dbNotice)) {
            this.getBaseMapper().insert(notice);
        }else {
            dbNotice.setDetail(notice.getDetail());
            this.getBaseMapper().updateById(dbNotice);
        }
    }
}
