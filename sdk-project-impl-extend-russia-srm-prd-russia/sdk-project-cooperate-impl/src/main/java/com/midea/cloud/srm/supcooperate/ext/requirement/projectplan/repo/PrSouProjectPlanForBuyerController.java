package com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.repo;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.sies.client.SiesClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 招标计划 - 项目计划
 * PS: 专用于导入导出功能
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/08
 */
@RestController
@RequestMapping("/npm/pr/requirement/projectPlan")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouProjectPlanForBuyerController {

    @Autowired
    private SiesClient importClient;

    @ApiOperation(value = "下载项目计划导入模板", notes = "下载项目计划导入模板")
    @GetMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        importClient.downloadTemplate(this.getClass(), response);
    }

    @ApiOperation(value = "导入项目计划", notes = "导入项目计划")
    @PostMapping("/importExcel")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file, Fileupload fileupload, HttpServletRequest request) {
        return importClient.importExcel(this.getClass(), file, fileupload, request).getResult();
    }

}
