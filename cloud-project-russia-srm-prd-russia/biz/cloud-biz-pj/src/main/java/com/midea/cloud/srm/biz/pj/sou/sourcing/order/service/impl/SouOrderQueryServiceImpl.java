package com.midea.cloud.srm.biz.pj.sou.sourcing.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.service.SouOrderQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.dao.SouSignUpFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.downloadquotetemp.ApiSouOrderDownloadQuoteTempHandler;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrTableColumnVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataVO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemQuoteTempDownloadDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderResultQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouOrderSignUpInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 供应商报价 - 查询服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/20
 */
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
public class SouOrderQueryServiceImpl implements SouOrderQueryService {

    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouOrderMapper souOrderMapper;
    @Autowired
    private SouOrderItemHisDAOImpl souOrderItemLadderDAO;
    @Autowired
    private SouOrderFileDAOImpl souOrderFileDao;
    @Autowired
    private SouItemLadderDAOImpl souItemLadderDao;
    @Autowired
    private SouFileDAOImpl souFileDao;
    @Autowired
    private SouSignUpFileDAOImpl souSignUpFileDao;
    @Autowired
    private SupplierClient supplierClient;
    @Autowired
    private SouInitQueryService souInitQueryService;
    @Autowired
    private SouControlEventService souControlEventService;
    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private ISouQuoteTempService souQuoteTempService;

