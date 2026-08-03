package com.midea.cloud.srm.biz.pj.bidproject.controller;

import com.midea.cloud.srm.biz.pj.bidproject.service.ISccPjBidProjectService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.biz.pj.bidproject.dto.SupHistoricalCooperationDTO;
import com.midea.cloud.srm.biz.pj.bidproject.dto.SupWinRateDTO;
import com.midea.cloud.srm.model.common.BaseController;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description scc_pj_bid_project
 * @author panmq
 * @date 2023-09-25
 */
@RestController
@Slf4j
@RequestMapping("/bid/project")
public class SccPjBidProjectController extends BaseController {

   @Autowired
    private ISccPjBidProjectService iSccPjBidProjectService;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    /** DataWorks数据服务-供应商360视图-根据供应商ids获取对应参与率、中标率、被邀请率 */
    @Value("${gwm.url.getSupplierWinningRateByVendoridOrCode-url}")
    private String getSupplierWinningRateByVendoridOrCodeUrl;

    /** DataWorks数据服务-供应商360视图-根据供应商ids获取对应参与率、中标率、被邀请率 */
    @Value("${gwm.url.getSupHistoricalCooperationByIdOrCode-url}")
    private String getSupHistoricalCooperationByIdOrCodeUrl;

    @ApiOperation(value = "根据供应商ids获取对应参与率、中标率、被邀请率")
    @PostMapping("/getSupplierWinningRateByVendorIds")
    public List<SupWinRateDTO> getSupplierWinningRateByVendorIds(@RequestBody List<Long> vendorIdList){

        String vendorIds = vendorIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        OpenClient openClient = new OpenClient(appKey,secret);
        String url = String.format(getSupplierWinningRateByVendoridOrCodeUrl,vendorIds);
        String result = openClient.sendHttpGet(url);
        JSONObject jsonObject = JSONObject.parseObject(result, JSONObject.class);
        String re =jsonObject.get("data").toString();

        return JSONArray.parseArray(re, SupWinRateDTO.class);
    }

    @ApiOperation(value = "根据供应商ids获取历史合作信息-项目品类投标中标信息")
    @PostMapping("/getSupHistoricalCooperationByIds")
    public List<SupHistoricalCooperationDTO> getSupHistoricalCooperationByIds(@RequestBody List<Long> vendorIdList){

        String vendorIds = vendorIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        OpenClient openClient = new OpenClient(appKey,secret);
        String url = String.format(getSupHistoricalCooperationByIdOrCodeUrl,vendorIds);
        String result = openClient.sendHttpGet(url);
        JSONObject jsonObject = JSONObject.parseObject(result, JSONObject.class);
        String re =jsonObject.get("data").toString();

        List<SupHistoricalCooperationDTO> supHistoricalCooperationDTOList = JSONArray.parseArray(re, SupHistoricalCooperationDTO.class);
        supHistoricalCooperationDTOList.forEach(supHistoricalCooperationDTO ->
                supHistoricalCooperationDTO.setIsWin("Y".equals(supHistoricalCooperationDTO.getIsWin())? YesOrNo.YES.getName() : YesOrNo.NO.getName()));

        return supHistoricalCooperationDTOList;
    }


}

