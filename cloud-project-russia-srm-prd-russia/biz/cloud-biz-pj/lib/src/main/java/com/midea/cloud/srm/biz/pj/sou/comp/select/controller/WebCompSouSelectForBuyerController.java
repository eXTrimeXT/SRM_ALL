package com.midea.cloud.srm.biz.pj.sou.comp.select.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.srm.biz.pj.sou.comp.select.service.CompSouSelectEventWebService;
import com.midea.cloud.srm.biz.pj.sou.comp.select.service.CompSouSelectQueryWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectEventService;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiCompSouOrderReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectItemQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto.PriceApprovalDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 竞价 - 投标控制
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/16
 */
@RestController
@RequestMapping("/buyer/comp/select")
@Api(tags = "竞价 - 评选(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class WebCompSouSelectForBuyerController {

    @Autowired
    private CompSouSelectQueryWebService compSouSelectQueryService;
    @Autowired
    private CompSouSelectEventWebService compSouSelectEventService;

    @PostMapping("/page")
    @ApiOperation("评选-历史报价查询")
    public PageInfo<ApiSouSelectItemQueryVO> listEvaluations(@RequestBody ApiSouSelectQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return new PageInfo<>(compSouSelectQueryService.listItemEvaluations(queryParam));
    }


    @PostMapping("/listOrderResult")
    @ApiOperation("评选-查看报价结果")
    public PageInfo<ApiSouSelectResultVO> listOrderResult(@RequestBody ApiSouSelectQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return new PageInfo<>(compSouSelectQueryService.listOrderResult(queryParam));
    }

    @PostMapping("/changeWinStatus")
    @ApiOperation("评选-入围/淘汰")
    public void changeWinStatus(@RequestBody ApiSouChangeWinStatusDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouSelectEventService.changeWinStatus(param);
    }

    @PostMapping("/changeSelectStatus")
    @ApiOperation("中标/落标")
    public void changeSelectStatus(@RequestBody ApiSouChangeSelectStatusDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouSelectEventService.changeSelectStatus(param);
    }

    /**
     * 流程走完的回调函数修改竞价单状态
     */
    @PostMapping("/selectWinNotice")
    @ApiOperation("中标通知")
    public void selectWinNotice(@RequestBody ApiSouChangeSelectStatusDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouSelectEventService.changeProjectStatus(param);
    }

    @PostMapping("/listWinNotice")
    @ApiOperation("评选-中标通知查询")
    public PageInfo<ApiSouSelectResultVO> listWinNotice(@RequestBody ApiSouSelectQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return new PageInfo<>(compSouSelectQueryService.listWinNotice(queryParam));
    }

    /**
     * 归档文件上传后改竞价单状态
     */
    @PostMapping("/placeOnFile")
    @ApiOperation("归档")
    public void placeOnFile(@RequestBody ApiSouPlaceOnFileDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouSelectEventService.placeOnFile(param);
    }

    @PostMapping("/getPlaceOnFileList")
    @ApiOperation("评选-归档查询")
    public PageInfo<SouPlaceOnFileDTO> listPlaceOnFile(@RequestBody ApiSouSelectQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return new PageInfo<>(compSouSelectQueryService.listPlaceOnFile(queryParam));
    }

    //导出
    @GetMapping("/downloadExcel/{projectId}")
    public void downloadExcel(@PathVariable("projectId") Long projectId, HttpServletResponse response) throws IOException {
        ApiSouSelectQueryDTO params = new ApiSouSelectQueryDTO();
        params.setProjectId(projectId);
        // 1: 查询数据
        List<ApiSouSelectResultVO> list = compSouSelectQueryService.listOrderResult(params);
        // 2: 设置导出excel头信息
        List<List<String>> headList = new ArrayList<>(30);
        headList.add(new ArrayList<>(Collections.singletonList("序号")));
        headList.add(new ArrayList<>(Collections.singletonList("物料名称")));
        headList.add(new ArrayList<>(Collections.singletonList("所属单位")));
        headList.add(new ArrayList<>(Collections.singletonList("状态")));
        headList.add(new ArrayList<>(Collections.singletonList("月均产量")));
        headList.add(new ArrayList<>(Collections.singletonList("计量单位")));
        List<String> tempL = Arrays.asList("最高价", "TODO");
        List<String> v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
        v.set(1, "名称");
        headList.add(v);
        v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
        v.set(1, "单价");
        headList.add(v);
        List<String> tempL2 = Arrays.asList("次高价", "TODO");
        List<String> v2 = JSON.parseArray(JSON.toJSONString(tempL2), String.class);
        v2.set(1, "名称");
        headList.add(v2);
        v2 = JSON.parseArray(JSON.toJSONString(tempL2), String.class);
        v2.set(1, "单价");
        headList.add(v2);
        List<String> tempL3 = Arrays.asList("第三高", "TODO");
        List<String> v3 = JSON.parseArray(JSON.toJSONString(tempL3), String.class);
        v3.set(1, "名称");
        headList.add(v3);
        v3 = JSON.parseArray(JSON.toJSONString(tempL3), String.class);
        v3.set(1, "单价");
        headList.add(v3);


        headList.add(new ArrayList<>(Collections.singletonList("价格差异率")));
        headList.add(new ArrayList<>(Collections.singletonList("备注")));
        headList.add(new ArrayList<>(Collections.singletonList("中标供应商")));
        headList.add(new ArrayList<>(Collections.singletonList("中标原因")));
        headList.add(new ArrayList<>(Collections.singletonList("是否流标")));
        headList.add(new ArrayList<>(Collections.singletonList("流标原因")));

        // 3: 设置行数据
        List<List<Object>> dataList = new ArrayList<>(list.size());
        // 查询数据
        int index = 0;
        for (ApiSouSelectResultVO itemInfo : list) {
            List<Object> row = new ArrayList<>(30);
            dataList.add(row);
            index++;
            row.add(index);
            row.add(itemInfo.getItemDesc());
            row.add(itemInfo.getAffiliatedUnit());
            String status="拟定";
            SouApprovalStatusEnum temp=itemInfo.getResultStatus();
            //审批状态,需要转字典
            if("SUBMITTED".equals(temp.toString())){
                status="审批中";
            }
            else if("REJECTED".equals(temp.toString())){
                status="已驳回";
            }
            else if("WITHDRAW".equals(temp.toString())){
                status="已撤回";
            }
            else  if("ABANDONED".equals(temp.toString())){
                status="已废弃";
            }
            else  if("APPROVED".equals(temp.toString())){
                status="已审批";
            }
            row.add(status);
            row.add(itemInfo.getMonthlyProduction());
            row.add(itemInfo.getMeteringUnit());
            row.add(itemInfo.getMaxVendorName());
            row.add(itemInfo.getMaxPrice());
            row.add(itemInfo.getSecondVendorName());
            row.add(itemInfo.getSecondPrice());
            row.add(itemInfo.getThirdVendorName());
            row.add(itemInfo.getThirdPrice());
            row.add(itemInfo.getDifferenceRate());
            row.add(itemInfo.getOrderRemark());
            row.add(itemInfo.getWinVendorName());
            row.add(itemInfo.getWinReason());
            row.add(itemInfo.getFailureBidFlag());
            row.add(itemInfo.getFailureReason());
        }
        // 4: 导出
        try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "集采询比价评选列表信息.xlsx")) {
            EasyExcel.write(outputStream)
                    .sheet(0)
                    .head(headList)
                    .doWrite(dataList);
        }
    }

    //报价结果导出
    @GetMapping("/qouteDownloadExcel/{projectId}")
    public void qouteDownloadExcel(@PathVariable("projectId") Long projectId, HttpServletResponse response) throws IOException {
        ApiSouSelectQueryDTO params = new ApiSouSelectQueryDTO();
        params.setProjectId(projectId);
        // 1: 查询数据
        List<ApiSouSelectResultVO> list = compSouSelectQueryService.listOrderResult(params);
        // 2: 设置导出excel头信息
        List<List<String>> headList = new ArrayList<>(30);
        headList.add(new ArrayList<>(Collections.singletonList("序号")));
        headList.add(new ArrayList<>(Collections.singletonList("物资名称")));
        headList.add(new ArrayList<>(Collections.singletonList("所属单位")));
        headList.add(new ArrayList<>(Collections.singletonList("月约产量")));
        List<String> tempL = Arrays.asList("最高价", "TODO");
        List<String> v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
        v.set(1, "名称");
        headList.add(v);
        v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
        v.set(1, "单价（元）");
        headList.add(v);
        List<String> tempL2 = Arrays.asList("次高价", "TODO");
        List<String> v2 = JSON.parseArray(JSON.toJSONString(tempL2), String.class);
        v2.set(1, "名称");
        headList.add(v2);
        v2 = JSON.parseArray(JSON.toJSONString(tempL2), String.class);
        v2.set(1, "单价（元）");
        headList.add(v2);
        List<String> tempL3 = Arrays.asList("第三高", "TODO");
        List<String> v3 = JSON.parseArray(JSON.toJSONString(tempL3), String.class);
        v3.set(1, "名称");
        headList.add(v3);
        v3 = JSON.parseArray(JSON.toJSONString(tempL3), String.class);
        v3.set(1, "单价（元）");
        headList.add(v3);
        List<String> tempL4 = Arrays.asList("上期中标供应商", "TODO");
        List<String> v4 = JSON.parseArray(JSON.toJSONString(tempL4), String.class);
        v4.set(1, "名称");
        headList.add(v4);
        v4 = JSON.parseArray(JSON.toJSONString(tempL4), String.class);
        v4.set(1, "单价（元）");
        headList.add(v4);

        // 3: 设置行数据
        List<List<Object>> dataList = new ArrayList<>(list.size());
        // 查询数据
        int index = 0;
        for (ApiSouSelectResultVO itemInfo : list) {
            List<Object> row = new ArrayList<>(30);
            dataList.add(row);
            index++;
            row.add(index);
            row.add(itemInfo.getItemDesc());
            row.add(itemInfo.getAffiliatedUnit());
            row.add(itemInfo.getMonthlyProduction());
            row.add(itemInfo.getMaxVendorName());
            row.add(itemInfo.getMaxPrice());
            row.add(itemInfo.getSecondVendorName());
            row.add(itemInfo.getSecondPrice());
            row.add(itemInfo.getThirdVendorName());
            row.add(itemInfo.getThirdPrice());
            row.add(itemInfo.getPeriodVendorName());
            row.add(itemInfo.getPeriodPrice());
        }
        // 4: 导出
        try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "报价结果")) {
            EasyExcel.write(outputStream)
                    .sheet(0)
                    .head(headList)
                    .doWrite(dataList);
        }
    }
    //历史报价导出
    @GetMapping("/hisDownloadExcel/{projectId}")
    public void hisDownloadExcel(@PathVariable("projectId") Long projectId, HttpServletResponse response) throws IOException {
        ApiSouSelectQueryDTO params = new ApiSouSelectQueryDTO();
        params.setProjectId(projectId);
        // 1: 查询数据
        List<ApiSouSelectItemQueryVO> list = compSouSelectQueryService.listItemEvaluations(params);
        // 2: 设置导出excel头信息
        List<List<String>> headList = new ArrayList<>(30);
        headList.add(new ArrayList<>(Collections.singletonList("序号")));
        headList.add(new ArrayList<>(Collections.singletonList("物资名称")));
        headList.add(new ArrayList<>(Collections.singletonList("供应商编码")));
        headList.add(new ArrayList<>(Collections.singletonList("供应商名称")));
        headList.add(new ArrayList<>(Collections.singletonList("报价IP")));
        headList.add(new ArrayList<>(Collections.singletonList("报价时间")));
        headList.add(new ArrayList<>(Collections.singletonList("报价单价")));
        // 3: 设置行数据
        List<List<Object>> dataList = new ArrayList<>(list.size());
        // 查询数据
        int index = 0;
        for (ApiSouSelectItemQueryVO itemInfo : list) {
            List<Object> row = new ArrayList<>(30);
            dataList.add(row);
            index++;
            row.add(index);
            row.add(itemInfo.getItemDesc());
            row.add(itemInfo.getVendorCode());
            row.add(itemInfo.getVendorName());
            row.add(itemInfo.getSubmitByIp());
            row.add(itemInfo.getCreationDate());
            row.add(itemInfo.getOrderNowPrice());
        }
        // 4: 导出
        try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "历史报价结果")) {
            EasyExcel.write(outputStream)
                    .sheet(0)
                    .head(headList)
                    .doWrite(dataList);
        }
    }
}