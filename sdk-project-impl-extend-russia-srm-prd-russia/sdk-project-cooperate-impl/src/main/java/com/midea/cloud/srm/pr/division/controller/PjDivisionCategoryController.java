package com.midea.cloud.srm.pr.division.controller;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.configguide.entity.ConfigGuide;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.supcooperate.ext.division.dto.PersonInChargeUserDto;
import com.midea.cloud.srm.pr.division.service.IPjDivisionCategoryService;
import com.midea.cloud.srm.pr.division.utils.ExportUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  品类分工规则表 前端控制器
 * </pre>
 *
 * @author chensl26@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-07-22 08:41:41
 *  修改内容:
 * </pre>
 */
@Api(value = "DivisionCategoryController", tags = {"品类分工规则"})
@RestController
@RequestMapping("/pj/division/divisionCategory")
public class PjDivisionCategoryController extends BaseController {

    @Autowired
    private IPjDivisionCategoryService iDivisionCategoryService;

    @Autowired
    private BaseClient baseClient;

    /**
     * 批量更新品类分工负责人
     * @param personInChargeUserDto 批量更新品类分工负责人参数
     */
    @ApiOperation(value = "批量更新负责人", notes = "批量更新负责人", httpMethod = "POST")
    @PostMapping("/batchUpdatePersonInChargeUser")
    public void batchUpdatePersonInChargeUser(@RequestBody PersonInChargeUserDto personInChargeUserDto) {
        iDivisionCategoryService.batchUpdatePersonInChargeUser(personInChargeUserDto);
    }
    /**
     * 编辑品类分工规则
     *
     * @param divisionCategories
     */
    @ApiOperation(value = "编辑品类分工规则", notes = "编辑品类分工规则", httpMethod = "POST")
    @PostMapping("/updateDivisionCategory")
    public void updateDivisionCategory(@RequestBody List<DivisionCategory> divisionCategories) {
        iDivisionCategoryService.saveOrUpdateDivisionCategory(divisionCategories);
    }

    /**
     * 新增品类分工规则
     *
     * @param divisionCategories
     */
    @ApiOperation(value = "新增品类分工规则", notes = "新增品类分工规则", httpMethod = "POST")
    @PostMapping("/saveDivisionCategory")
    public void saveDivisionCategory(@RequestBody List<DivisionCategory> divisionCategories) {
        iDivisionCategoryService.saveOrUpdateDivisionCategory(divisionCategories);
        baseClient.saveOrUpdateConfigGuide(new ConfigGuide().setDvConfig(YesOrNo.YES.getValue()));
    }



    /**
     * 导入文件模板下载
     *
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "导入文件模板下载", notes = "导入文件模板下载")
    @RequestMapping("/importModelDownload")
    public void importModelDownload(HttpServletResponse response) throws Exception {
        iDivisionCategoryService.importModelDownload(response);
    }


    /**
     * 获取标题
     * @return
     */
    @ApiOperation(value = "获取标题", notes = "获取标题", httpMethod = "POST")
    @PostMapping("/exportExcelTitle")
    public Map<String, String> exportExcelTitle() {
        return ExportUtils.getCategoryDvTitles();
    }

    /**
     * 导入功能新 2022-09-22
     * @param file
     * @param fileupload
     * @return
     * @throws Exception
     */
    @RequestMapping("/importExcelNew")
    public Map<String, Object> importExcelNew(@RequestParam("file") MultipartFile file, Fileupload fileupload) throws Exception {
        return iDivisionCategoryService.importExcelNew(file, fileupload);
    }
}
