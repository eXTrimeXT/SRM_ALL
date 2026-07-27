package com.midea.cloud.srm.sou.expert.excelhandler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertHrImportVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesResponse;
import com.midea.cloud.srm.sies.pojo.SiesResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 招标计划 - 项目计划
 * PS: 专用于导入导出功能
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/11/17
 */
@RestController
@RequestMapping("/npm/sou-expert/temp-hr")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtSouExpertTempQueryHrController {

    @Autowired
    private SiesClient importClient;

    @ApiOperation(value = "下载专家库hr信息模板", notes = "下载专家库hr信息模板")
    @GetMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        importClient.downloadTemplate(this.getClass(), response);
    }

    @ApiOperation(value = "导入专家库hr信息", notes = "导入专家库hr信息")
    @PostMapping("/importExcel")
    public void importExcel(@RequestParam("file") MultipartFile file, Fileupload fileupload, HttpServletRequest request) {
        SiesResponse importResponse = importClient.importExcel(this.getClass(), file, fileupload, request);
        if (importResponse.isSuccess()) {
            //返回数据给前端
            SiesImportParam importParam = (SiesImportParam) importResponse.getDataParam();
            Collection<List<?>> data = importParam.getExtData().getAllManualData().values();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(data)) {
                List<ExtSouExpertHrImportVO> voList = SouObjectXUtil.convertList(data.iterator().next(), ExtSouExpertHrImportVO.class);
// http://localhost:8847/api-sou/npm/sou-expert/temp-hr/importExcel
                ExcelWriter excelWriter = null;
                try {
                    ServletOutputStream outputStream = EasyExcelUtil.getServletOutputStream(HttpServletHolder.getResponse(), "专家库HR信息");
                    excelWriter = EasyExcel.write(outputStream).build();
                    WriteSheet mainSheet = EasyExcel
                            .writerSheet(0, "专家库HR信息")
                            .head(ExtSouExpertHrImportVO.class)
                            .build();
                    excelWriter.write(voList, mainSheet);
                } catch (IOException e) {
                    throw new IllegalArgumentException("获取io异常");
                } finally {
                    if (excelWriter != null) {
                        excelWriter.finish();
                    }
                }
            }
        }
    }

}
