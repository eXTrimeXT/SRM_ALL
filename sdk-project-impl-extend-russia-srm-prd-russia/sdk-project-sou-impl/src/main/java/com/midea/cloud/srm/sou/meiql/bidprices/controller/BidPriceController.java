package com.midea.cloud.srm.sou.meiql.bidprices.controller;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.bidprices.dto.BidPriceDto;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesResponse;
import com.midea.cloud.srm.sies.pojo.SiesResult;
import com.midea.cloud.srm.sou.meiql.bidprices.service.SouNpmBidPriceSerice;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@RestController
@RequestMapping("/npm/bidPrice")
public class BidPriceController {

    @Autowired
    private SiesClient siesClient;

    @Autowired
    private SouNpmBidPriceSerice souNpmBidPriceSerice;

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

    @ApiOperation("定标申请审批通过生成招标价格库")
    @GetMapping("/generateBidPriceById")
    List<BidPriceDto> generateBidPriceById(@RequestParam("caId") Long caId) {
        return souNpmBidPriceSerice.generateBidPriceById(caId);
    }
}
