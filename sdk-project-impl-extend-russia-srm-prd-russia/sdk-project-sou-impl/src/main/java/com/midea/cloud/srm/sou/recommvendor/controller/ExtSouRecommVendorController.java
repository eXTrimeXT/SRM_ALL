package com.midea.cloud.srm.sou.recommvendor.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.recommvendor.service.ExtSouRecommVendorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 推荐供应商- 接口层
 *
 * @author
 * @since 2022/12/21
 */
@RestController
@RequestMapping("/ext/buyer/recommvendor")
@Api(tags = "推荐供应商")
@Slf4j
public class ExtSouRecommVendorController extends BaseController {

    @Autowired
    private ExtSouRecommVendorService souRecommVendorService;

    @GetMapping("/getRecommVendorInfoByNo")
    @ApiOperation(value = "推荐供应商信息查询", notes = "推荐供应商信息")
    public ApiExtSouRecommVendorInfoDTO getRecommVendorInfoByNo(@RequestParam(value = "recommVendorNo") String recommVendorNo) {
        try {
            return souRecommVendorService.getRecommVendorInfoByNo(recommVendorNo);
        } catch (Exception e) {
            log.error("bid getRecommVendorInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getRecommVendorInfo")
    @ApiOperation(value = "推荐供应商信息查询", notes = "推荐供应商信息")
    public ApiExtSouRecommVendorInfoDTO getRecommVendorInfo(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souRecommVendorService.getRecommVendorInfo(projectId);
        } catch (Exception e) {
            log.error("bid getRecommVendorInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @PostMapping("/editRecommVendorInfo")
    @ApiOperation(value = "保存推荐供应商信息", notes = "保存推荐供应商信息")
    Long editVendorInfo(@RequestBody ApiExtSouRecommVendorInfoDTO param) {
        try {
            return souRecommVendorService.editRecommVendor(param, false, SouTypeEnum.recomm.name());
        } catch (Exception e) {
            log.error("vendor editRecommVendorInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getDemandByAppNo")
    @ApiOperation(value = "获取需求申请单号", notes = "获取需求申请单号")
    Long getDemandByAppNo(@RequestParam(value = "appNo") String appNo) {
        try {
            return souRecommVendorService.getApplicantByAppNo(appNo);
        } catch (Exception e) {
            log.error("recommVendor getDemandByAppNo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getRecommVendorInfoByProjectId")
    @ApiOperation(value = "根据projectId查询推荐供应商信息", notes = "推荐供应商信息")
    public List<ExtSouVendor> getRecommVendorInfoByProjectId(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souRecommVendorService.getRecommVendorInfoByProjectId(projectId);
        } catch (Exception e) {
            log.error("bid getRecommVendorInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

}
