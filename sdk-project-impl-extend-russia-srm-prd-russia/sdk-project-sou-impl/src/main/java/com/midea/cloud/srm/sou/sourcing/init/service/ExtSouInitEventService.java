package com.midea.cloud.srm.sou.sourcing.init.service;

import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemResultEditParam;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtSouInitEventService {

    /**
     *
     * 编辑/提交寻源基本信息
     * @param param 寻源基本信息
     * @param isCopy true-单据复制场景
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return 返回
     */
    Long editProject(ApiExtSouProjectInfoDTO param, boolean isCopy, String souType);

    /**
     * 提交-罚款、扣款信息
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    List<ExtSouMarginRecordDto> editMarginRecord(ApiExtSouMarginRecordDto param, String souType);

    /**
     * 确认报价模板字段
     * @param param
     * @param souType
     * @return
     */
    Long editPriceTemplate(ApiExtSouPriceTemplateDto param, String souType);

    /**
     * 修改寻源单据状态
     * @param param
     * @param souType
     * @return
     */
    Long modifyProjectStatus(ApiExtSouProjectModifyDto param, String souType);

    /**
     * 调整投标截止时间
     * @param param
     * @param souType
     * @return
     */
    Long editProjectEndTime(ApiExtSouEndTimeDto param, String souType);

    /**
     * 调整投标截止时间
     * @param projectId
     * @param souType
     * @return
     */
    void delProject(Long projectId, String souType);

    /**
     * 移除评标小组
     * @param param
     */
    void removeEvaGroup(ExtSouGroup param);

    /**
     * 保存报价信息
     * @param param
     * @param souType
     * @return
     */
    Long editRequires(ApiExtSouItemDto param, String souType);

    /**
     * 保存供应商信息
     * @param param
     * @param souType
     * @return
     */
    Long editInviteSupplier(ApiExtSouVendorDto param, String souType);

    /**
     * 保存评分规则
     * @param param
     * @param souType
     * @return
     */
    Long editScoreRule(ApiExtSouScoreRuleDto param, String souType);

    /**
     * 导入评分模板
     * @param query 参数
     * @param file 参数
     * @param fileupload 参数
     * @param souType 参数
     * @return 返回
     * @throws Exception 报错
     */
    public Map<String, Object> importScoreExcel(ApiExtSouTechScoreLineQueryDTO query, MultipartFile file, Fileupload fileupload, String souType) throws Exception;

    /**
     * 暂存或提交评分
     * @param param
     * @param souType
     * @return
     */
    Long editScore(ApiExtSouTechScoreDto param, String souType);

    /**
     * 更新招标状态
     * @param projectId
     * @param souBiddingProStatusEnum
     */
    void updateSouBidStatus(Long projectId, SouBiddingProStatusEnum souBiddingProStatusEnum);

    /**
     * 退回评标
     * @param param
     * @param souType
     * @return
     */
    Long rejectScoreHead(ExtSouTechScoreHeadDto param, String souType);

    /**
     * 技术开标
     * @param projectId
     * @param souType
     * @return
     */
    Long openTech(Long projectId, String souType);

    /**
     * 开始技术评标前校验权限
     * @param projectId
     */
    void checkBeforeEvaTech(Long projectId);

    /**
     * 开始技术评标
     * @param projectId
     * @param souType
     * @return
     */
    Long evaTech(Long projectId, String souType);

    /**
     * 商务开标
     * @param projectId
     * @param souType
     * @return
     */
    Long openBus(Long projectId, String souType);

    /**
     * 组织商务报价
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    Long startPrice(ApiExtRoundDto param, String souType);

    /**
     * 允许不缴纳保证金
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    Long canNotNeedPayMargin(ExtSouMargin param, String souType);

    /**
     * 废标
     * @param param
     * @param souType
     * @return
     */
    Long rejectOrder(ApiExtSouOrderDto param, String souType);

    /**
     * 保存不参与原因
     * @param param
     * @param souType
     * @return
     */
    Long withdrawOrderBatch(List<ApiExtSouOrderDto> param, String souType);

    /**
     * 发起定标申请
     * @param projectId 参数
     * @param souType 参数
     * @return 返回
     * @throws Exception 报错
     */
    ExtSouProjectDto confirmBid(Long projectId, String souType) throws Exception;

    /**
     * 编辑定标结果
     * @param param
     * @param souType
     * @return
     */
    Long editOrderItemResult(ApiExtSouOrderItemResultEditParam param, String souType);

    /**
     * 生成中/落标通知书
     * @param projectId
     * @param souType
     * @return
     * @throws Exception
     */
    ExtSouProjectDto generateWinLossBidNotice(Long projectId, String souType) throws Exception;

    /**
     * 保存谈判资料
     * @param param
     * @param souType
     * @return
     */
    Long editBusTalkFile(ApiExtSouTalkFileEditDto param, String souType);

    /**
     * 进入项目归档
     * @param projectId
     * @param souType
     * @return
     */
    Long editWinOrLossNotice(Long projectId, String souType);

    /**
     * 保存项目归档资料
     * @param param
     * @param souType
     * @return
     */
    Long editArchiveFile(ApiExtSouArchiveFileEditDto param, String souType);

    /**
     * 生成招标项目号
     * @param prefix
     * @param code
     * @param dateStr
     * @return
     */
    String generateProjectNum(String prefix, String code, String dateStr);

    /**
     * 随机抽取专家组
     * @param param
     * @param souType
     * @return
     */
    Long randomExtractExpert(ApiExtSouExpertRandomExtractDto param, String souType);

    /**
     * 确认评标
     * @param projectId
     * @param souType
     * @return
     */
    Long confirmTechEva(Long projectId, String souType);

    /**
     * 指定专家
     * @param param
     * @param souType
     * @return
     */
    Long addTechManagementGroup(ApiExtSouGroupEditDto param, String souType);

    /**
     * 更新汇总上报时间
     * @param caId
     * @param souType
     * @return
     */
    Long saveCaSumbimteReport(Long caId, String souType);

    /**
     * 取消招标单
     * @param projectIdList
     * @return
     */
    void cancleBid(List<Long> projectIdList);
}
