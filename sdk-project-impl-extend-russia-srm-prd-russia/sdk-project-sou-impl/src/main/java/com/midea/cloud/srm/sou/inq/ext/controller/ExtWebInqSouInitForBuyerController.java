package com.midea.cloud.srm.sou.inq.ext.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouItemQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorCheckDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorDelDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendorDel;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouItemQueryVO;
import com.midea.cloud.srm.model.sou.agreement.excel.ExportJcHtHeadLineData;
import com.midea.cloud.srm.model.sou.inq.vo.ExportInqReqInfoDataVo;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.supplier.vendororgcategory.vo.AiRecommendCompanyInfoVO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouInitEventService;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouInitQueryService;
import com.midea.cloud.srm.sou.inq.init.service.InqSouInitQueryWebService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 长城 - 询比价 - 立项
 * @author huangbf3
 */
@RestController
@RequestMapping("/npm/buyer/inq/init")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtWebInqSouInitForBuyerController {

    @Autowired
    private ExtInqSouInitQueryService extInqSouInitQueryService;
    @Autowired
    private ExtInqSouInitEventService extInqSouInitEventService;

    @Resource
    private BaseClient baseClient;

    @Resource
    private InqSouInitQueryWebService inqSouInitQueryWebService;

    @Resource
    private QlOpenClient qlOpenClient;

    @GetMapping("/getVendorAiRecommend/{projectId}")
    public List<AiRecommendCompanyInfoVO> getVendorAiRecommend(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return extInqSouInitQueryService.getVendorAiRecommend(projectId);
    }

    @GetMapping("/getHistoryMinPriceVendors/{projectId}")
    public List<AiRecommendCompanyInfoVO> getHistoryMinPriceVendors(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return extInqSouInitQueryService.getHistoryMinPriceVendors(projectId);
    }

    @ApiOperation("删除新增供应商(仅限立项阶段-逻辑删)")
    @PostMapping("/removeVendor")
    public void removeVendor(@RequestBody ExtPjInqSouVendorDelDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        extInqSouInitEventService.removeVendor(param);
    }

    @ApiOperation("查看被删除的邀请供应商")
    @PostMapping("/queryVendorDel")
    public PageInfo<ExtPJInqSouVendorDel> queryVendorDel(@RequestBody ExtPjInqSouVendorQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return new PageInfo<>(extInqSouInitQueryService.queryVendorDel(queryParam));
    }

    @ApiOperation("询比价物料明细报表查询")
    @PostMapping("/querySouItems")
    public PageInfo<ExtInqSouItemQueryVO> querySouItems(@RequestBody ExtInqSouItemQueryDTO queryParam) {
        return new PageInfo<>(extInqSouInitQueryService.querySouItems(queryParam));
    }

    @ApiOperation(value = "导出询价管理需求信息信息", notes = "导出询价管理需求信息信息", httpMethod = "POST")
    @PostMapping("/exportJcHtHeadLineData")
    public void exportJcHtHeadLineData(@RequestParam Long projectId, HttpServletResponse response) throws IOException {
        ApiInqSouInitDetailVO inq = inqSouInitQueryWebService.getInqProjectInfo(projectId);
        List<ApiInqSouItemVO> isi = inq.getRequireInfo();
        if (CollectionUtils.isNotEmpty(isi)) {
            List<ExportInqReqInfoDataVo> reList = new ArrayList<>();
            List<DictItemDTO> qyList = baseClient.listAllByDictCode("REGION");
            List<PurchaseUnit> unitList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(PurchaseUnit.class)
                    .eq(PurchaseUnit::getEnabled, YesOrNo.YES.getValue()), PurchaseUnit.class);
            Map<String, String> unitMap = unitList.stream()
                    .collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));
            isi.forEach(item -> {
                ExportInqReqInfoDataVo export = new ExportInqReqInfoDataVo();
                BeanUtil.copyProperties(item,export,true);
                if (StringUtils.isNotBlank(export.getExtAreaCode())) {
                    export.setExtAreaCode(dealArea(export.getExtAreaCode(), qyList));
                }
                String unit = unitMap.get(export.getUnit());
                if ( StringUtils.isNotEmpty(unit)) {
                    export.setUnit(unit);
                }
                reList.add(export);
            });
//            List<ExportInqReqInfoDataVo> reList = BeanUtil.copyToList(isi, ExportInqReqInfoDataVo.class);
//            reList.forEach(e -> {
//                if (StringUtils.isNotBlank(e.getExtAreaCode())) {
//                    e.setExtAreaCode(dealArea(e.getExtAreaCode(), qyList));
//                }
//                String unit = unitMap.get(e.getUnit());
//                if ( StringUtils.isNotEmpty(unit)) {
//                    e.setUnit(unit);
//                }
//            });
            // 获取输出流
            OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "需求信息");
            EasyExcel.write(outputStream).head(ExportInqReqInfoDataVo.class).sheet(0).sheetName("sheet1").doWrite(reList);
        } else {
            throw new BaseException("获取需求信息异常");
        }
    }

    public static String dealArea(String area, List<DictItemDTO> qyList) {
        String[] str = area.split(",");
        List<String> reList = new ArrayList<>();
        for (String b : str) {
            for (DictItemDTO a : qyList) {
                if (a.getDictItemCode().equals(b)) {
                    reList.add(a.getDictItemName());
                }
            }
        }
        return CollectionUtils.isNotEmpty(reList) ? String.join(",", reList) : "";
    }

    @ApiOperation("新增供应-检查供应商与已经邀请供应商是否存在关联关系")
    @PostMapping("/checkVendor")
    public Boolean checkVendor(@RequestBody ExtPjInqSouVendorCheckDTO param) {
//        SouUserTypeCheckUtils.checkIsBuyer();
        return extInqSouInitEventService.checkVendor(param);
    }
}
