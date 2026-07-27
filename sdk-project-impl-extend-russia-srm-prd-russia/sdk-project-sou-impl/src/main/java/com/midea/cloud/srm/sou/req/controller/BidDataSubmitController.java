package com.midea.cloud.srm.sou.req.controller;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesResponse;
import com.midea.cloud.srm.sies.pojo.SiesResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  招标资料递交
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/23 17:34
 *  修改内容:
 * </pre>
 */
@Api(value = "PrShareStockController", tags = {"招标资料递交"})
@RestController
@Slf4j
@RequestMapping("/bidDataSubmit")
public class BidDataSubmitController {

    @Autowired
    private SiesClient siesClient;

    @ApiOperation(value = "导入模板下载", notes = "导入模板下载")
    @RequestMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        siesClient.downloadTemplate(this.getClass(), response);
    }

    @ApiOperation(value = "excel导入", notes = "excel导入", httpMethod = "POST")
    @PostMapping("/import")
    public Map<String, Object> importV2(@RequestParam("file") MultipartFile file, Fileupload fileupload, HttpServletRequest request) throws Exception {
        Assert.notNull(file, "文件不能为空");
        SiesResponse importResponse = siesClient.importExcel(this.getClass(), file, fileupload, request);
        SiesResult result = importResponse.getResult();
        if (importResponse.isSuccess()) {
            //返回数据给前端
            SiesImportParam importParam = (SiesImportParam) importResponse.getDataParam();
            Collection<List<?>> data = importParam.getExtData().getAllManualData().values();
            if (CollectionUtils.isNotEmpty(data)) {
                result.put("data", data.iterator().next());
            }
        }
        return result;
    }
}
