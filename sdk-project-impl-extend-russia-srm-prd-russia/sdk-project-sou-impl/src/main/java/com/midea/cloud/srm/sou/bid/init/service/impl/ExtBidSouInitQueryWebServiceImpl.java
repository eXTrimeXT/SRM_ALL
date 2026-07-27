package com.midea.cloud.srm.sou.bid.init.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.common.utils.ZipUtil;
import com.midea.cloud.srm.feign.client.ExtFileCenterClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouMarginQueryDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitQueryWebService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouPriceTemplateMapper;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouPriceTemplateDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPriceTemplate;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouMarginService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ExtBidSouInitQueryWebServiceImpl implements ExtBidSouInitQueryWebService {

    @Autowired
    private ExtSouPriceTemplateMapper priceTemplateMapper;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private FileCenterClient fileCenterClient;

    @Autowired
    private ExtFileCenterClient extFileCenterClient;

    @Override
    public ApiExtSouPriceTemplateDto listPriceTemplate(Long projectId) {

        //查询模板
        List<ExtSouPriceTemplate> templateList = listSouPriceTemplate(-1L);
        //抹除模板ID，防止修改
        templateList.stream().forEach(t -> t.setTemplateId(null));

        //已选
        List<ExtSouPriceTemplate> selectTemplateList = new ArrayList<>();
        //备选
        List<ExtSouPriceTemplate> alternativeList = new ArrayList<>();

        if(!Objects.isNull(projectId) && Long.compare(projectId, -1L) != 0) {
            selectTemplateList = listSouPriceTemplate(projectId);
        }

        if(CollectionUtils.isEmpty(selectTemplateList)) {
            //取模板的默认选中字段
            Map<String, List<ExtSouPriceTemplate>> priceTemplateGroup = templateList.stream()
                    .sorted(Comparator.comparingInt(ExtSouPriceTemplate::getColnmnSort)).collect(Collectors.groupingBy(ExtSouPriceTemplate::getColumnDefault));
            selectTemplateList = priceTemplateGroup.getOrDefault(YesOrNo.YES.getValue(), new ArrayList<>());
            alternativeList = priceTemplateGroup.getOrDefault(YesOrNo.NO.getValue(), new ArrayList<>());
        }

        if(CollectionUtils.isEmpty(alternativeList)) {
            Set<String> selectKeySet = selectTemplateList.stream().map(ExtSouPriceTemplate::getColumnCode).collect(Collectors.toSet());
            alternativeList = templateList.stream().filter(t -> !selectKeySet.contains(t.getColumnCode())).peek(t->t.setColumnDefault(YesOrNo.NO.getValue())).collect(Collectors.toList());
        }

        //备选 N 已选 Y
        ApiExtSouPriceTemplateDto extSouPriceTemplateDto = new ApiExtSouPriceTemplateDto();
        extSouPriceTemplateDto.setProjectId(projectId);
        extSouPriceTemplateDto.setAlternativeList(alternativeList);
        extSouPriceTemplateDto.setSelectedList(selectTemplateList);
        return extSouPriceTemplateDto;
    }

    private List<ExtSouPriceTemplate> listSouPriceTemplate(Long projectId) {
        LambdaQueryWrapper<ExtSouPriceTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouPriceTemplate::getProjectId, projectId);
        queryWrapper.orderByAsc(ExtSouPriceTemplate::getColnmnSort);
        return priceTemplateMapper.selectList(queryWrapper);
    }

    @Override
    public void exportPriceExcelTemplate(Long projectId, HttpServletResponse response) throws Exception {
        ApiExtSouPriceTemplateDto priceTemplateDto = this.listPriceTemplate(projectId);
        List<List<String>> headList = new ArrayList<>();
        headList.add(Arrays.asList("包名"));
        priceTemplateDto.getBuyerAsSelectedList().stream().forEach(item -> {
            List<String> titleList = new ArrayList<>();
            titleList.add(item.getColumnName());
            headList.add(titleList);
        });

        // 获取输出流
        OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response,"报价模板");
        EasyExcel.write(outputStream).head(headList).sheet(0).sheetName("sheetName").doWrite(new ArrayList<>());

    }

    @Override
    public PageInfo<ExtSouMarginDto> listYearlyMargin(ApiExtSouMarginQueryDto param) {
        PageUtil.startPage(param.getPageNum(), param.getPageSize());
        LambdaQueryWrapper<ExtSouMargin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouMargin::getYearFlag, YesOrNo.YES.getValue());
        queryWrapper.eq(ExtSouMargin::getProjectId, SrmConstant.LONG_MINUS_ONE);
        queryWrapper.like(StringUtils.isNotBlank(param.getVendorName()), ExtSouMargin::getVendorName, param.getVendorName());
        queryWrapper.orderByDesc(ExtSouMargin::getMarginId);
        List<ExtSouMargin> marginList = marginService.list(queryWrapper);

        List<ExtSouMarginDto> marginDtoList = JSON.parseArray(JSON.toJSONString(marginList), ExtSouMarginDto.class);

        if(CollectionUtils.isNotEmpty(marginDtoList)) {
            LambdaQueryWrapper<ExtSouProject> query = new LambdaQueryWrapper<>();
            query.in(ExtSouProject::getProjectId, marginDtoList.stream().map(ExtSouMarginDto::getSourceProjectId).distinct().collect(Collectors.toList()));
            List<ExtSouProject> souProjectList = projectService.list(query);
            Map<Long, ExtSouProject> souProjectMap = souProjectList.stream().collect(Collectors.toMap(ExtSouProject::getProjectId, Function.identity()));
            marginDtoList.stream().forEach(m -> {
                ExtSouProject project = souProjectMap.getOrDefault(m.getSourceProjectId(), new ExtSouProject());
                m.setSouName(project.getSouName());
                m.setExtProjectNo(project.getExtProjectNo());
            });
        }

        PageInfo pageInfo = new PageInfo(marginList);
        pageInfo.setList(marginDtoList);
        return pageInfo;
    }

    @Override
    public void testZip(HttpServletResponse response) throws Exception {
        Fileupload fileupload = new Fileupload();
        fileupload.setFileuploadId(445341687115777L);
        Response response1 = extFileCenterClient.downloadFileByParamForAnon(fileupload);
        InputStream inputStream = response1.body().asInputStream();

        String fileName = "test-" + System.currentTimeMillis() + ".zip";
        //替换空格 不然会变为加号
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");

        Map<String, InputStream> srcI = new HashMap<>(50);
        srcI.put("招标资料1\\报价模板维护.xlsx", inputStream);

        ZipUtil.toZip(srcI, response.getOutputStream());

//        OutputStream outputStream = response.getOutputStream();
//        ZipOutputStream outputStream = new ZipOutputStream(response.getOutputStream());
//        ZipEntry zipEntry = new ZipEntry("报价模板维护.xlsx");
//        outputStream.putNextEntry(zipEntry);
//        byte[] bytes = new byte[2048];
//        int len = 0;
//        while ((len = inputStream.read(bytes)) != -1) {
//            outputStream.write(bytes, 0, len);
//        }
//        outputStream.closeEntry();
//        outputStream.flush();

    }
}