    /**
     * 查询供应商报价单列表信息
     *
     * @param queryParam 查询条件
     * @param souType    寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouOrderQueryVO> listOrders(ApiSouOrderQueryDTO queryParam, String souType) {
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 2: 查询供应商可报价或可查看的询价单 */
        if (queryParam.getPageNum() != null || queryParam.getPageSize() != null) {
            PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<ApiSouOrderQueryVO> voList = souOrderMapper.listOrders(queryParam, souType);
        /* 2.x: 单据状态刷新 */
        {
            Set<Long> needRefreshProjectIds = voList.stream().filter(p -> {
                switch (p.getProjectStatus()) {
                    case ACCEPT_SIGN_UP: /* 接受报名中 */
                    case SIGN_UP_END: /* 报名截止 */
                    case ORDER_NOT_START: /* 报价未开始 */
                    case ACCEPT_ORDER: /* 接受报价中 */
                        return true;
                    default:
                        return false;
                }
            }).map(ApiSouOrderQueryVO::getProjectId).collect(Collectors.toSet());
            if (!needRefreshProjectIds.isEmpty()) {
                needRefreshProjectIds.forEach(projectId -> souControlEventService.refreshProjectBySouTime(projectId));
                /* 刷新后，回写状态等信息，免得返回给界面旧数据 */
                Map<Long/* projectId */, SouProject> projectMap = souProjectDao.listByIds(needRefreshProjectIds)
                        .stream().collect(Collectors.toMap(SouProject::getProjectId, Function.identity()));
                voList.forEach(vo -> {
                    SouProject pj = projectMap.get(vo.getProjectId());
                    if (pj != null) {
                        SouObjectXUtil.mergeProperties(pj, vo);
                    }
                });
            }
        }
        /* 3: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class).doHandlerAfterListOrders(queryParam, souType, voList);
    }

    /**
     * 查看项目信息
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId  供应商ID
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouInitProjectInfoVO getProjectInfo(long projectId, long vendorId, String souType) {
        /* 0: 刷新数据 */
        souControlEventService.refreshProjectBySouTime(projectId);
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class).judgeGetProjectAuth(projectId, vendorId, souType);
        /* 2: 查询立项数据 */
        ApiSouInitProjectInfoVO vo = souInitQueryService.getProject(projectId, souType);
        vo.doVendorView();
        /* 3: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class).doHandlerAfterGetProjectInfo(projectId, vendorId, souType, vo);
    }

    /**
     * 查看项目需求
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId  供应商ID
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouItemVO> getRequireInfo(long projectId, long vendorId, String souType) {
        /* 0: 刷新数据 */
        souControlEventService.refreshProjectBySouTime(projectId);
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class).judgeGetProjectAuth(projectId, vendorId, souType);
        /* 2: 查询供应商有报价权限的物料 */
        List<SouItem> itemList;
        {
            SouVendor vendor = souVendorDao.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    .eq(SouVendor::getVendorId, vendorId)
                    .one();
            if (vendor == null) {
                itemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                        .getValidItemsInSpecifiedRound(projectId, null);
            } else {
                itemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                        .getAvailableItemsForVendor(projectId, vendor.getJoinRound(), vendorId);
            }
        }
        /* 3: 查询阶梯价信息 */
        List<SouItemLadder> ladderList;
        {
            if (itemList.isEmpty()) {
                ladderList = Collections.emptyList();
            } else {
                ladderList = souItemLadderDao.lambdaQuery()
                        .in(SouItemLadder::getSouItemId, itemList.stream().map(SouItem::getSouItemId).collect(Collectors.toSet()))
                        .orderByAsc(SouItemLadder::getSortIndex)
                        .list();
            }
        }
        /* 4: 组装数据 */
        List<ApiSouItemVO> voList = ApiSouItemVO.convertApiVO(itemList, ladderList);
        /* 5: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                .doHandlerAfterGetRequireInfo(projectId, vendorId, souType, voList);
    }

    /**
     * 查看报名信息
     *
     * @param projectId 寻源单ID{@link SouTypeEnum}
     * @param vendorId  供应商ID
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouOrderSignUpInfoVO getSignUpInfo(long projectId, long vendorId, String souType) {
        /* 0: 刷新数据 */
        souControlEventService.refreshProjectBySouTime(projectId);
        /* 1: 校验操作条件/权限 */
        SouVendor vendor = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class)
                .judgeGetSignUpInfoAuth(projectId, vendorId, souType);
        /* 2: 查询数据 */
        if (vendor == null) {
            Map<Long/* vendorId */, CompanyInfo> companyInfoMap = supplierClient
                    .getComponyByIds(Collections.singletonList(vendorId))
                    .stream().collect(Collectors.toMap(CompanyInfo::getCompanyId, Function.identity()));
            CompanyInfo companyInfo = companyInfoMap.get(vendorId);
            AssertUtils.notNull(companyInfo, LocaleHandler.getLocaleMsg("供应商信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), vendorId);
            vendor = new SouVendor();
            vendor.setVendorId(vendorId);
            vendor.setVendorCode(companyInfo.getCompanyCode());
            vendor.setVendorName(companyInfo.getCompanyName());
            vendor.setSignUpStatus(SouSignUpStatusEnum.NO_SIGN_UP);
        }
        List<SouFile> outerFileList = souFileDao.lambdaQuery()
                .eq(SouFile::getProjectId, projectId)
                .eq(SouFile::getFileType, SouFileTypeEnum.OUTER)
                .list();
        List<SouSignUpFile> signUpFileList = souSignUpFileDao.lambdaQuery()
                .eq(SouSignUpFile::getProjectId, projectId)
                .eq(SouSignUpFile::getVendorId, vendorId)
                .list();
        /* 3: 组装数据 */
        ApiSouOrderSignUpInfoVO vo = ApiSouOrderSignUpInfoVO.convertApiVO(vendor, outerFileList, signUpFileList);
        /* 4: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                .doHandlerAfterGetSignUpInfo(projectId, vendorId, souType, vo);
    }

    /**
     * 供应商报价结果查询
     *
     * @param queryParam 查询条件
     * @param isBuyer    true-采购商/false-供应商
     * @param souType    寻源类型{@link SouTypeEnum}
     */
    @Override
    public List<ApiSouOrderItemVO> listOrderResult(ApiSouOrderResultQueryDTO queryParam, boolean isBuyer, String souType) {
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class)
                .judgeGetOrderInfoAuth(queryParam.getProjectId(), queryParam.getVendorId(), queryParam.getRound(), isBuyer, souType);
        /* 3: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<SouOrderItem> orderItemList = souOrderItemDao.lambdaQuery()
                .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
//        有效的物料报价
                .eq(SouOrderItem::getIsValid, Enable.Y)
//        供应商ID
                .eq(SouOrderItem::getVendorId, queryParam.getVendorId())
//        物料编码
                .like(queryParam.getItemCode() != null, SouOrderItem::getItemCode, queryParam.getItemCode())
//        物料名称
                .like(queryParam.getItemDesc() != null, SouOrderItem::getItemDesc, queryParam.getItemDesc())
//        轮次
                .eq(queryParam.getRound() != null, SouOrderItem::getRound, queryParam.getRound())
                .orderByAsc(SouOrderItem::getRound)
                .list();
        /* 4: 数据转换 */
        List<SouItem> itemList = souItemDao.list(SouItem::getProjectId, queryParam.getProjectId());
        List<SouOrder> orderList;
        {
            if (orderItemList.isEmpty()) {
                orderList = Collections.emptyList();
            } else {
                orderList = souOrderDao.listByIds(orderItemList.stream().map(SouOrderItem::getOrderId).collect(Collectors.toSet()));
            }
        }
        List<SouVendor> vendorList;
        {
            if (orderItemList.isEmpty()) {
                vendorList = Collections.emptyList();
            } else {
                vendorList = souVendorDao.lambdaQuery()
                        .eq(SouVendor::getProjectId, queryParam.getProjectId())
                        .in(SouVendor::getVendorId, orderItemList.stream().map(SouOrderItem::getVendorId).collect(Collectors.toSet()))
                        .list();
            }
        }
        List<ApiSouOrderItemVO> voList = ApiSouOrderItemVO.convertApiVO(orderItemList, itemList, orderList, vendorList);
        /* 4: 行业包额外处理 */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class).doHandlerAfterListOrderResult(queryParam, souType, voList);
    }

    /**
     * 查看供应商报价详情
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId  供应商ID
     * @param round     轮次
     * @param isBuyer   true-采购商/false-供应商
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    public ApiSouOrderDetailVO getSouOrderInfo(long projectId, long vendorId, @Nullable Integer round, boolean isBuyer, String souType) {
        /* 0: 刷新数据 */
        souControlEventService.refreshProjectBySouTime(projectId);
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class).judgeGetOrderInfoAuth(projectId, vendorId, round, isBuyer, souType);
        /* 2: 查询立项信息 */
        ApiSouInitDetailVO souInitInfo = souInitQueryService.getSouInitInfo(projectId, souType);
        souInitInfo.doVendorView(vendorId);
        /* 3: 查询供应商本轮次的可报价物料 */
        List<SouItem> availableItems = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                .getAvailableItemsForVendor(projectId, round, vendorId);
        /* 4: 查询供应商报价信息 */
        /* 4.1: 查询报价单 */
        SouOrder order;
        {
            order = souOrderDao.lambdaQuery()
                    .eq(SouOrder::getProjectId, projectId)
                    .eq(SouOrder::getRound, souInitInfo.getProjectInfo().getCurrentRound())
                    .eq(SouOrder::getVendorId, vendorId)
                    .one();
            if (order == null && souInitInfo.getProjectInfo().getCurrentRound() > 1) {
                order = souOrderDao.lambdaQuery()
                        .eq(SouOrder::getProjectId, projectId)
                        .eq(SouOrder::getRound, souInitInfo.getProjectInfo().getCurrentRound() - 1)
                        .eq(SouOrder::getVendorId, vendorId)
                        .one();
            }
        }
        List<SouOrderItem> orderItemList;
        {
            if (order != null) {
                orderItemList = souOrderItemDao.lambdaQuery()
                        .eq(SouOrderItem::getOrderId, order.getOrderId())
                        .eq(SouOrderItem::getIsValid, Enable.Y)
                        .list();

            } else {
                orderItemList = Collections.emptyList();
            }
        }
        /* 4.2: 查询供应商报价附件 */
        List<SouOrderFile> orderFileList;
        {
            if (order != null) {
                orderFileList = souOrderFileDao.lambdaQuery()
                        .eq(SouOrderFile::getOrderId, order.getOrderId())
                        .list();
            } else {
                orderFileList = Collections.emptyList();
            }
        }
        /* 4.3: 查询供应商阶梯报价 */
        List<SouOrderItemHis> ladderPriceList;
        {
            if (order != null) {
                ladderPriceList = souOrderItemLadderDAO.lambdaQuery()
                        .eq(SouOrderItemHis::getOrderId, order.getOrderId())
                        .list();
            } else {
                ladderPriceList = Collections.emptyList();
            }
        }
        /* 5: 组装数据 */
        ApiSouOrderDetailVO vo = ApiSouOrderDetailVO.convertApiVO(souInitInfo, availableItems, order, orderItemList, orderFileList, ladderPriceList);
        /* 6: 行业包额外处理 */
        vo = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class).doHandlerAfterGetSouOrderInfo(projectId, round, vendorId, souType, vo);
        /* 7: 行业包额外处理(去除干扰参数) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderQueryHandler.class)
                .doHandlerAfterGetSouOrderInfoForClearJammingInfos(projectId, round, vendorId, souType, vo);
    }

    /**
     * 物料需求维度报价模板导出excel文件
     */
    @Override
    public void downloadOrderItemQuoteTempExcel(ApiSouOrderItemQuoteTempDownloadDTO param, boolean isBuyer) {
        param.formatParams();
        /* 0: 刷新数据 */
        souControlEventService.refreshProjectBySouTime(param.getProjectId());
        /* 1: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouOrderJudgeHandler.class).judgeDownloadOrderItemQuoteTempExcel(param, isBuyer);
        /* 2: 导出文件 */
        SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouOrderDownloadQuoteTempHandler.class).download(param, isBuyer);
    }

    /**
     * 查询供应商的料费分离报价数据
     * 但是考虑到那个接口比较通用，难以预防供应商端获取其他供应商的报价信息，因此需要在这里单独做一次前置的校验处理
     *
     * @param tempId           {@link SouQuoteTemp#getTempId}
     * @param businessId       业务ID
     * @param isBuyer          true-采购商端/false-供应商端
     * @param vendorId         供应商ID(isBuyer=false时必填)
     * @param searchLatestData true-如果供应商当前轮次没有报价，则自动寻找供应商上一轮的报价
     * @param souType          寻源类型{@link SouTypeEnum}
     * @return 报价模板数据
     */
    @Override
    public SouQuoteTempDataVO getQuoteTempOrderInfo(long tempId, String businessId, boolean isBuyer, @Nullable Long vendorId, boolean searchLatestData, String souType) {
        /* 1: 校验操作条件/权限 */
        String businessId2 = SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderJudgeHandler.class)
                .judgeGetQuoteTempOrderInfoAuth(tempId, businessId, isBuyer, vendorId, searchLatestData, souType);
        /* 2: 查询料费分离数据 */
        boolean isCurrentData = businessId2.equals(businessId);
        SouQuoteTempDataVO vo = souQuoteTempService.queryTempData(tempId, businessId2, true);
        vo.getPriceData().getData().forEach((attrId, rows) -> rows.forEach(row -> {
            if (!isCurrentData) {
                row.put(SouQuoteTempAttrTableColumnVO.TABLE_ID, null);
                row.put(SouQuoteTempAttrTableColumnVO.BUSINESS_ID, null);
            }
        }));
        return vo;
    }

}
