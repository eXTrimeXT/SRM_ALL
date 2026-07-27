package com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.impl;

import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouFileConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemRecordDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouRoundDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.service.SouOrderQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.ApiSouControlJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.ApiSouControlQueryHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouItemRecordQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouControlVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouItemRecordLatestVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.control.ApiSouItemRecordVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 业务控制 - 查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/27
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouControlQueryServiceImpl implements SouControlQueryService {

    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouOrderQueryService souOrderQueryService;
    @Autowired
    private SouFileConfigDAOImpl souFileConfigDao;
    @Autowired
    private SouOrderFileDAOImpl souOrderFileDao;
    @Autowired
    private SouItemRecordDAOImpl souItemRecordDao;

    /**
     * 获取报价管理信息
     *
     * @param projectId 寻源单ID{@link SouOrder#getOrderId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouControlVO getControlInfo(long projectId, String souType) {
        /* 1: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeGetControlInfo(projectId, souType);
        /* 2: 查询数据 */
        /* 2.1: 查询当前轮次 */
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, projectId)
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        /* 2.2: 查询供应商信息 */
        Set<Long> availableVendorIds = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class).getAuthedVendors(projectId, null);
        List<SouVendor> vendorList;
        {
            if (availableVendorIds.isEmpty()) {
                vendorList = Collections.emptyList();
            } else {
                vendorList = souVendorDao.lambdaQuery()
                        .eq(SouVendor::getProjectId, projectId)
                        .in(SouVendor::getVendorId, availableVendorIds)
                        .list();
            }
        }
        /* 2.3: 查询本轮次供应商的报价单 */
        List<SouOrder> orderList = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getRound, project.getCurrentRound())
                .list();
        /* 2.4: 查询供方必须上传附件 */
        List<SouFileConfig> fileConfigList = souFileConfigDao.lambdaQuery()
                .eq(SouFileConfig::getProjectId, projectId)
                .eq(SouFileConfig::getFileType, SouFileConfigTypeEnum.BUSINESS_FILE)
                .list();
        /* 2.5: 查询供应商报价附件 */
        List<SouOrderFile> orderFileList;
        {
            if (orderList.isEmpty()) {
                orderFileList = Collections.emptyList();
            } else {
                orderFileList = souOrderFileDao.lambdaQuery()
                        .in(SouOrderFile::getOrderId, orderList.stream().map(SouOrder::getOrderId).collect(Collectors.toSet()))
                        .list();
            }
        }
        /* 3: 组装数据 */
        ApiSouControlVO vo = ApiSouControlVO.convertApiVO(project, currentRound, vendorList, orderList, fileConfigList, orderFileList);
        /* 4: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouControlQueryHandler.class).doHandlerAfterGetControlInfo(projectId, souType, vo);
    }

    /**
     * 查询供应商报价详情
     *
     * @param orderId 报价单ID{@link SouOrder#getOrderId}
     * @param souType 寻源类型{@link SouTypeEnum}
     * @param isBuyer true-采购商/false-供应商
     */
    @Override
    public ApiSouOrderDetailVO getVendorOrderInfo(long orderId, boolean isBuyer, String souType) {
        /* 1: 校验操作条件/权限 */
        SouOrder order = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeGetVendorOrderAuth(orderId, souType);
        /* 2: 查询报价单信息 */
        ApiSouOrderDetailVO vo = souOrderQueryService.getSouOrderInfo(order.getProjectId(), order.getVendorId(), order.getRound(), isBuyer, souType);
        /* 3: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouControlQueryHandler.class).doHandlerAfterGetVendorOrderInfo(orderId, souType, vo);
    }

    /**
     * 查询物料需求变更记录
     *
     * @param queryParam 查询条件
     * @param souType    寻源类型{@link SouTypeEnum}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ApiSouItemRecordVO> listSouItemRecords(ApiSouItemRecordQueryDTO queryParam, String souType) {
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeListSouItemRecords(queryParam.getProjectId(), souType);
        /* 3: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<SouItemRecord> recordList = souItemRecordDao.lambdaQuery()
                .eq(SouItemRecord::getProjectId, queryParam.getProjectId())
//        刷新类型
                .eq(queryParam.getRefreshType() != null, SouItemRecord::getRefreshType, queryParam.getRefreshType())
//        刷新状态
                .eq(queryParam.getRefreshStatus() != null, SouItemRecord::getRefreshStatus, queryParam.getRefreshStatus())
//        物料ID
                .eq(queryParam.getItemId() != null, SouItemRecord::getItemId, queryParam.getItemId())
//        物料名称
                .like(queryParam.getItemDesc() != null, SouItemRecord::getItemDesc, queryParam.getItemDesc())
//        批次号倒序排列，最新的在前面
                .orderByDesc(SouItemRecord::getBatchNo, SouItemRecord::getRefreshType)
                .list();
        List<ApiSouItemRecordVO> voList = ApiSouItemRecordVO.convertApiVO(recordList);
        /* 4: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouControlQueryHandler.class).doHandlerAfterListSouItemRecords(queryParam, souType, voList);
    }

    /**
     * 查询最新的物料变更记录
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ApiSouItemRecordLatestVO getLatestItemRecord(long projectId, String souType) {
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeGetLatestItemRecord(projectId, souType);
        /* 2: 查询数据 */
        List<SouItemRecord> recordList = souItemRecordDao.lambdaQuery()
                .eq(SouItemRecord::getProjectId, projectId)
//        批次号倒序排列，最新的在前面
                .orderByDesc(SouItemRecord::getBatchNo, SouItemRecord::getRefreshType)
                .list();
        ApiSouItemRecordLatestVO vo = ApiSouItemRecordLatestVO.convertApiVO(recordList);
        /* 3: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouControlQueryHandler.class).doHandlerAfterGetLatestItemRecord(projectId, souType, vo);
    }

}
