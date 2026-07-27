package com.midea.cloud.srm.supcooperate.ext.requirement.pr.controller;

import com.google.common.collect.Lists;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.sies.client.SiesClient;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesResponse;
import com.midea.cloud.srm.sies.pojo.SiesResult;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PrShareStock;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.StockQueryDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Api(value = "PrShareStockController", tags = {"共享库存"})
@RestController
@Slf4j
@RequestMapping("/prShareStock")
public class PrShareStockController {

    @Autowired
    private SiesClient siesClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private BaseClient baseClient;

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

    @ApiOperation(value = "按编码查询共享库存", notes = "按编码查询共享库存")
    @PostMapping("/getStock")
    public Object getStock(@RequestBody StockQueryDTO queryDto) {
        String orgCode = queryDto.getOrgCode();
        Assert.hasText(orgCode, "业务实体不能为空");
        List<String> materialCodes = queryDto.getMaterialCodes();
        Assert.notEmpty(materialCodes, "物料编码不能为空");
        List<String> codes = materialCodes.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

        Map<String, BigDecimal> shareStockMap = new HashMap<>(15);
        Map<String, BigDecimal> actualStockMap = new HashMap<>(15);
        List<MaterialItem> materialItemList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(codes)) {
            materialItemList = baseClient.listMaterialByCodeBatch(codes);
            List<String> names = materialItemList.stream().map(MaterialItem::getMaterialName).collect(Collectors.toList());
            List<Record> records = qlService.queryByWrapper(QlWrappers.query("PrShareStock")
                    .select(PrShareStock::getMaterialName)
                    .select(QlQueryFieldWrapper.sum(PrShareStock::getQty))
                    .in(PrShareStock::getMaterialName, names)
                    .groupBy(PrShareStock::getMaterialName), Record.class);
            shareStockMap.putAll(records.stream().collect(Collectors.toMap(e -> e.get("materialName").toString(),
                    e -> new BigDecimal(e.get("qty").toString()), (v1, v2) -> v1)));

            Map<String, Object> params = new HashMap<>(15);
            params.put("storageorgid", orgCode);
            params.put("materialname", names);
            params.put("istotal", true);
            List<Map<String, Object>> stocks = pjProjectExtClient.getActualStock(params);
            if (CollectionUtils.isNotEmpty(stocks)) {
                actualStockMap.putAll(stocks.stream().collect(Collectors.toMap(e -> e.get("materialName").toString(),
                        e -> new BigDecimal(e.get("qty").toString()), (v1, v2) -> v1)));
            }
        }

        List<MaterialItem> finalMaterialItemList = materialItemList;
        return materialCodes.stream().map(e -> {
            String name = finalMaterialItemList.stream()
                    .filter(item-> Objects.equals(e,item.getMaterialCode()))
                    .map(MaterialItem::getMaterialName)
                    .findFirst()
                    .orElse(null);

            Record map = new Record();
            map.put(PrShareStock::getMaterialCode, e);
            map.put("shareStock", shareStockMap.getOrDefault(name, BigDecimal.ZERO));
            map.put("actualStock", actualStockMap.getOrDefault(name, BigDecimal.ZERO));
            return map;
        }).collect(Collectors.toList());
    }

}
