package com.midea.cloud.srm.sou.sourcing.vendor.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouMarginDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.*;
import com.midea.cloud.srm.model.sou.sourcing.dto.MarginRecordVo;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.vendor.service.ExtBidSouForVendorEventService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.ExtBidSouForVendorQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Api("寻源核心-供应商控制类")
@RestController
@Slf4j
@RequestMapping("/ext/vendor/bid")
public class ExtBidSouForVendorController extends BaseController {
    LoginAppUser loginAppUser= AppUserUtil.getLoginAppUser();

    @Autowired
    private ExtBidSouForVendorQueryService vendorQueryService;

    @Autowired
    private ExtBidSouForVendorEventService vendorEventService;
    /**
     * 供应商招标项目查询退款
     */
    @ApiOperation("供应商招标项目查询退款")
    @GetMapping("/getSouMarginRecord")
    public List<MarginRecordVo>getSouMarginRecord(@RequestParam("projectId") Long projectId) {
        List<MarginRecordVo>lists=new ArrayList<>();
        if(loginAppUser==null){
            return lists;
        }
        Long companyId=loginAppUser.getCompanyId();
        lists=vendorQueryService.getSouMarginRecord(projectId,companyId);
        return lists;
    }
    /**
     * 供应商分页查询接口
     *
     * @param query
     * @return
     */
    @ApiOperation("寻源核心-招标单供应商分页查询接口")
    @PostMapping("/getPage")
    PageInfo<ExtSouOrderDto> getPage(@RequestBody ApiExtSouProjectQueryDTO query) {
        try {
            return vendorQueryService.getPage(query, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor getPage Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询报价明细
     *
     * @param orderId
     * @return
     */
    @ApiOperation("查询报价明细")
    @GetMapping("/getOrderDetail")
    ApiExtSouOrderDetailDto getOrderDetail(@RequestParam(value = "orderId") Long orderId) {
        try {
            return vendorQueryService.getOrderDetail(orderId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor getOrderDetail Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 商务标投标或投标查询
     *
     * @param orderId
     * @return
     */
    @ApiOperation("查询报价明细")
    @GetMapping("/getTenderDetail")
    ApiExtSouOrderDetailDto getTenderDetail(@RequestParam(value = "orderId") Long orderId) {
        try {
            return vendorQueryService.getTenderDetail(orderId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor getTenderDetail Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查看招标文件
     *
     * @param projectId
     * @return
     */
    @ApiOperation("查看招标文件")
    @GetMapping("/getBidSouFileList")
    ApiExtSouBidFileDto getBidSouFileList(@RequestParam(value = "projectId") Long projectId) {
        try {
            return vendorQueryService.getBidSouFileList(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor getBidSouFileList Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询招标保证金
     *
     * @param projectId
     * @param vendorId
     * @return
     */
    @GetMapping("/getMargin")
    ExtSouMarginDto getMargin(@RequestParam(value = "projectId") Long projectId, @RequestParam(value = "vendorId") Long vendorId) {
        try {
            return vendorQueryService.getMargin(projectId, vendorId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor getMargin Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 缴纳保证金
     *
     * @param param
     * @return
     */
    @ApiOperation("缴纳保证金")
    @PostMapping("/editOrderMargin")
    public Long editOrderMargin(@RequestBody ExtSouMarginDto param) {
        try {
            return vendorEventService.editOrderMargin(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor editOrderMargin Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 确认投标
     *
     * @param param
     * @return
     */
    @PostMapping("/confirmTender")
    public Long confirmTender(@RequestBody ExtSouOrderDto param) {
        try {
            return vendorEventService.confirmTender(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor confirmTender Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 撤回投标
     *
     * @param param
     * @return
     */
    @ApiOperation("撤回投标")
    @PostMapping("/withdrawTender")
    public Long withdrawTender(@RequestBody ExtSouOrderDto param) {
        try {
            return vendorEventService.withdrawTender(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor withdrawTender Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 暂存或提交报价
     *
     * @param param
     * @return
     */
    @ApiOperation("暂存或提交报价投标报价")
    @PostMapping("/editOrderItem")
    public Long editOrderItem(@RequestBody ApiExtSouOrderDetailDto param) {
        try {
            return vendorEventService.editOrderItem(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor editOrderItem Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看招标结果")
    @GetMapping("/getBidNoticeDetail")
    ApiExtSouNoticeDto getBidNoticeDetail(@RequestParam(value = "projectId") Long projectId) {
        try {
            return vendorQueryService.getBidNoticeDetail(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor getBidNoticeDetail Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 推送招标电子签章数据
     * @param param
     * @return
     */
    @ApiOperation("推送招标电子签章数据")
    @PostMapping("/pushSgin")
    String pushSgin(@RequestBody ApiExtSouSignEditDto param) {
        try {
            return vendorQueryService.pushSgin(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid vendor pushSgin Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 更新标书下载时间
     * @param projectId
     * @return
     */
    @GetMapping("/updateBidFileDownloadTime")
    public Long updateBidFileDownloadTime(@RequestParam("projectId") Long projectId){
        try {
            return vendorEventService.updateBidFileDownloadTime(projectId);
        } catch (Exception e) {
            log.error("bid updateBidFileDownloadTime Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
