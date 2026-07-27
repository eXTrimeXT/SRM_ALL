package com.midea.cloud.srm.sou.sourcing.init.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.*;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorRiskDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorRiskDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtSouInitQueryService {

    /**
     * 主页面查询
     * @param souProjectQuery
     * @param souType
     * @return
     */
    PageInfo listProjects(ApiExtSouProjectQueryDTO souProjectQuery, String souType);

    /**
     * 基本信息
     * @param projectId 参数
     * @param souType 参数
     * @return 返回
     */
    ApiExtSouProjectInfoDTO getProjectInfo(Long projectId, String souType);

    /**
     * 基本信息
     * @param projectId
     * @return
     */
    ApiExtSouProjectInfoDTO getProjectInfo(Long projectId);

    /**
     * 报价信息
     * @param projectId
     * @return
     */
    List<ExtSouItem> getRequireInfo(Long projectId);

    /**
     * 查询评分规则
     * @param projectId
     * @return
     */
    List<ExtScoreRule> getScoreRule(Long projectId);

    /**
     * 查询供应商
     * @param projectId
     * @param souType
     * @return
     */
    List<ExtSouVendor> getInviteSupplier(Long projectId, String souType);

    /**
     * 查询保证金
     * @param projectId
     * @return
     */
    List<ExtSouMargin> getSouMargin(Long projectId);

    /**
     * 查询扣款、退款历史
     * @param projectId
     * @param type
     * @return
     */
    List<ExtSouMarginRecordDto> getSouMarginRecord(Long projectId, String type);

    /**
     * 查询投标控制信息
     * @param projectId
     * @return
     */
    ExtSouProjectControlDto getProjectControl(Long projectId);

    /**
     * 查询投标控制明细
     * @param projectId
     * @return
     */
    List<ApiExtSouOrderDto> getExtSouOrder(Long projectId);

    /**
     * 查询评标小组
     * @param projectId
     * @return
     */
    List<ExtSouGroup> getEvaGroup(Long projectId);

    /**
     * 招标评分分页查询
     * @param query
     * @param souType
     * @return
     */
    PageInfo<ExtSouTechScoreHeadDto> getTechScore(ApiExtSouTechScoreQueryDTO query, String souType);

    /**
     * 查询招标评分明细
     * @param query
     * @return
     */
    List<ApiExtScoreRuleDto> getExtScoreRule(ApiExtSouTechScoreLineQueryDTO query);

    /**
     * 查询招标评分明细
     * @param query
     * @return
     */
    Map<Long, List<ApiExtScoreRuleDto>> getExtScoreRuleBatchAsAllGroup(ApiExtSouTechScoreLineQueryDTO query);

    /**
     * 招标评分-评分操作-详情页面查询
     * @param query
     * @return
     */
    ApiExtSouTechEvaDetailDto getExtScoreDetail(ApiExtSouTechScoreLineQueryDTO query);

    /**
     * 技术评分明细
     * @param query
     * @return
     */
    ApiExtScoreRuleRespDto getExtScore(ApiExtSouTechScoreLineQueryDTO query);

    /**
     * 技术评分历史明细
     * @param query
     * @return
     */
    ApiExtScoreRuleRespDto getExtScoreHistory(ApiExtSouTechScoreLineQueryDTO query);

    /**
     * 查询技术得分平均得分接口
     * 返回值说明：key值为供应商ID，value值为供应商对应的技术分平均得分
     * @param projectId
     * @return
     */
    Map<Long, BigDecimal> caculateAverageScore(Long projectId);

    /**
     * 导出招标评分模板
     * @param query 参数
     * @param response 参数
     * @throws Exception 报错
     */
    void exportScoreExcelTemplate(ApiExtSouTechScoreLineQueryDTO query, HttpServletResponse response) throws Exception;

    /**
     * 一键导出招标评分
     * @param query 参数
     * @param response 参数
     * @throws Exception 报错
     */
    void exportScoreExcelBatch(ApiExtSouTechScoreLineQueryDTO query, HttpServletResponse response) throws Exception;

    /**
     * 归档评分文件数据查询
     * @param query
     * @return
     */
    List<Map<String, Object>> exportScoreExcelForArchivist(ApiExtSouTechScoreLineQueryDTO query);

    /**
     * 备注
     * @param applicantNo 参数
     * @return 返回
     */
    String getApplicantId(String applicantNo);

    /**
     * 获取需求号
     * @param projectId
     * @return
     */
    String getApplicantNo(Long projectId);

    /**
     * 是否部分取消
     * @param projectId
     * @return
     */
    String partCancle(Long projectId);

    /**
     * 评标进度跟踪
     * @param projectId
     * @return
     */
    @ApiOperation("评标进度跟踪")
    List<ExtSouTechScoreHeadDto> getTechScoreHead(Long projectId);

    /**
     * 备注
     * @param projectId 参数
     * @return 返回
     */
    @ApiOperation("商务标管理页面查询详情接口")
    ApiExtSouBusManageQueryRespDto getBusOrderMangeInfo(Long projectId);

    /**
     * 商务标查看报价情况
     * @param projectId
     * @return
     */
    @ApiOperation("商务标查看报价情况")
    List<ExtSouOrderDto> getSouOrder(Long projectId);

    /**
     * 查询商务标开标人员列表
     * @param projectId 参数
     * @return 返回
     */
    @ApiOperation("查询商务标开标人员列表")
    List<ExtNpmSouOpenBidRecordDto> getSouOrderBusOpenUser(Long projectId);

    /**
     * 商务标管理-查询谈判资料
     * @param projectId 参数
     * @return 返回
     */
    @ApiOperation("商务标管理-查询谈判资料")
    List<ExtSouFile> getTalkFile(Long projectId);

    /**
     * 查看比价
     * @param projectId 参数
     * @return 返回
     */
    @ApiOperation("查看比价")
    ApiExtComparePriceRespDto getComparePrice(Long projectId);

    /**
     * 报价详情
     * @param query
     * @param souType
     * @return
     */
    List<ApiExtSouOrderItemDto> getOrderItem(ApiExtSouOrderItemQueryDto query, String souType);

    /**
     * 查询商务标文件
     * @param orderId
     * @return
     */
    List<ExtSouOrderFile> getBusOrderFile(Long orderId);

    /**
     * 查看定标说明
     * @param projectId
     * @return
     */
    List<CaSelectionResultDTO> queryCaResult(Long projectId);

    /**
     * 查看编制定标结果
     * @param projectId
     * @param vendorId
     * @return
     */
    List<ApiExtSouOrderItemDto> getEditSouResult(Long projectId, Long vendorId);

    /**
     * 查看编制定标结果的供应商集合
     * @param projectId
     * @return
     */
    List<ExtSouVendor> getEditSouResultVendor(Long projectId);

    /**
     * 编制定标结果查询
     * @param param
     * @param souType
     * @return
     */
    ApiExtSouOrderEditResultDto getEditSouResultInfo(ApiExtSouOrderQueryResultDto param, String souType);

    /**
     * 获取组织报价初始数据
     * @param projectId
     * @return
     */
    ApiExtRoundDto getStartPrice(Long projectId);

    /**
     * 查看中/落标通知
     * @param projectId
     * @return
     */
    ApiExtSouWinLossNoticeDto getWinLossNotice(Long projectId);

    /**
     * 查询项目归档资料
     * @param projectId
     * @return
     */
    ApiExtSouArchiveFileEditDto getArchiveFile(Long projectId);

    /**
     * 查询技术标管理页面
     * @param projectId 参数
     * @param souType 参数
     * @return 返回
     */
    ApiExtSouTechManageQueryRespDto getTechManagement(Long projectId, String souType);

    /**
     * 查询抽取历史
     * @param projectId
     * @param souType
     * @return
     */
    List<ExtSouExpertRecordDto> getExpertRecord(Long projectId, String souType);

    /**
     * 查看抽取风险
     * @param projectId
     * @param souType
     * @return
     */
    ApiExtSouTechManageQueryRespDto getExtractRisk(Long projectId, String souType);

    /**
     * 商务标管理-投标详情查询条件值域下拉
     * @param projectId
     * @param souType
     * @return
     */
    ApiExtSouOrderBusManagementDto getBusinessManagementOrderInfo(Long projectId, String souType);

    /**
     * 查询项目对应的包名列表
     * @param projectId
     * @param souType
     * @return
     */
    List<String> getProjectPackName(Long projectId, String souType);

    /**
     * 查询调整截止时间列表
     * @param projectId
     * @param souType
     * @return
     */
    List<ExtNpmSouAjustTime> listAjustTime(Long projectId, String souType);

    /**
     * getRecommvendorRiskDto
     * @param projectId
     * @return
     */
    RecommvendorRiskDto getRecommvendorRiskDto(Long projectId);


    String checkSouMarginRecord(List<Long> idList);
}
