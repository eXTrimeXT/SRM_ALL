package com.midea.cloud.srm.sou.req.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.sou.req.PreBidNotice;
import com.midea.cloud.srm.model.sou.req.PreBidNoticeVendor;
import com.midea.cloud.srm.model.sou.req.enums.PreBidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouSceneModuleCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 *  标前通知
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/28 11:03
 *  修改内容:
 * </pre>
 */
@Api(value = "PreBidNoticeController", tags = {"标前通知"})
@RestController
@Slf4j
@RequestMapping("/preBidNotice")
public class PreBidNoticeController {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;

    @ApiOperation(value = "统计公示和不公示供应商推荐数量", notes = "统计公示和不公示供应商推荐数量")
    @PostMapping("/listPage")
    public PageInfo<PreBidNotice> countRecomm(@RequestBody PreBidNotice preBidNotice) {
        PageUtil.startPage(preBidNotice.getPageNum(), preBidNotice.getPageSize());
        List<PreBidNotice> preBidNoticeList = qlService.queryByWrapper(QlWrappers.query(PreBidNotice.class)
                        .contains(ObjectUtil.isNotEmpty(preBidNotice.getBidNoticeNo()), PreBidNotice::getBidNoticeNo, preBidNotice.getBidNoticeNo())
                        .contains(ObjectUtil.isNotEmpty(preBidNotice.getProjectName()), PreBidNotice::getProjectName, preBidNotice.getProjectName())
                        .eq(PreBidNotice::getStatus, PreBidNoticeStatusEnum.ISSUED.getCode())
                        .orderByDesc(PreBidNotice::getCreationDate)
                , PreBidNotice.class);
        for (PreBidNotice et : preBidNoticeList) {
            //标前交流通知-技术交流供应商表
            List<PreBidNoticeVendor> noticeVendorList = qlService.queryByWrapper(QlWrappers.query(PreBidNoticeVendor.class)
                            .eq(PreBidNoticeVendor::getBidNoticeId, et.getBidNoticeId())
                    , PreBidNoticeVendor.class);
            et.setNoticeVendorList(noticeVendorList);
            //交流大纲签字版附件
            SceneFile sceneFileParam = (new SceneFile()).setBusinessId(et.getBidNoticeId()).setSceneModuleCode(SouSceneModuleCodeEnum.PRE_BID_COMM_SIGN_ATTACHMENT.toString());
            List<SceneFile> commSignFiles = baseClient.listSceneFile(sceneFileParam);
            et.setCommSignFiles(commSignFiles);
            //交流大纲编辑附件
            SceneFile commEditFilesParam = (new SceneFile()).setBusinessId(et.getBidNoticeId()).setSceneModuleCode(SouSceneModuleCodeEnum.PRE_BID_COMM_EDIT_ATTACHMENT.toString());
            List<SceneFile> commEditFiles = baseClient.listSceneFile(commEditFilesParam);
            et.setCommEditFiles(commEditFiles);
        }
        return new PageInfo<>(preBidNoticeList);
    }


}
