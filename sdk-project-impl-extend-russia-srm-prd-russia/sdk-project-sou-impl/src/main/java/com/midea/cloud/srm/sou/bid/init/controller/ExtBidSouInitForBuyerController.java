package com.midea.cloud.srm.sou.bid.init.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.approve.dto.SouApproveOperateDto;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveOperate;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.*;
import com.midea.cloud.srm.model.sou.sourcing.dto.CheckSouMarginRecordDTO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorRiskDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveOperateService;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitEventWebService;
import com.midea.cloud.srm.sou.bid.init.service.ExtBidSouInitQueryWebService;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouOrderFileService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProcessConfigService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.ExtBidSouForVendorEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 招投标.询价信息 - 接口层
 *
 * @author
 * @since 2022/12/21
 */
@RestController
@RequestMapping("/ext/buyer/bid/init")
@Api(tags = "项目式询价立项(采购商端)")
@Slf4j
public class ExtBidSouInitForBuyerController extends BaseController {

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private ExtBidSouInitQueryWebService extBidSouInitQueryWebService;

    @Autowired
    private ExtBidSouInitEventWebService extBidSouInitEventWebService;

    @Autowired
    private ExtSouInitEventService extSouInitEventService;

    @Autowired
    private IExtSouProcessConfigService souProcessConfigService;

    @Autowired
    private IExtSouOrderFileService souOrderFileService;

    @Autowired
    private ExtBidSouForVendorEventService vendorEventService;

    @Autowired
    private ISouApproveOperateService approveOperateService;

    @Autowired
    private IExtSouProjectService iExtSouProjectService;

