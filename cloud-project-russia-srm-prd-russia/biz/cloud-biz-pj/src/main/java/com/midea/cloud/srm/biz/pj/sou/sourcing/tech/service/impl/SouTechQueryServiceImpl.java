package com.midea.cloud.srm.biz.pj.sou.sourcing.tech.service.impl;

import com.github.pagehelper.PageHelper;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouFileConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouGroupDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouScoreRuleLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.ApiSouTechJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.ApiSouTechQueryHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreHeadDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreHeadMapper;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreLineDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.service.SouTechQueryService;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressDetailQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech.ApiSouTechProgressReviewQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech.*;
import com.midea.cloud.srm.model.pj.sou.score.enums.SouScoreDimensionCodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleDimensionEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 技术标查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/26
 */
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
public class SouTechQueryServiceImpl implements SouTechQueryService {

    @Autowired
    private SouGroupDAOImpl souGroupDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouTechScoreHeadDAOImpl souTechScoreHeadDao;
    @Autowired
    private SouTechScoreLineDAOImpl souTechScoreLineDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouOrderFileDAOImpl souOrderFileDao;
    @Autowired
    private SouScoreRuleLineDAOImpl souScoreRuleLineDao;
    @Autowired
    private SouTechScoreHeadMapper souTechScoreHeadMapper;
    @Autowired
    private SouFileConfigDAOImpl souFileConfigDao;
    @Autowired
    private SupplierClient supplierClient;

