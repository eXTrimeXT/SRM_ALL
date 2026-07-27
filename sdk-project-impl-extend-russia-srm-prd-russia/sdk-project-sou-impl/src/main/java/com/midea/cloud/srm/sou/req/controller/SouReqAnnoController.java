package com.midea.cloud.srm.sou.req.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.component.paging.PageRequest;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
import com.midea.cloud.srm.sou.req.vo.SouReqHeadVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *  功能名称
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
@Api(value = "SouAnnoController", tags = {"寻源不需要鉴权接口控制器"})
@RestController
@Slf4j
@RequestMapping("/sou-firstPage")
public class SouReqAnnoController {
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private PjSouClient pjSouClient;
    @Autowired
    protected QlService qlService;

    @ApiOperation(value = "公示寻源-分页查询", notes = "分页查询")
    @PostMapping("/souReqlistPage")
    public PageInfo<SouReqHead> listPage(@RequestBody SouReqHead souReqHead) {
        PageUtil.startPage(souReqHead.getPageNum(), souReqHead.getPageSize());
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(souReqHead.getPageNum());
        pageRequest.setPageSize(souReqHead.getPageSize());
        Page<Record> page = qlService.queryPageByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER)
                .eq(SouReqHead::getStatus, SouReqHeadStatusEnum.APPROVED.getCode())
                .contains(ObjectUtil.isNotEmpty(souReqHead.getProjectName()), SouReqHead::getProjectName, souReqHead.getProjectName())
                .eq(SouReqHead::getIsPublic, Enable.Y.name())
                .gt(SouReqHead::getPublicEndTime, new Date())
                .and(a -> a.isNull(SouReqHead::getClosePublicReason).or(o -> o.eq(SouReqHead::getClosePublicReason, "")))
                .orderByDesc(SouReqHead::getReleaseDate), pageRequest, Record.class);
        return PageUtil.convertToPageInfo(convertPage(page));
    }

    @ApiOperation(value = "公示寻源-查看详情", notes = "查看详情")
    @PostMapping("/souReqgetById")
    public SouReqHead getById(@RequestBody SouReqHead souReqHead) {
        SouReqHeadVO result = new SouReqHeadVO();
        souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, souReqHead.getReqHeadId(), SouReqHead.class);
        BeanUtil.copyProperties(souReqHead, result);
        //查询附件信息
        result.setFileUploads(baseClient.listSceneFileBatch(Collections.singletonList(souReqHead.getReqHeadId())));
        //公示模板
        result.setPjSourcePubconfig(pjSouClient.queryPubconfig(result.getPubconfigId()));
        return result;
    }

    public static Page<SouReqHead> convertPage(Page<Record> recordPage) {
        Page<SouReqHead> souReqHeadPage = new Page<>();
        souReqHeadPage.setPageNum(recordPage.getPageNum());
        souReqHeadPage.setPageSize(recordPage.getPageSize());
        souReqHeadPage.setTotal(recordPage.getTotal());
        souReqHeadPage.setPageCount(recordPage.getPageCount());
        List<SouReqHead> convertedRecords = new ArrayList<>();
        for (Record record : recordPage.getRecords()) {
            SouReqHead souReqHead = MeiQl.toValue(record, SouReqHead.class);
            convertedRecords.add(souReqHead);
        }
        souReqHeadPage.setRecords(convertedRecords);
        return souReqHeadPage;
    }
}
