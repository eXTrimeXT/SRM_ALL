package com.midea.cloud.srm.biz.pj.base.noitce.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.base.noitce.service.IPjNoticeService;
import com.midea.cloud.srm.model.base.notice.dto.NoticeDetailDTO;
import com.midea.cloud.srm.model.base.notice.dto.NoticeRequestDTO;
import com.midea.cloud.srm.model.base.notice.dto.NoticeSaveRequestDTO;
import com.midea.cloud.srm.model.base.notice.entry.Notice;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.pj.base.notice.entity.PjNotice;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <pre>
 *  公告表 前端控制器
 * </pre>
 *
 * @author huangbf3
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020/4/8 16:41
 *  修改内容:
 * </pre>
 */
@RestController
@RequestMapping("/pjnotice/notice")
@Api(value = "PjNoticeController", tags = "公告")
public class PjNoticeController extends BaseController {
    @Autowired
    private IPjNoticeService iPjNoticeService;

    /**
     * 分页查询公告列表
     * @param
     */
    @PostMapping("/listPage")
    @ApiOperation(value = "分页查询公告列表", notes = "分页查询公告列表")
    public PageInfo<PjNotice> listPage(@RequestBody NoticeRequestDTO noticeRequestDTO) {
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        if(!StringUtils.equals(UserType.BUYER.name(), loginAppUser.getUserType())){
            Assert.isTrue(false, "用户类型不存在");
        }
        PageUtil.startPage(noticeRequestDTO.getPageNum(), noticeRequestDTO.getPageSize());
        return new PageInfo(iPjNoticeService.listPage(noticeRequestDTO));
    }

    /**
     * 获取公告信息
     * @param
     */
    @GetMapping("/get")
    @ApiOperation(value = "根据ID获取公告信息", notes = "根据ID获取公告信息")
    public PjNotice get(@RequestParam("noticeId") Long noticeId) {
        Assert.notNull(noticeId, "公告ID不能为空");
        return iPjNoticeService.getById(noticeId);
    }



    /**
     * 新增
     * @param pjNotice
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增公告信息", notes = "新增公告信息")
    public void add(@RequestBody PjNotice pjNotice) {
        Assert.notNull(pjNotice, "数据错误");
        Assert.notNull(pjNotice.getNoticeType(), "公告分类不能为空");
        Assert.notNull(pjNotice.getTitle(), "标题不能为空");
        Assert.notNull(pjNotice.getDetail(), "正文不能为空");

        pjNotice.setNoticeStatus("UNPUBLISHED");
        if(pjNotice.getNoticeId()==null){
            pjNotice.setNoticeId(IdGenrator.generate());
        }
        iPjNoticeService.saveOrUpdate(pjNotice);
    }

    /**
     * 删除
     * @param noticeId
     */
    @GetMapping("/delete")
    @ApiOperation(value = "删除公告信息", notes = "删除公告信息")
    public void delete(@RequestParam("noticeId") Long noticeId) {
        Assert.notNull(noticeId, "noticeId不能为空");
        iPjNoticeService.removeById(noticeId);
    }

    /**
     * 修改
     * @param pjNotice
     */
    @PostMapping("/modify")
    @ApiOperation(value = "修改公告信息", notes = "修改公告信息")
    public void modify(@RequestBody PjNotice pjNotice) {
        Assert.notNull(pjNotice, "数据错误");
        Assert.notNull(pjNotice.getNoticeId(), "公告ID不能为空");
        Assert.notNull(pjNotice.getNoticeType(), "公告分类不能为空");
        Assert.notNull(pjNotice.getTitle(), "标题不能为空");
        Assert.notNull(pjNotice.getDetail(), "正文不能为空");

        iPjNoticeService.saveOrUpdate(pjNotice);
    }

    /**
     * 发布
     * @param pjNotice
     */
    @PostMapping("/publish")
    @ApiOperation(value = "发布公告信息", notes = "发布公告信息")
    public void publish(@RequestBody PjNotice pjNotice) {
        Assert.notNull(pjNotice, "数据错误");
        Assert.notNull(pjNotice.getNoticeType(), "公告分类不能为空");
        Assert.notNull(pjNotice.getTitle(), "标题不能为空");
        Assert.notNull(pjNotice.getDetail(), "正文不能为空");

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        pjNotice.setNoticeStatus("PUBLISHED");
        pjNotice.setPublishBy(loginAppUser.getUsername());
        pjNotice.setPublisherId(loginAppUser.getUserId());
        pjNotice.setPublishTime(new Date());

        iPjNoticeService.saveOrUpdate(pjNotice);
    }


    /**
     * 生成(本接口不对前端调用开放)
     *
     * @param notice 请注意
     */
    @PostMapping("/generate")
    @ApiOperation(value = "发布公告信息", notes = "发布公告信息")
    public void generateNotice(@RequestBody PjNotice notice) {
        notice.setNoticeStatus("PUBLISHED");
        notice.setPublishTime(new Date());
        iPjNoticeService.generate(notice);
    }
}
