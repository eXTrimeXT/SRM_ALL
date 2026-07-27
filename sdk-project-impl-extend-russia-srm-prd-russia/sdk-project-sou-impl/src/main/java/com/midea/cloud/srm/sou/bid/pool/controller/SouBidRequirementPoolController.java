package com.midea.cloud.srm.sou.bid.pool.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.approve.dto.SouApproveOperateDto;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveOperate;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.*;
import com.midea.cloud.srm.model.sou.pool.dto.SouBidRequirementPoolDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveOperateService;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitEventWebService;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitQueryWebService;
import com.midea.cloud.srm.sou.bid.pool.service.SouBidRequirementPoolService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitEventService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouOrderFileService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProcessConfigService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.ExtBidSouForVendorEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 招标计划需求池 - 接口层
 *
 * @author
 * @since 2022/12/21
 */
@RestController
@RequestMapping("/bid/requirementpool")
@Api(tags = "招标计划需求池")
@Slf4j
public class SouBidRequirementPoolController extends BaseController {

    @Autowired
    private SouBidRequirementPoolService poolService;


    @ApiOperation("招标需求池校验供应商推荐、标书、寻源需求申请单号-查询")
    @PostMapping("/getRequirementPoolInfo")
    SouBidRequirementPoolDto getRequirementPoolInfo(@RequestBody SouBidRequirementPoolDto param) {
        try {
            return poolService.getRequirementPoolInfo(param);
        } catch (Exception e) {
            log.error("bid getRequirementPoolInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