    /**
     * 附件压缩包下载
     */
    @GetMapping("/testZip")
    void testZip(HttpServletResponse response) {
        try {
            extBidSouInitQueryWebService.testZip(response);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 分页查询
     *
     * @param souProjectQuery
     * @return
     */
    @PostMapping("/page")
    PageInfo<ExtSouProjectDto> pageProjects(@RequestBody ApiExtSouProjectQueryDTO souProjectQuery) {
        try {
            if (StringUtils.isBlank(souProjectQuery.getProjectStatus())) {
                souProjectQuery.setProjectStatusFlag("proStatusBidFlagNoArchiveDone");
            }
            return extSouInitQueryService.listProjects(souProjectQuery, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid pageProjects Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询报价模板
     *
     * @param projectId
     * @return
     */
    @GetMapping("/listPriceTemplate")
    ApiExtSouPriceTemplateDto listPriceTemplate(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extBidSouInitQueryWebService.listPriceTemplate(projectId);
        } catch (Exception e) {
            log.error("bid listPriceTemplate Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 导出报价模板
     *
     * @param projectId
     * @param response
     */
    @GetMapping("/exportPriceExcelTemplate")
    void exportPriceExcelTemplate(@RequestParam(value = "projectId") Long projectId, HttpServletResponse response) {
        try {
            extBidSouInitQueryWebService.exportPriceExcelTemplate(projectId, response);
        } catch (Exception e) {
            log.error("bid exportPriceExcelTemplate Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 导入报价模板
     *
     * @param file
     */
    @RequestMapping("/importPriceExcel")
    @ApiOperation(value = "导入报价模板", notes = "导入报价模板")
    public Map<String, Object> importPriceExcel(@RequestParam("file") MultipartFile file, Fileupload fileupload) throws Exception {
        try {
            return extBidSouInitEventWebService.importPriceExcel(fileupload.getBusinessId(), file, fileupload, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid importPriceExcel Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getProjectInfo")
    @ApiOperation(value = "立项信息查询", notes = "招标基本信息")
    public ApiExtSouProjectInfoDTO getProjectInfo(@RequestParam(value = "projectId") Long projectId) {
//        try {
            return extSouInitQueryService.getProjectInfo(projectId, SouTypeEnum.bid.name());
//        } catch (Exception e) {
//            log.error("bid getProjectInfo Exception", e);
//            throw new BaseException(e.getMessage());
//        }
    }

    @GetMapping("/getRequireInfo")
    @ApiOperation(value = "报价查询", notes = "招标报价信息")
    public List<ExtSouItem> getRequireInfo(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getRequireInfo(projectId);
        } catch (Exception e) {
            log.error("bid getRequireInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getScoreRule")
    @ApiOperation(value = "评分规则查询", notes = "评分规则查询")
    public List<ExtScoreRule> getScoreRule(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getScoreRule(projectId);
        } catch (Exception e) {
            log.error("bid getScoreRule Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getInviteSupplier")
    @ApiOperation(value = "供应商信息查询", notes = "供应商信息查询")
    public List<ExtSouVendor> getInviteSupplier(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getInviteSupplier(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getInviteSupplier Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getSouMargin")
    @ApiOperation(value = "保证金缴纳信息查询", notes = "保证金缴纳信息查询")
    public List<ExtSouMargin> getSouMargin(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getSouMargin(projectId);
        } catch (Exception e) {
            log.error("bid getSouMargin Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @GetMapping("/getSouMarginRecord")
    @ApiOperation(value = "保证金扣款OR退款历史查询", notes = "保证金扣款OR退款历史查询")
    public List<ExtSouMarginRecordDto> getSouMarginRecord(@RequestParam(value = "projectId") Long projectId, @RequestParam(value = "type") String type) {
        try {
            return extSouInitQueryService.getSouMarginRecord(projectId, type);
        } catch (Exception e) {
            log.error("bid getSouMarginRecord Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @PostMapping("/editProjectInfo")
    @ApiOperation(value = "保存招标基本信息", notes = "保存招标基本信息")
    Long editProjectInfo(@RequestBody ApiExtSouProjectInfoDTO param) {
        try {
            return extSouInitEventService.editProject(param, false, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editProjectInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @PostMapping("/editMarginRecord")
    @ApiOperation(value = "提交保证金扣款、退款", notes = "提交保证金扣款、退款")
    public List<ExtSouMarginRecordDto> editMarginRecord(@RequestBody ApiExtSouMarginRecordDto param) {
        try {
            return extSouInitEventService.editMarginRecord(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editMarginRecord Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @PostMapping("/editPriceTemplate")
    @ApiOperation(value = "确认报价模板字段", notes = "确认报价模板字段")
    public Long editPriceTemplate(@RequestBody ApiExtSouPriceTemplateDto param) {
        try {
            return extSouInitEventService.editPriceTemplate(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editPriceTemplate Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 修改寻源单据状态
     * @param param
     * @return
     */
    @ApiOperation(value = "修改寻源单据状态", notes = "修改寻源单据状态")
    @PostMapping("/modifyProjectStatus")
    Long modifyProjectStatus(@RequestBody ApiExtSouProjectModifyDto param) {
        try {
            return extSouInitEventService.modifyProjectStatus(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid modifyProjectStatus Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 年度保证金查询
     * @param param
     * @return
     */
    @ApiOperation("年度保证金查询")
    @PostMapping("/listYearlyMargin")
    PageInfo<ExtSouMarginDto> listYearlyMargin(@RequestBody ApiExtSouMarginQueryDto param){
        try {
            return extBidSouInitQueryWebService.listYearlyMargin(param);
        } catch (Exception e) {
            log.error("bid listYearlyMargin Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询投标控制信息
     * @param projectId
     * @return
     */
    @ApiOperation("查询投标控制信息")
    @GetMapping("/getProjectControl")
    ExtSouProjectControlDto getProjectControl(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getProjectControl(projectId);
        } catch (Exception e) {
            log.error("bid getProjectControl Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询投标控制明细
     * @param projectId
     * @return
     */
    @ApiOperation("查询投标控制明细")
    @GetMapping("/getExtSouOrder")
    List<ApiExtSouOrderDto> getExtSouOrder(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getExtSouOrder(projectId);
        } catch (Exception e) {
            log.error("bid getExtSouOrder Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询投标控制明细
     * @param extProjectNo
     * @return
     */
    @ApiOperation("查询投标控制明细")
    @GetMapping("/getExtSouOrderInfo")
    List<ApiExtSouOrderDto> getExtSouOrderInfo(@RequestParam(value = "extProjectNo") String extProjectNo) {
        try {
            LambdaQueryWrapper<ExtSouProject> esp = new LambdaQueryWrapper<>();
            esp.eq(ExtSouProject::getExtProjectNo, extProjectNo);
            List<ExtSouProject> project = iExtSouProjectService.list(esp);
            if (CollectionUtils.isNotEmpty(project) && project.get(0).getProjectId() != null) {
                return extSouInitQueryService.getExtSouOrder(project.get(0).getProjectId());
            } else {
                throw new BaseException("找不到招标控制");
            }
        } catch (Exception e) {
            log.error("bid getExtSouOrder Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 废标
     * @param param
     * @return
     */
    @ApiOperation("废标")
    @PostMapping("/rejectOrder")
    Long rejectOrder(@RequestBody ApiExtSouOrderDto param) {
        try {
            return extSouInitEventService.rejectOrder(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid rejectOrder Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 保存不参与原因
     * @param param
     * @return
     */
    @ApiOperation("保存不参与原因")
    @PostMapping("/withdrawOrderBatch")
    Long withdrawOrderBatch(@RequestBody List<ApiExtSouOrderDto> param) {
        try {
            return extSouInitEventService.withdrawOrderBatch(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid withdrawOrderBatch Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("调整投标截止时间")
    @PostMapping("/editProjectEndTime")
    public Long editProjectEndTime(@RequestBody ApiExtSouEndTimeDto param) {
        try {
            return extSouInitEventService.editProjectEndTime(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid modifyProjectStatus Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 删除项目信息
     * @param projectId
     * @return
     */
    @ApiOperation("删除项目信息")
    @GetMapping("/delProject")
    void delProject(@RequestParam(value = "projectId") Long projectId) {
        try {
            extSouInitEventService.delProject(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid delProject Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 移除评标小组
     * @param param
     * @return
     */
    @ApiOperation("移除评标小组")
    @PostMapping("/removeEvaGroup")
    public void removeEvaGroup(@RequestBody ExtSouGroup param) {
        try {
            extSouInitEventService.removeEvaGroup(param);
        } catch (Exception e) {
            log.error("bid removeEvaGroup Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询评标小组
     * @param projectId
     * @return
     */
    @ApiOperation("查询评标小组")
    @GetMapping("/getEvaGroup")
    public List<ExtSouGroup> getEvaGroup(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getEvaGroup(projectId);
        } catch (Exception e) {
            log.error("bid getEvaGroup Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 招标评分分页查询
     * @param query
     * @return
     */
    @ApiOperation("招标评分分页查询")
    @PostMapping("/getTechScore")
    PageInfo<ExtSouTechScoreHeadDto> getTechScore(@RequestBody ApiExtSouTechScoreQueryDTO query) {
        try {
            return extSouInitQueryService.getTechScore(query, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getTechScore Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询招标评分明细
     * @param query
     * @return
     */
    @ApiOperation("查询招标评分明细")
    @PostMapping("/getExtScoreRule")
    List<ApiExtScoreRuleDto> getExtScoreRule(@RequestBody ApiExtSouTechScoreLineQueryDTO query) {
        try {
            return extSouInitQueryService.getExtScoreRule(query);
        } catch (Exception e) {
            log.error("bid getExtScoreRule Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 招标评分-评分操作-详情页面查询
     * @param query
     * @return
     */
    @ApiOperation("招标评分-评分操作-详情页面查询")
    @PostMapping("/getExtScoreDetail")
    ApiExtSouTechEvaDetailDto getExtScoreDetail(@RequestBody ApiExtSouTechScoreLineQueryDTO query) {
        try {
            return extSouInitQueryService.getExtScoreDetail(query);
        } catch (Exception e) {
            log.error("bid getExtScoreDetail Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查看技术评分明细"
     * @param query
     * @return
     */
    @ApiOperation("查看技术评分明细")
    @PostMapping("/getExtScore")
    ApiExtScoreRuleRespDto getExtScore(@RequestBody ApiExtSouTechScoreLineQueryDTO query) {
        try {
            query.setExtendAbandon(YesOrNo.YES.getValue());
            return extSouInitQueryService.getExtScore(query);
        } catch (Exception e) {
            log.error("bid getExtScore Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看技术评分历史明细")
    @PostMapping("/getExtScoreHistory")
    ApiExtScoreRuleRespDto getExtScoreHistory(@RequestBody ApiExtSouTechScoreLineQueryDTO query) {
        try {
            query.setExtendAbandon(YesOrNo.YES.getValue());
            return extSouInitQueryService.getExtScoreHistory(query);
        } catch (Exception e) {
            log.error("bid getExtScoreHistory Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 保存报价信息
     * @param param
     * @return
     */
    @ApiOperation("保存报价信息")
    @PostMapping("/editRequires")
    Long editRequires(@RequestBody ApiExtSouItemDto param) {
        try {
            return extSouInitEventService.editRequires(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editRequires Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 保存供应商信息
     * @param param
     * @return
     */
    @ApiOperation("保存供应商信息")
    @PostMapping("/editInviteSupplier")
    Long editInviteSupplier(@RequestBody ApiExtSouVendorDto param) {
        try {
            return extSouInitEventService.editInviteSupplier(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editInviteSupplier Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 保存评分规则信息
     * @param param
     * @return
     */
    @ApiOperation("保存评分规则信息")
    @PostMapping("/editScoreRule")
    Long editScoreRule(@RequestBody ApiExtSouScoreRuleDto param) {
        try {
            return extSouInitEventService.editScoreRule(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editScoreRule Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 导出评分模板
     *
     * @param query
     * @param response
     */
    @PostMapping("/exportScoreExcelTemplate")
    @ApiOperation("导出评分模板")
    public void exportScoreExcelTemplate(@RequestBody ApiExtSouTechScoreLineQueryDTO query, HttpServletResponse response) {
        try {
            extSouInitQueryService.exportScoreExcelTemplate(query, response);
        } catch (Exception e) {
            log.error("bid exportScoreExcelTemplate Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 一键导出评分
     *
     * @param query
     * @param response
     */
    @PostMapping("/exportScoreExcelBatch")
    @ApiOperation("一键导出评分")
    public void exportScoreExcelBatch(@RequestBody ApiExtSouTechScoreLineQueryDTO query, HttpServletResponse response) {
        try {
            query.setExtendAbandon(YesOrNo.YES.getValue());
            extSouInitQueryService.exportScoreExcelBatch(query, response);
        } catch (Exception e) {
            log.error("bid exportScoreExcelBatch Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 归档评分文件数据查询
     *
     * @param query
     */
    @PostMapping("/exportScoreExcelForArchivist")
    @ApiOperation("归档评分文件数据查询")
    public List<Map<String, Object>> exportScoreExcelForArchivist(@RequestBody ApiExtSouTechScoreLineQueryDTO query) {
        try {
            return extSouInitQueryService.exportScoreExcelForArchivist(query);
        } catch (Exception e) {
            log.error("bid exportScoreExcelForArchivist Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 导入评分模板
     *
     * @param query
     * @param file
     * @param fileupload
     */
    @PostMapping("/importScoreExcel")
    @ApiOperation("导入评分模板")
    public Map<String, Object> importScoreExcel(ApiExtSouTechScoreLineQueryDTO query, MultipartFile file, Fileupload fileupload) {
        try {
            return extSouInitEventService.importScoreExcel(query, file, fileupload, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid importScoreExcel Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 暂存或提交评分
     * @param param
     * @return
     */
    @ApiOperation("暂存或提交评分")
    @PostMapping("/editScore")
    Long editScore(@RequestBody ApiExtSouTechScoreDto param) {
        try {
            return extSouInitEventService.editScore(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editScore Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 生成招标单默认流程
     * @param projectId
     * @return
     */
    @ApiOperation("生成招标单默认流程")
    @GetMapping("/generateDefaultProcessConfig")
    public Long generateDefaultProcessConfig(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souProcessConfigService.generateDefaultProcessConfig(projectId);
        } catch (Exception e) {
            log.error("bid generateDefaultProcessConfig Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 技术评分查询招标单技术文件
     * @param projectId
     * @return
     */
    @ApiOperation("技术评分查询招标单技术文件")
    @GetMapping("/getScoreTechOrderFile")
    List<ExtSouOrderFile> getScoreTechOrderFile(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souOrderFileService.getScoreTechOrderFile(projectId);
        } catch (Exception e) {
            log.error("bid getScoreTechOrderFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 评标进度跟踪
     * @param projectId
     * @return
     */
    @ApiOperation("评标进度跟踪")
    @GetMapping("/getTechScoreHead")
    List<ExtSouTechScoreHeadDto> getTechScoreHead(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getTechScoreHead(projectId);
        } catch (Exception e) {
            log.error("bid getTechScoreHead Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("退回评标")
    @PostMapping("/rejectScoreHead")
    public Long rejectScoreHead(@RequestBody ExtSouTechScoreHeadDto param) {
        try {
            return extSouInitEventService.rejectScoreHead(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid rejectScoreHead Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 技术开标
     * @param projectId
     * @return
     */
    @ApiOperation("技术开标")
    @GetMapping("/openTech")
    Long openTech(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitEventService.openTech(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid openTech Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 开始评标
     * @param projectId
     * @return
     */
    @ApiOperation("开始评标")
    @GetMapping("/evaTech")
    Long evaTech(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitEventService.evaTech(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid evaTech Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 商务开标
     * @param projectId
     * @return
     */
    @ApiOperation("商务开标")
    @GetMapping("/openBus")
    Long openBus(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitEventService.openBus(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid openBus Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 组织商务报价
     * @param param
     * @return
     */
    @ApiOperation("组织商务报价")
    @PostMapping("/startPrice")
    Long startPrice(@RequestBody ApiExtRoundDto param) {
        try {
            return extSouInitEventService.startPrice(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid startPrice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查看技术方案
     * @param projectId
     * @return
     */
    @GetMapping("/getTechPlan")
    @ApiOperation("查看技术方案")
    ApiExtTechFileDto getTechPlan(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souOrderFileService.getTechPlan(projectId);
        } catch (Exception e) {
            log.error("bid getTechPlan Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 下载技术方案
     * @param projectId
     * @return
     */
    @GetMapping("/downloadTechPlan")
    @ApiOperation("查看技术方案")
    void downloadTechPlan(@RequestParam(value = "projectId") Long projectId, HttpServletResponse response) {
        try {
            souOrderFileService.downloadTechPlan(projectId, response);
        } catch (Exception e) {
            log.error("bid downloadTechPlan Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 下载技术附件
     * @param projectId
     * @return
     */
    @GetMapping("/listDownloadTechPlanFile")
    @ApiOperation("下载商务附件")
    Map<String, Object> listDownloadTechPlanFile(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souOrderFileService.listDownloadTechPlanFile(projectId);
        } catch (Exception e) {
            log.error("bid listDownloadTechPlanFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 下载商务附件
     * @param projectId
     * @return
     */
    @GetMapping("/downloadBusinessFile")
    @ApiOperation("下载商务附件")
    void downloadBusinessFile(@RequestParam(value = "projectId") Long projectId, HttpServletResponse response) {
        try {
            response = HttpServletHolder.getResponse();
            souOrderFileService.downloadBusinessFile(projectId, response);
        } catch (Exception e) {
            log.error("bid downloadBusinessFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 下载商务附件
     * @param projectId
     * @return
     */
    @GetMapping("/listDownloadBusinessFile")
    @ApiOperation("下载商务附件")
    Map<String, Object> listDownloadBusinessFile(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souOrderFileService.listDownloadBusinessFile(projectId);
        } catch (Exception e) {
            log.error("bid listDownloadBusinessFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查看上传脱敏文件
     * @param projectId
     * @return
     */
    @GetMapping("/getSecretFileList")
    @ApiOperation("查看上传脱敏文件")
    List<ExtSouOrderFileDto> getSecretFileList(@RequestParam(value = "projectId") Long projectId) {
        try {
            return souOrderFileService.getSecretFileList(projectId);
        } catch (Exception e) {
            log.error("bid getSecretFileList Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("上传脱敏文件-提交")
    @PostMapping("/editSecretFile")
    public Long editSecretFile(@RequestBody ApiExtTechFileDto techFile) {
        try {
            return souOrderFileService.editSecretFile(techFile, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editSecretFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("允许不缴纳保证金")
    @PostMapping("/canNotNeedPayMargin")
    Long canNotNeedPayMargin(@RequestBody ExtSouMargin param) {
        try {
            return extSouInitEventService.canNotNeedPayMargin(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid canNotNeedPayMargin Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 商务部管理-查看投标情况
     * @param projectId
     * @return
     */
    @ApiOperation("商务部管理-查看投标情况")
    @GetMapping("/getSouOrder")
    List<ExtSouOrderDto> getSouOrder(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getSouOrder(projectId);
        } catch (Exception e) {
            log.error("bid getSouOrder Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("商务标管理页面查询详情接口")
    @GetMapping("/getBusOrderMangeInfo")
    ApiExtSouBusManageQueryRespDto getBusOrderMangeInfo(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getBusOrderMangeInfo(projectId);
        } catch (Exception e) {
            log.error("bid getBusOrderMangeInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("商务标管理开标人员查询列表")
    @GetMapping("/getSouOrderBusOpenUser")
    List<ExtNpmSouOpenBidRecordDto> getSouOrderBusOpenUser(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getSouOrderBusOpenUser(projectId);
        } catch (Exception e) {
            log.error("bid getSouOrderBusOpenUser Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("商务标管理-查询谈判资料")
    @GetMapping("/getTalkFile")
    List<ExtSouFile> getTalkFile(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getTalkFile(projectId);
        } catch (Exception e) {
            log.error("bid getTalkFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看比价")
    @GetMapping("/getComparePrice")
    ApiExtComparePriceRespDto getComparePrice(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getComparePrice(projectId);
        } catch (Exception e) {
            log.error("bid getComparePrice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看报价详情")
    @PostMapping("/getOrderItem")
    List<ApiExtSouOrderItemDto> getOrderItem(@RequestBody ApiExtSouOrderItemQueryDto query) {
        try {
            return extSouInitQueryService.getOrderItem(query, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getOrderItem Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 商务标管理-投标详情查询条件值域下拉
     * @param projectId
     * @return
     */
    @ApiOperation("商务标管理-投标详情查询条件值域下拉")
    @GetMapping("/getBusinessManagementOrderInfo")
    ApiExtSouOrderBusManagementDto getBusinessManagementOrderInfo(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.getBusinessManagementOrderInfo(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getBusinessManagementOrderInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看商务附件")
    @GetMapping("/getBusOrderFile")
    List<ExtSouOrderFile> getBusOrderFile(@RequestParam("orderId") Long orderId) {
        try {
            return extSouInitQueryService.getBusOrderFile(orderId);
        } catch (Exception e) {
            log.error("bid getBusOrderFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 发起定标申请
     * @param projectId
     * @return
     */
    @ApiOperation("发起定标申请")
    @GetMapping("/confirmBid")
    ExtSouProjectDto confirmBid(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitEventService.confirmBid(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid confirmBid Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看定标说明")
    @GetMapping("/queryCaResult")
    public List<CaSelectionResultDTO> queryCaResult(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.queryCaResult(projectId);
        } catch (Exception e) {
            log.error("bid queryCaResult Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看编制定标结果")
    @GetMapping("/getEditSouResult")
    public List<ApiExtSouOrderItemDto> getEditSouResult(@RequestParam("projectId") Long projectId, @RequestParam(value = "vendorId", required = false) Long vendorId){
        try {
            return extSouInitQueryService.getEditSouResult(projectId, vendorId);
        } catch (Exception e) {
            log.error("bid getEditSouResult Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("编制定标结果")
    @PostMapping("/editOrderItemResult")
    public Long editOrderItemResult(@RequestBody ApiExtSouOrderItemResultEditParam param){
        try {
            return extSouInitEventService.editOrderItemResult(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editOrderItemResult Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看编制定标结果的供应商集合")
    @GetMapping("/getEditSouResultVendor")
    public List<ExtSouVendor> getEditSouResultVendor(@RequestParam("projectId") Long projectId){
        try {
            return extSouInitQueryService.getEditSouResultVendor(projectId);
        } catch (Exception e) {
            log.error("bid getEdistSouResultVendor Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 获取组织报价初始数据
     * @param projectId
     * @return
     */
    @ApiOperation("获取组织报价初始数据")
    @GetMapping("/getStartPrice")
    ApiExtRoundDto getStartPrice(@RequestParam(value = "projectId") Long projectId) {
        try {
            return extSouInitQueryService.getStartPrice(projectId);
        } catch (Exception e) {
            log.error("bid getStartPrice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 生成中/落标通知书
     * @param projectId
     * @return
     * @throws Exception
     */
    @GetMapping("/generateWinLossBidNotice")
    @ApiOperation("生成中/落标通知书")
    ExtSouProjectDto generateWinLossBidNotice(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitEventService.generateWinLossBidNotice(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid generateWinLossBidNotice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 保存谈判资料
     * @param param
     * @return
     */
    @ApiOperation("保存谈判资料")
    @PostMapping("/editBusTalkFile")
    Long editBusTalkFile(@RequestBody ApiExtSouTalkFileEditDto param) {
        try {
            return extSouInitEventService.editBusTalkFile(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editBusTalkFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查看中/落标通知
     * @param projectId
     * @return
     */
    @GetMapping("/getWinLossNotice")
    @ApiOperation("查看中/落标通知")
    ApiExtSouWinLossNoticeDto getWinLossNotice(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.getWinLossNotice(projectId);
        } catch (Exception e) {
            log.error("bid getWinLossNotice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 进入项目归档
     * @param projectId
     * @return
     */
    @GetMapping("/editWinOrLossNotice")
    @ApiOperation("进入项目归档")
    Long editWinOrLossNotice(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitEventService.editWinOrLossNotice(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editWinOrLossNotice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询项目归档资料
     * @param projectId
     * @return
     */
    @GetMapping("/getArchiveFile")
    @ApiOperation("查询项目归档资料")
    ApiExtSouArchiveFileEditDto getArchiveFile(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.getArchiveFile(projectId);
        } catch (Exception e) {
            log.error("bid getArchiveFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 保存项目归档资料
     * @param param
     * @return
     */
    @PostMapping("/editArchiveFile")
    @ApiOperation("保存项目归档资料")
    Long editArchiveFile(@RequestBody ApiExtSouArchiveFileEditDto param) {
        try {
            return extSouInitEventService.editArchiveFile(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid editArchiveFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 生成招标项目号
     * @param prefix
     * @param code
     * @param dateStr
     * @return
     */
    @GetMapping("/generateProjectNum")
    String generateProjectNum(@RequestParam("prefix") String prefix, @RequestParam("code") String code, @RequestParam("dateStr") String dateStr) {
        try {
            return extSouInitEventService.generateProjectNum(prefix, code, dateStr);
        } catch (Exception e) {
            log.error("bid editArchiveFile Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询技术标管理页面
     * @param projectId
     * @return
     */
    @ApiOperation("查询技术标管理页面")
    @GetMapping("/getTechManagement")
    ApiExtSouTechManageQueryRespDto getTechManagement(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.getTechManagement(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getTechManagement Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询抽取历史
     * @param projectId
     * @return
     */
    @GetMapping("/getExpertRecord")
    @ApiOperation("查询抽取历史")
    List<ExtSouExpertRecordDto> getExpertRecord(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.getExpertRecord(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getExpertRecord Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查看抽取风险
     * @param projectId
     * @return
     */
    @ApiOperation("查看抽取风险")
    @GetMapping("/getExtractRisk")
    ApiExtSouTechManageQueryRespDto getExtractRisk(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.getExtractRisk(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getExtractRisk Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 随机抽取专家组
     * @param param
     * @return
     */
    @ApiOperation("随机抽取专家组")
    @PostMapping("/randomExtractExpert")
    Long randomExtractExpert(@RequestBody ApiExtSouExpertRandomExtractDto param) {
        try {
            return extSouInitEventService.randomExtractExpert(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid randomExtractExpert Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 确认评标
     * @param projectId
     * @return
     */
    @ApiOperation("确认评标")
    @GetMapping("/confirmTechEva")
    Long confirmTechEva(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitEventService.confirmTechEva(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid confirmTechEva Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 指定专家
     * @param param
     * @return
     */
    @ApiOperation("指定专家")
    @PostMapping("/addTechManagementGroup")
    Long addTechManagementGroup(@RequestBody ApiExtSouGroupEditDto param) {
        try {
            return extSouInitEventService.addTechManagementGroup(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid addTechManagementGroup Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 审批操作
     * @param operate
     * @return
     */
    @ApiOperation("审批操作")
    @PostMapping("/approveOperate")
    public SouApproveOperate approveOperate(@RequestBody SouApproveOperateDto operate) {
        try {
            return approveOperateService.operate(operate.getBusinessId(), SouApprovalStatusEnum.valueOf(operate.getOperate()), operate.getDescrption(), "SOU_PROJECT_APPLY");
        } catch (Exception e) {
            log.error("bid approveOperate Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询项目对应的包名列表
     * @param projectId
     * @return
     */
    @ApiOperation("查询项目对应的包名列表")
    @GetMapping("/getProjectPackName")
    List<String> getProjectPackName(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.getProjectPackName(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getProjectPackName Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("编制中标结果-查询")
    @PostMapping("/getEditSouResultInfo")
    ApiExtSouOrderEditResultDto getEditSouResultInfo(@RequestBody ApiExtSouOrderQueryResultDto param) {
        try {
            return extSouInitQueryService.getEditSouResultInfo(param, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("bid getEditSouResultInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 查询调整截止时间列表
     * @param projectId
     * @return
     */
    @GetMapping("/listAjustTime")
    List<ExtNpmSouAjustTime> listAjustTime(@RequestParam("projectId") Long projectId) {
        try {
            return extSouInitQueryService.listAjustTime(projectId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("listAjustTime Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 更新汇总上报时间
     * @param caId
     * @return
     */
    @GetMapping("/saveCaSumbimteReport")
    Long saveCaSumbimteReport(@RequestParam("caId") Long caId) {
        try {
            return extSouInitEventService.saveCaSumbimteReport(caId, SouTypeEnum.bid.name());
        } catch (Exception e) {
            log.error("saveCaSumbimteReport Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 取消招标单任务、待办
     * @param projectIdList
     */
    @PostMapping("/cancleBid")
    void cancleBid(@RequestBody List<Long> projectIdList) {
        try {
            extSouInitEventService.cancleBid(projectIdList);
        } catch (Exception e) {
            log.error("cancleBid Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("查看投标控制供应商风险")
    @GetMapping("/getRisk")
    public RecommvendorRiskDto getRecommvendorRiskDto(@RequestParam("projectId") Long projectId){
        try {
            return extSouInitQueryService.getRecommvendorRiskDto(projectId);
        } catch (Exception e) {
            log.error("bid getRecommvendorRiskDto Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @PostMapping("/checkSouMarginRecord")
    @ApiOperation(value = "保证金扣款OR退款历史查询", notes = "保证金扣款OR退款历史查询")
    public CheckSouMarginRecordDTO checkSouMarginRecord(@RequestBody List<Long> idList) {
        try {
            String msg = extSouInitQueryService.checkSouMarginRecord(idList);
            CheckSouMarginRecordDTO result = new CheckSouMarginRecordDTO();
            if(msg.length() > 0) {
                result.setResult(false);
                result.setMessage(msg);
            } else {
                result.setResult(true);
                result.setMessage("验证成功");
            }
            return result;
        } catch (Exception e) {
            log.error("bid checkSouMarginRecord Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
