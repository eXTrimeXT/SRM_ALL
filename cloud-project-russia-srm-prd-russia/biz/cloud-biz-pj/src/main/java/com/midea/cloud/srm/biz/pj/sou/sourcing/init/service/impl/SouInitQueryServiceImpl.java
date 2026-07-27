package com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouProcessQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.ApiSouInitJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.ApiSouInitQueryHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 流程控制 - 信息查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/19
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouInitQueryServiceImpl implements SouInitQueryService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouCurrencyDAOImpl souCurrencyDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouItemLadderDAOImpl souItemLadderDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouVendorAuthDAOImpl souVendorAuthDao;
    @Autowired
    private SouFileDAOImpl souFileDao;
    @Autowired
    private SouFileConfigDAOImpl souFileConfigDao;
    @Autowired
    private SouGroupDAOImpl souGroupDao;
    @Autowired
    private SouProcessQueryService souProcessQueryService;
    @Autowired
    private SouControlEventService souControlEventService;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;

    /**
     * 查询寻源单集合
     *
     * @param queryParam 查询条件
     * @param souType    寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<SouProject> listProjects(ApiSouProjectQueryDTO queryParam, String souType) {
        // 1: 入参格式化
        queryParam.formatParams();
        // 2: 查询数据
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<SouProject> projectList = souProjectDao.lambdaQuery()
                .eq(SouProject::getSouType, souType)
                // 寻源单号
                .like(queryParam.getSouNo() != null, SouProject::getSouNo, queryParam.getSouNo())
                // 寻源名称
                .like(queryParam.getSouName() != null, SouProject::getSouName, queryParam.getSouName())
                // 寻源状态
                .eq(queryParam.getProjectStatus() != null, SouProject::getProjectStatus, queryParam.getProjectStatus())
                // 评分规则
                .eq(queryParam.getScoreRuleType() != null, SouProject::getScoreRuleType, queryParam.getScoreRuleType())
                // 创建人ID
                .eq(queryParam.getCreatedId() != null, SouProject::getCreatedId, queryParam.getCreatedId())
                // 创建人账号
                .like(queryParam.getCreatedBy() != null, SouProject::getCreatedBy, queryParam.getCreatedBy())
                // 立项审核状态
                .eq(queryParam.getCreateApprovalStatus() != null, SouProject::getCreateApprovalStatus, queryParam.getCreateApprovalStatus())
                // 创建时间范围
                .ge(queryParam.getCreationDateFrom() != null, SouProject::getCreationDate, queryParam.getCreationDateFrom())
                .le(queryParam.getCreationDateTo() != null, SouProject::getCreationDate, queryParam.getCreationDateTo())
                // 发布时间范围
                .ge(queryParam.getPublishTimeFrom() != null, SouProject::getPublishTime, queryParam.getPublishTimeFrom())
                .le(queryParam.getPublishTimeTo() != null, SouProject::getPublishTime, queryParam.getPublishTimeTo())
                // 指定创建人
//                .and(queryParam.getCurrentUserId() != null, wrapper -> wrapper
//                        .eq(SouProject::getCreatedId, queryParam.getCurrentUserId())
//                        .or()
//                        .exists(MessageFormat.format("select b.project_id from scc_sou_group b where project_id = b.project_id AND b.user_id = {0}",
//                                String.valueOf(queryParam.getCurrentUserId()))))
                // 寻源单号降序排列
                //.orderByDesc(SouProject::getSouNo)
                .orderByDesc(SouProject::getCreationDate)
                .list();
        // 2.x: 单据状态刷新
        {
            Set<Long> needRefreshProjectIds = projectList.stream().filter(p -> {
                switch (p.getProjectStatus()) {
                    case ACCEPT_SIGN_UP: // 接受报名中
                    case SIGN_UP_END: // 报名截止
                    case ORDER_NOT_START: // 报价未开始
                    case ACCEPT_ORDER: // 接受报价中
                        return true;
                    default:
                        return false;
                }
            }).map(SouProject::getProjectId).collect(Collectors.toSet());
            if (!needRefreshProjectIds.isEmpty()) {
                needRefreshProjectIds.forEach(projectId -> souControlEventService.refreshProjectBySouTime(projectId));
                // 刷新后，回写状态等信息，免得返回给界面旧数据
                Map<Long/* projectId */, SouProject> projectMap = souProjectDao.listByIds(needRefreshProjectIds)
                        .stream().collect(Collectors.toMap(SouProject::getProjectId, Function.identity()));
                projectList.forEach(project -> {
                    SouProject pj = projectMap.get(project.getProjectId());
                    if (pj != null) {
                        SouObjectXUtil.mergeProperties(pj, project);
                    }
                });
            }
        }
        // 3: 行业包额外处理
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouInitQueryHandler.class).doHandlerAfterPageProjects(queryParam, souType, projectList);
    }

    /**
     * 查询寻源基本信息
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouInitProjectInfoVO getProject(long projectId, String souType) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(projectId);
        // 1: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeGetProjectAuth(projectId, souType);
        CompSouProject compSouProject = compSouProjectDao.getById(projectId);
        // 2: 查询数据
        SouProcessConfig processConfig = project.getProcessConfigId() == null ? null : souProcessConfigDao.getById(project.getProcessConfigId());
        List<ApiSouProcessNodeVO> processNodeList = souProcessQueryService.listProcessNodes(projectId, souType);
        List<SouCurrency> currencyList = souCurrencyDao.lambdaQuery()
                .eq(SouCurrency::getProjectId, projectId)
                .orderByAsc(SouCurrency::getSortIndex)
                .list();
        List<SouFile> souFileList = souFileDao.list(SouFile::getProjectId, projectId);
        List<SouFileConfig> fileConfigList = souFileConfigDao.list(SouFileConfig::getProjectId, projectId);
        List<SouGroup> groupList = souGroupDao.list(SouGroup::getProjectId, projectId);
        // 3: 组装数据
        ApiSouInitProjectInfoVO vo = ApiSouInitProjectInfoVO.convertApiVO(project, processConfig, processNodeList, currencyList, souFileList, fileConfigList, groupList, compSouProject);
        // 4: 行业包额外处理
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouInitQueryHandler.class).doHandlerAfterGetProject(projectId, souType, vo);
    }

    /**
     * 采购商/供应商端: 查询寻源物料需求信息
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouItemVO> listRequires(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeGetProjectAuth(projectId, souType);
        // 2: 查询物料需求信息
        List<SouItem> itemList;
        {
            // 采购商
            if (project.getCurrentRound() == null) {
                itemList = souItemDao.lambdaQuery()
                        .eq(SouItem::getProjectId, projectId)
                        .orderByAsc(SouItem::getSortIndex)
                        .list();
            } else {
                itemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                        .getValidItemsInSpecifiedRound(projectId, null);
            }
        }
        // 3: 查询阶梯价模板信息
        List<SouItemLadder> itemLadderList = Collections.emptyList();
        if (!itemList.isEmpty()) {
            itemLadderList = souItemLadderDao.lambdaQuery()
                    .in(SouItemLadder::getSouItemId, itemList.stream().map(SouItem::getSouItemId).collect(Collectors.toList()))
                    .orderByAsc(SouItemLadder::getSortIndex)
                    .list();
        }
        // 4: 组装数据
        List<ApiSouItemVO> voList = ApiSouItemVO.convertApiVO(itemList, itemLadderList);
        // 5: 行业包额外处理
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouInitQueryHandler.class).doHandlerAfterListRequires(projectId, souType, voList);
    }

    /**
     * 采购商端: 查询邀请供应商列表信息
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouVendorVO> listVendors(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeListVendorsAuth(projectId, souType);
        // 2: 查询供应商信息
        List<SouVendor> vendorList = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .orderByAsc(SouVendor::getSortIndex)
                .list();
        // 3: 查询供应商报价权限
        List<SouVendorAuth> authList = Collections.emptyList();
        if (!vendorList.isEmpty()) {
            authList = souVendorAuthDao.lambdaQuery()
                    .eq(SouVendorAuth::getProjectId, projectId)
                    .list();
            //删除多余权限数据
            clearAuth(authList);
        }
        // 4: 组装数据返回
        List<ApiSouVendorVO> voList = ApiSouVendorVO.convertApiVO(vendorList, authList);
        // 5: 行业包额外处理
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouInitQueryHandler.class).doHandlerAfterListVendors(projectId, souType, voList);
    }

    /**
     * 删除多余报价权限
     * @param authList
     */
    private void clearAuth(List<SouVendorAuth> authList) {
        if(CollectionUtils.isEmpty(authList)) {
            return;
        }
        Iterator<SouVendorAuth> authIterable = authList.iterator();

        //唯一业务主键
        Set<String> uniqueKey = new HashSet<>();
        //移除多余数据
        List<Long> removeIdList = new ArrayList<>();

        while (authIterable.hasNext()) {
            SouVendorAuth auth = authIterable.next();
            String key = StringUtils.joinWith("_", auth.getSouVendorId(), auth.getSouItemId(), auth.getProjectId());
            if(!uniqueKey.contains(key)) {
                uniqueKey.add(key);
            } else {
                if(!Objects.isNull(auth.getVendorAuthId())) {
                    removeIdList.add(auth.getVendorAuthId());
                }
                authIterable.remove();
            }
        }
        if(CollectionUtils.isNotEmpty(removeIdList)) {
            souVendorAuthDao.removeBatchByIds(removeIdList);
        }
    }

    /**
     * 采购商/供应商端: 查询指定供应商信息
     *
     * @param projectId {@link SouProject#getProjectId}
     * @param vendorId  供应商ID
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    @Nullable
    public ApiSouVendorVO getVendor(long projectId, long vendorId, String souType) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouInitJudgeHandler.class).judgeListVendorsAuth(projectId, souType);
        // 1: 查询供应商信息
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        if (vendor == null) {
            return null;
        }
        // 2: 查询报价权限信息
        List<SouVendorAuth> authList = souVendorAuthDao.lambdaQuery()
                .eq(SouVendorAuth::getProjectId, projectId)
                .eq(SouVendorAuth::getVendorId, vendorId)
                .list();
        // 3: 组装数据
        ApiSouVendorVO vo = SouObjectXUtil.convertTargetObj(vendor, ApiSouVendorVO.class);
        vo.setAuthList(authList);
        // 4: 行业包额外处理
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouInitQueryHandler.class).doHandlerAfterGetVendor(projectId, vendorId, souType, vo);
    }

    /**
     * 查询立项的所有信息
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouInitDetailVO getSouInitInfo(long projectId, String souType) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(projectId);
        // 1: 查询基本信息
        ApiSouInitProjectInfoVO projectInfo = this.getProject(projectId, souType);
        // 2: 查询物料需求信息
        List<ApiSouItemVO> requireInfo = this.listRequires(projectId, souType);
        // 3: 查询邀请供应商信息
        List<ApiSouVendorVO> vendorInfo = this.listVendors(projectId, souType);
        // 4: 组装数据
        ApiSouInitDetailVO vo = new ApiSouInitDetailVO();
        {
            vo.setProjectInfo(projectInfo);
            vo.setRequireInfo(requireInfo);
            vo.setVendorInfo(vendorInfo);
        }
        // 5: 行业包额外处理
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouInitQueryHandler.class).doHandlerAfterGetSouInitInfo(projectId, souType, vo);
    }

}
