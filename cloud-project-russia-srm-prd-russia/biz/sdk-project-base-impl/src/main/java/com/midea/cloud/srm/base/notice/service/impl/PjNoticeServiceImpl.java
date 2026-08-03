//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.midea.cloud.srm.base.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.base.notice.mapper.PjNoticeMapper;
import com.midea.cloud.srm.base.notice.service.INoticeVendorService;
import com.midea.cloud.srm.base.notice.service.IPjNoticeService;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.notice.PjNotice;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeDetailDTO;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeRequestDTO;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeSaveRequestDTO;
import com.midea.cloud.srm.model.base.notice.entry.NoticeVendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
public class PjNoticeServiceImpl extends BaseServiceImpl<PjNoticeMapper, PjNotice> implements IPjNoticeService {
    @Autowired
    private INoticeVendorService iNoticeVendorService;
    @Autowired
    private FileCenterClient fileCenterClient;
    @Autowired
    private PjNoticeMapper noticeMapper;

    public PjNoticeServiceImpl() {
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public void delete(Long noticeId) {
        PjNotice notice = (PjNotice)this.getById(noticeId);
        this.removeById(noticeId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("NOTICE_ID", noticeId);
        this.iNoticeVendorService.remove(queryWrapper);
        if (notice.getFileRelationId() != null) {
            try {
                this.fileCenterClient.delete(notice.getFileRelationId());
            } catch (Exception var5) {
                Assert.isTrue(false, "文件刪除失败");
            }
        }

    }

    @Override
    public List listPage(PjNoticeRequestDTO PjNoticeRequestDTO) {
        return this.noticeMapper.findList(PjNoticeRequestDTO);
    }

    @Override
    public PjNoticeDetailDTO getDetail(Long noticeId) {
        PjNoticeDetailDTO dto = this.noticeMapper.getDetail(noticeId);
        List<NoticeVendor> vendors = new ArrayList();
        if (null != dto && !dto.getNoticeVendors().isEmpty()) {
            Iterator var4 = dto.getNoticeVendors().iterator();

            while(var4.hasNext()) {
                NoticeVendor vendor = (NoticeVendor)var4.next();
                if (null != vendor.getNoticeVendorId()) {
                    vendors.add(vendor);
                }
            }

            dto.setNoticeVendors(vendors);
        }

        return dto;
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public void saveOrUpdate(PjNoticeSaveRequestDTO noticeSaveRequestDTO) {
        if (noticeSaveRequestDTO.getNotice().getNoticeId() == null) {
            noticeSaveRequestDTO.getNotice().setNoticeId(IdGenrator.generate());
            this.save(noticeSaveRequestDTO.getNotice());
        } else {
            this.updateById(noticeSaveRequestDTO.getNotice());
            if (noticeSaveRequestDTO.getDeleteNoticeVendorIds() != null && !noticeSaveRequestDTO.getDeleteNoticeVendorIds().isEmpty()) {
                this.iNoticeVendorService.removeByIds(noticeSaveRequestDTO.getDeleteNoticeVendorIds());
            }
        }

        if (noticeSaveRequestDTO.getNoticeVendors() != null && !noticeSaveRequestDTO.getNoticeVendors().isEmpty()) {
            noticeSaveRequestDTO.getNoticeVendors().forEach((item) -> {
                if (item.getNoticeVendorId() == null) {
                    item.setNoticeVendorId(IdGenrator.generate());
                    item.setNoticeId(noticeSaveRequestDTO.getNotice().getNoticeId());
                    item.setReadStatus("N");
                }

            });
            this.iNoticeVendorService.saveOrUpdateBatch(noticeSaveRequestDTO.getNoticeVendors());
        }

    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public void generate(PjNotice notice) {
        Long sourceFormId = notice.getSourceFormId();
        String noticeSource = notice.getNoticeSource();
        PjNotice dbNotice = (PjNotice)this.noticeMapper.selectOne(
                (Wrapper)((LambdaQueryWrapper)Wrappers.lambdaQuery(PjNotice.class)
                        .eq(PjNotice::getNoticeSource, noticeSource)
                .eq(PjNotice::getSourceFormId, sourceFormId)));
        if (Objects.isNull(dbNotice)) {
            this.noticeMapper.insert(notice);
        } else {
            dbNotice.setDetail(notice.getDetail());
            this.noticeMapper.updateById(dbNotice);
        }

    }
}
