package com.midea.cloud.srm.perf.template.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.perf.template.PerfTemplateStatusEnum;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PjPerfTemplateHeaderQueryDTO;
import com.midea.cloud.srm.model.perf.template.dto.PerfTemplateHeaderQueryDTO;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateCategory;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateHeader;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateCategoryService;
import com.midea.cloud.srm.perf.template.service.IPerfTemplateHeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 *  绩效模型头表 前端控制器
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-10-27 16:41:07
 *  修改内容:
 * </pre>
 */
@Api(value = "PjPerfTemplateHeaderController", tags = {"绩效模型头-二开"})
@RestController
@RequestMapping("/pj/template")
@Slf4j
public class PjPerfTemplateHeaderController extends BaseController {
    @Autowired
    private IPerfTemplateHeaderService iPerfTemplateHeaderService;

    @Autowired
    private IPerfTemplateCategoryService iPerfTemplateCategoryService;

    /**
     * Description 分页查询绩效模型头表信息
     *
     * @return
     * @throws
     * @Param pefTemplateHeader 绩效模型头表实体类
     * @Date 2020.05.28
     **/
    @ApiOperation(value = "分页查询绩效模型头表信息", notes = "分页查询绩效模型头表信息", httpMethod = "POST")
    @PostMapping("/listPefTemplateHeaderPage")
    public PageInfo<PerfTemplateHeader> listPefTemplateHeaderPage(@RequestBody PjPerfTemplateHeaderQueryDTO queryDTO) {
        List<PerfTemplateCategory> perfTemplateCategories = new ArrayList<>();
        if (queryDTO.getCategoryId() != null) {
            perfTemplateCategories = iPerfTemplateCategoryService.list(Wrappers.lambdaQuery(PerfTemplateCategory.class)
                    .eq(PerfTemplateCategory::getCategoryId, queryDTO.getCategoryId()));
            if (CollectionUtils.isEmpty(perfTemplateCategories)) {
                return new PageInfo<PerfTemplateHeader>(new ArrayList<>());
            }
        }
        List<Long> headIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(perfTemplateCategories)) {
            headIds = perfTemplateCategories.stream().map(PerfTemplateCategory::getTemplateHeadId).distinct().collect(Collectors.toList());
        }
        PageUtil.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<PerfTemplateHeader> list = iPerfTemplateHeaderService.list(Wrappers.lambdaQuery(PerfTemplateHeader.class)
                .like(StringUtils.isNotEmpty(queryDTO.getTemplateName()), PerfTemplateHeader::getTemplateName, queryDTO.getTemplateName())
                .eq(StringUtils.isNotEmpty(queryDTO.getTemplateStatus()), PerfTemplateHeader::getTemplateStatus, queryDTO.getTemplateStatus())
                .eq(Objects.nonNull(queryDTO.getOrganizationId()), PerfTemplateHeader::getOrganizationId, queryDTO.getOrganizationId())
                .like(Objects.nonNull(queryDTO.getVersion()), PerfTemplateHeader::getVersion, queryDTO.getVersion())
                .in(CollectionUtils.isNotEmpty(headIds), PerfTemplateHeader::getTemplateHeadId, headIds)
                .eq(StringUtils.isNotEmpty(queryDTO.getAttribute1()), PerfTemplateHeader::getAttribute1, queryDTO.getAttribute1())
                .orderByDesc(PerfTemplateHeader::getLastUpdateDate));
        return new PageInfo<PerfTemplateHeader>(list);
    }


    @ApiOperation(value = "绩效模型启动/禁用", notes = "绩效模型启动/禁用", httpMethod = "POST")
    @PostMapping("/enablePefTemplateHeader")
    public String enablePefTemplateHeader(@RequestBody PerfTemplateHeader pefTemplateHeader) {
        Assert.notNull(pefTemplateHeader, ResultCode.MISSING_SERVLET_REQUEST_PARAMETER.getMessage());
        iPerfTemplateHeaderService.updateById(pefTemplateHeader);
        return ResultCode.SUCCESS.getMessage();
    }

    /**
     * Description 获取有效的绩效模型集合,按订单化还是项目化
     **/
    @ApiOperation(value = "获取有效的绩效模型集合", notes = "获取有效的绩效模型集合", httpMethod = "GET")
    @GetMapping("/getValidTemplateHeader")
    public List<PerfTemplateHeader> getValidTemplateHeader(@RequestParam("attribute1") String attribute1) {
        PerfTemplateHeader queryTemplateHeader = new PerfTemplateHeader();
        queryTemplateHeader.setAttribute1(attribute1);
        queryTemplateHeader.setDeleteFlag(Enable.N.toString());
        queryTemplateHeader.setTemplateStatus(PerfTemplateStatusEnum.VALID.getValue());
        return iPerfTemplateHeaderService.list(new QueryWrapper<>(queryTemplateHeader));
    }

}
