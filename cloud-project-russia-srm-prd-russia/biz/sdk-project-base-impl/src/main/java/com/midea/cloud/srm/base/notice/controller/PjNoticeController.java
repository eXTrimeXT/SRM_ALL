//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.midea.cloud.srm.base.notice.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.base.notice.service.INoticeService;
import com.midea.cloud.srm.base.notice.service.IPjNoticeService;
import com.midea.cloud.srm.model.base.notice.PjNotice;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeDetailDTO;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeRequestDTO;
import com.midea.cloud.srm.model.base.notice.dto.PjNoticeSaveRequestDTO;
import com.midea.cloud.srm.model.base.notice.entry.Notice;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@RestController
@RequestMapping({"/notice/notice/pj"})
@Api(
        value = "NoticeController",
        tags = {"公告"}
)
public class PjNoticeController extends BaseController {
    @Autowired
    private IPjNoticeService iNoticeService;

    public PjNoticeController() {
    }

    @PostMapping({"/listPage"})
    @ApiOperation(
            value = "分页查询公告列表",
            notes = "分页查询公告列表"
    )
    public PageInfo<Notice> listPage(@RequestBody PjNoticeRequestDTO noticeRequestDTO) {
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        if (!StringUtils.equals(UserType.VENDOR.name(), loginAppUser.getUserType()) && !StringUtils.equals(UserType.BUYER.name(), loginAppUser.getUserType())) {
            Assert.isTrue(false, "用户类型不存在");
        }

        if (StringUtils.equals(UserType.VENDOR.name(), loginAppUser.getUserType())) {
            noticeRequestDTO.setVendorId(loginAppUser.getCompanyId());
        }

        PageUtil.startPage(noticeRequestDTO.getPageNum(), noticeRequestDTO.getPageSize());
        return new PageInfo(this.iNoticeService.listPage(noticeRequestDTO));
    }

    @GetMapping({"/get"})
    @ApiOperation(
            value = "根据ID获取公告信息",
            notes = "根据ID获取公告信息"
    )
    public PjNoticeDetailDTO get(@RequestParam("noticeId") Long noticeId) {
        Assert.notNull(noticeId, "公告ID不能为空");
        return this.iNoticeService.getDetail(noticeId);
    }

    @PostMapping({"/add"})
    @ApiOperation(
            value = "新增公告信息",
            notes = "新增公告信息"
    )
    public void add(@RequestBody PjNoticeSaveRequestDTO noticeSaveRequestDTO) {
        Assert.notNull(noticeSaveRequestDTO, "数据错误");
        Assert.notNull(noticeSaveRequestDTO.getNotice(), "数据错误");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getNoticeType(), "公告分类不能为空");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getTitle(), "标题不能为空");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getDetail(), "正文不能为空");
        noticeSaveRequestDTO.getNotice().setNoticeStatus("UNPUBLISHED");
        this.iNoticeService.saveOrUpdate(noticeSaveRequestDTO);
    }

    @GetMapping({"/delete"})
    @ApiOperation(
            value = "删除公告信息",
            notes = "删除公告信息"
    )
    public void delete(@RequestParam("noticeId") Long noticeId) {
        Assert.notNull(noticeId, "noticeId不能为空");
        this.iNoticeService.delete(noticeId);
    }

    @PostMapping({"/modify"})
    @ApiOperation(
            value = "修改公告信息",
            notes = "修改公告信息"
    )
    public void modify(@RequestBody PjNoticeSaveRequestDTO noticeSaveRequestDTO) {
        Assert.notNull(noticeSaveRequestDTO, "数据错误");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getNoticeId(), "公告ID不能为空");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getNoticeType(), "公告分类不能为空");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getTitle(), "标题不能为空");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getDetail(), "正文不能为空");
        this.iNoticeService.saveOrUpdate(noticeSaveRequestDTO);
    }

    @PostMapping({"/publish"})
    @ApiOperation(
            value = "发布公告信息",
            notes = "发布公告信息"
    )
    public void publish(@RequestBody PjNoticeSaveRequestDTO noticeSaveRequestDTO) {
        Assert.notNull(noticeSaveRequestDTO, "数据错误");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getNoticeType(), "公告分类不能为空");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getTitle(), "标题不能为空");
        Assert.notNull(noticeSaveRequestDTO.getNotice().getDetail(), "正文不能为空");
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        noticeSaveRequestDTO.getNotice().setNoticeStatus("PUBLISHED");
        noticeSaveRequestDTO.getNotice().setPublishBy(loginAppUser.getUsername());
        noticeSaveRequestDTO.getNotice().setPublisherId(loginAppUser.getUserId());
        noticeSaveRequestDTO.getNotice().setPublishTime(new Date());
        this.iNoticeService.saveOrUpdate(noticeSaveRequestDTO);
    }

    @PostMapping({"/generate"})
    @ApiOperation(
            value = "发布公告信息",
            notes = "发布公告信息"
    )
    public void generateNotice(@RequestBody PjNotice notice) {
        notice.setNoticeStatus("PUBLISHED");
        notice.setPublishTime(new Date());
        this.iNoticeService.generate(notice);
    }
}