    /**
     * 查询技术评标进度
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouTechProgressQueryVO queryTechProgress(ApiSouTechProgressQueryDTO queryParam, String souType) {
        // 1: 入参格式化
        queryParam.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouTechJudgeHandler.class).judgeQueryTechProgressAuth(queryParam.getProjectId(), souType);
        // 3: 查询数据
        // 3.1: 查询工作小组技术评委
        List<SouGroup> groupList = souGroupDao.lambdaQuery()
                .eq(SouGroup::getProjectId, queryParam.getProjectId())
                .like(SouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH)
                .list();
        // 3.2: 查询首轮已提交的报价
        List<SouOrder> orderList = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, queryParam.getProjectId())
                .eq(SouOrder::getRound, 1)
                .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .eq(queryParam.getVendorId() != null, SouOrder::getVendorId, queryParam.getVendorId())
                .list();
        // 3.3: 查询首轮已投标的供应商信息
        List<SouVendor> vendorList; {
            Set<Long> vendorIds = orderList.stream().map(SouOrder::getVendorId).collect(Collectors.toSet());
            if (vendorIds.isEmpty()) {
                vendorList = Collections.emptyList();
            } else {
                vendorList = souVendorDao.lambdaQuery()
                        .eq(SouVendor::getProjectId, queryParam.getProjectId())
                        .in(SouVendor::getVendorId, vendorIds)
                        .list();
            }
        }
        // 3.4: 查询技术评分信息
        List<SouTechScoreHead> techScoreHeadList = souTechScoreHeadDao.lambdaQuery()
                .eq(SouTechScoreHead::getProjectId, queryParam.getProjectId())
                .list();
        // 4: 数据转化
        ApiSouTechProgressQueryVO vo = ApiSouTechProgressQueryVO.convertApiVO(groupList, vendorList, orderList, techScoreHeadList, queryParam.getScoreStatus());
        // 5: 行业包额外处理(后置)
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouTechQueryHandler.class).doHandlerAfterQueryTechProgress(queryParam, souType, vo);
    }

    /**
     * 查询供应商技术标信息
     * @param orderId 报价单ID{@link SouOrder#getOrderId}
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouTechVendorOrderVO queryVendorTechOrder(long orderId, String souType) {
        // 1: 查询数据
        SouOrder order = souOrderDao.getById(orderId);
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, order.getProjectId())
                .eq(SouVendor::getVendorId, order.getVendorId())
                .one();
        CompanyInfo companyInfo = supplierClient.getCompanyInfo(order.getVendorId());
        List<SouFileConfig> techFileConfigList = souFileConfigDao.lambdaQuery()
                .eq(SouFileConfig::getProjectId, order.getProjectId())
                .eq(SouFileConfig::getFileType, SouFileConfigTypeEnum.TECH_FILE)
                .list();
        List<SouOrderFile> orderFileList = souOrderFileDao.lambdaQuery()
                .eq(SouOrderFile::getOrderId, orderId)
                .eq(SouOrderFile::getFileType, SouFileConfigTypeEnum.TECH_FILE)
                .list();
        // 2: 组装数据
        ApiSouTechVendorOrderVO vo = ApiSouTechVendorOrderVO.convertApiVO(vendor, companyInfo, techFileConfigList, orderFileList);
        // 3: 行业包额外处理(后置)
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouTechQueryHandler.class).doHandlerAfterQueryVendorTechOrder(orderId, souType, vo);
    }

    /**
     * 查询供应商技术评分信息
     * PS: 针对具体某个供应商，各评分人的评分进度
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouTechProgressGroupVO> queryTechProgressInfo(long projectId, long vendorId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouTechJudgeHandler.class).judgeQueryTechProgressAuth(projectId, souType);
        // 2: 查询数据
        // 2.1: 查询工作小组技术成员信息
        List<SouGroup> groupList = souGroupDao.lambdaQuery()
                .eq(SouGroup::getProjectId, projectId)
                .like(SouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH)
                .list();
        // 2.2: 查询技术评分
        List<SouTechScoreHead> techScoreHeadList = souTechScoreHeadDao.lambdaQuery()
                .eq(SouTechScoreHead::getProjectId, projectId)
                .eq(SouTechScoreHead::getVendorId, vendorId)
                .list();
        // 3: 数据组装
        List<ApiSouTechProgressGroupVO> voList = ApiSouTechProgressGroupVO.convertApiVO(groupList, techScoreHeadList);
        // 4: 行业包额外处理(后置)
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouTechQueryHandler.class).doHandlerAfterQueryTechProgressInfo(projectId, vendorId, souType, voList);
    }

    /**
     * 采购商端: 查询评委的技术评分详情
     * PS: 针对具体某个供应商，查询某个评委的评分详情
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouTechProgressGroupDetailVO queryTechProgressInfoDetails(ApiSouTechProgressDetailQueryDTO queryParam, String souType) {
        // 1: 入参格式化
        queryParam.formatParams();
        // 2: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouTechJudgeHandler.class)
                .judgeQueryTechProgressAuth(queryParam.getProjectId(), souType);
        // 3: 查询数据
        // 3.1: 查询供应商信息
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, project.getProjectId())
                .eq(SouVendor::getVendorId, queryParam.getVendorId())
                .one();
        CompanyInfo companyInfo = supplierClient.getCompanyInfo(queryParam.getVendorId());
        // 3.2: 查询供应商技术投标附件
        List<SouFileConfig> techFileConfigList = souFileConfigDao.lambdaQuery()
                .eq(SouFileConfig::getProjectId, queryParam.getProjectId())
                .eq(SouFileConfig::getFileType, SouFileConfigTypeEnum.TECH_FILE)
                .list();
        List<SouOrderFile> orderFileList; {
            SouOrder order = souOrderDao.lambdaQuery()
                    .eq(SouOrder::getProjectId, queryParam.getProjectId())
                    .eq(SouOrder::getVendorId, queryParam.getVendorId())
                    .eq(SouOrder::getRound, 1)
                    .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .one();
            AssertUtils.notNull(order, "该供应商未提交报价，不能评分");
            orderFileList = souOrderFileDao.lambdaQuery()
                    .eq(SouOrderFile::getOrderId, order.getOrderId())
                    .eq(SouOrderFile::getFileType, SouFileConfigTypeEnum.TECH_FILE)
                    .list();
        }
        // 3.3: 查询评分规则信息
        List<SouScoreRuleLine> scoreRuleLineList = souScoreRuleLineDao.lambdaQuery()
                .eq(SouScoreRuleLine::getScoreRuleId, project.getScoreTemplateId())
                .eq(SouScoreRuleLine::getDimension, SouScoreRuleDimensionEnum.TECHNOLOGY)
                .list();
        // 3.4: 查询评委的评分信息
        SouTechScoreHead techScoreHead = souTechScoreHeadDao.lambdaQuery()
                .eq(SouTechScoreHead::getProjectId, queryParam.getProjectId())
                .eq(SouTechScoreHead::getVendorId, queryParam.getVendorId())
                .eq(SouTechScoreHead::getGroupId, queryParam.getGroupId())
                .one();
        List<SouTechScoreLine> techScoreLineList = Collections.emptyList();
        if (techScoreHead != null) {
            techScoreLineList = souTechScoreLineDao.lambdaQuery()
                    .eq(SouTechScoreLine::getTechScoreHeadId, techScoreHead.getTechScoreHeadId())
                    .list();
        }
        // 4: 构造数据
        ApiSouTechProgressGroupDetailVO vo = ApiSouTechProgressGroupDetailVO.convertApiVO(vendor, companyInfo, techFileConfigList,
                orderFileList, scoreRuleLineList, techScoreHead, techScoreLineList);
        // 5: 行业包额外处理(后置)
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouTechQueryHandler.class).doHandlerAfterQueryTechProgressInfoDetails(queryParam, souType, vo);
    }

    /**
     * 工作小组成员: 查询需要技术评分的寻源单信息
     * @param queryParam 查询条件
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouTechProgressReviewVO> queryTechProgressReview(ApiSouTechProgressReviewQueryDTO queryParam, String souType) {
        // 1. 入参格式化
        queryParam.formatParams();
        // 2. 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        // 3. 查询数据
        List<ApiSouTechProgressReviewVO> voList = souTechScoreHeadMapper.queryTechProgressReview(queryParam, souType);
        // 4: 行业包额外处理(后置)
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouTechQueryHandler.class).doHandlerAfterQueryTechProgressReview(queryParam, souType, voList);
    }

    /**
     * 工作小组成员: 查询询价单技术评分详情
     * PS: 具体需要对哪些供应商进行技术评分
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param userId 当前用户ID
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouTechProgressReviewDetailVO queryTechProgressReviewDetail(long projectId, long userId, String souType) {
        // 1: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouTechJudgeHandler.class).judgeQueryTechProgressAuth(projectId, souType);
        // 2: 查询当前评委ID
        SouGroup group; {
            List<SouGroup> groupList = souGroupDao.lambdaQuery()
                    .eq(SouGroup::getProjectId, projectId)
                    .eq(SouGroup::getUserId, userId)
                    .list();
            AssertUtils.notEmpty(groupList, "非工作小组成员，禁止操作");
            Optional<SouGroup> optional = groupList.stream().filter(g -> g.getScoreAuth() != null && g.getScoreAuth().contains(SouScoreDimensionCodeEnum.SOU_TECH.name()))
                    .findFirst();
            AssertUtils.isTrue(optional.isPresent(), "无技术评分权限，禁止操作");
            group = optional.get();
        }
        // 3: 查询本轮已投标的供应商
        List<SouVendor> vendorList; {
            Set<Long> vendorIds = souOrderDao.lambdaQuery()
                    .eq(SouOrder::getProjectId, projectId)
                    .eq(SouOrder::getRound, 1)
                    .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .list()
                    .stream().map(SouOrder::getVendorId).collect(Collectors.toSet());
            if (vendorIds.isEmpty()) {
                vendorList = Collections.emptyList();
            } else {
                vendorList = souVendorDao.lambdaQuery()
                        .eq(SouVendor::getProjectId, projectId)
                        .in(SouVendor::getVendorId, vendorIds)
                        .list();
            }
        }
        // 4: 查询技术评分信息
        List<SouTechScoreHead> techScoreHeadList = souTechScoreHeadDao.lambdaQuery()
                .eq(SouTechScoreHead::getProjectId, projectId)
                .eq(SouTechScoreHead::getGroupId, group.getGroupId())
                .list();
        // 5: 组装数据
        ApiSouTechProgressReviewDetailVO vo = ApiSouTechProgressReviewDetailVO.convertApiVO(project, group, vendorList, techScoreHeadList);
        // 6: 行业包额外处理(后置)
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouTechQueryHandler.class).doHandlerAfterQueryTechProgressReviewDetail(projectId, userId, souType, vo);
    }

}
