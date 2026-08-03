package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.select;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemHisDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.*;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto.PriceApprovalDTO;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto.PriceApprovalItemDTO;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderItemFollowStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderItemFollowTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supplier.risk.dto.MonitoringDTO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 评选业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouSelectEventHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouCurrencyDAOImpl souCurrencyDao;
    @Autowired
    private SouOrderFileDAOImpl souOrderFileDao;
    @Autowired
    private SouOrderItemFollowDAOImpl souOrderItemFollowDao;
    @Autowired
    private SouOrderItemHisDAOImpl souOrderItemLadderDAO;

    @ApiOperation("智能评选前的额外处理")
    public void doHandlerBeforeIntelligentSelect(ApiSouIntelligentSelectDTO param, String souType) {
    }

    @ApiOperation("智能评选后的额外处理")
    public void doHandlerAfterIntelligentSelect(ApiSouIntelligentSelectDTO param, String souType) {
    }

    @ApiOperation("入围/淘汰后的额外处理")
    public void doHandlerAfterChangeWinStatus(ApiSouChangeWinStatusDTO param, String souType, List<SouOrderItem> orderItemList) {
        this.checkVendorRiskForSelect(orderItemList.get(0).getProjectId(),
                orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()), true);
    }

    /**
     * 供应商风险不会反馈到报价权限上，报价权限属于采购商的强制限制，而供应商风险属于临时性动态限制。
     * 1. 如果是供应商维度限制(不能投标)，不能入围，可以中标。
     * 2. 如果是供应商维度限制(不能中标)，可以入围，不能中标。
     * 3. 如果是供应商+品类维度(不能投标)，不能入围，可以中标。
     * 4. 如果是供应商+品类维度(不能中标)，可以入围，不能中标。
     */
    @ApiModelProperty("供应商风险处理")
    public void checkVendorRiskForSelect(long projectId, Set<Long> orderItemIds, boolean winType/* true-入围/false-中标 */) {
        List<SouOrderItem> orderItemList = SouActiveBeanUtils.getBean(SouOrderItemDAOImpl.class).listByIds(orderItemIds);

        Map<Long/* vendorId */, List<MonitoringDTO>> monitorMap = SouActiveBeanUtils.getBean(SupplierClient.class).listMonitoringByCompanyIds(
                orderItemList.stream().map(SouOrderItem::getVendorId).collect(Collectors.toSet()));
        List<SouVendor> vendorList = souVendorDao.listByIds(monitorMap.keySet());
        for (SouVendor vendor : vendorList) {
            List<MonitoringDTO> monitorList = monitorMap.get(vendor.getVendorId());
            if (CollectionUtils.isNotEmpty(monitorList)) {
                // 判断供应商维度
                List<MonitoringDTO> globals = monitorList.stream()
                        .filter(e -> e.getCategoryId() == null)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(globals)) {
                    globals.forEach(global -> {
                        if (winType) {
                            AssertUtils.isFalse(Enable.Y.equals(global.getNoBid()), "供应商[{0}]被限制禁止投标(供应商风险)，不能入围", vendor.getVendorName());
                        } else {
                            AssertUtils.isFalse(Enable.Y.equals(global.getNoWinBid()), "供应商[{0}]被限制禁止中标(供应商风险)，不能中标", vendor.getVendorName());
                        }
                    });
                }
                // 判断供应商+品类维度
                List<MonitoringDTO> unGlobals = monitorList.stream()
                        .filter(e -> e.getCategoryId() != null)
                        .collect(Collectors.toList());
                orderItemList.stream().filter(e -> e.getVendorId().equals(vendor.getVendorId())).forEach(orderItem -> {
                    if (orderItem.getCategoryId() != null) {
                        unGlobals.forEach(monitor -> {
                            if (orderItem.getCategoryId().equals(monitor.getCategoryId())) {
                                if (winType) {
                                    AssertUtils.isFalse(Enable.Y.equals(monitor.getNoBid()), "供应商[{0}]被限制禁止投标品类[{0}]下的任何物料，不能入围",
                                            vendor.getVendorName(), orderItem.getCategoryName());
                                } else {
                                    AssertUtils.isFalse(Enable.Y.equals(monitor.getNoWinBid()), "供应商[{0}]被限制禁止中标品类[{0}]下的任何物料，不能中标",
                                            vendor.getVendorName(), orderItem.getCategoryName());
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    @ApiOperation("中标/落标后的额外处理")
    public void doHandlerAfterChangeSelectStatus(ApiSouChangeSelectStatusDTO param, String souType, List<SouOrderItem> orderItemList) {
        this.checkVendorRiskForSelect(orderItemList.get(0).getProjectId(),
                orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()), false);
    }

    @ApiOperation("修改中标数量前的额外处理")
    public List<SouOrderItem> doHandlerBeforeChangeWinAmount(List<ApiSouChangeWinAmountDTO> params, String souType, List<SouOrderItem> orderItemList) {
        return orderItemList;
    }

    @ApiOperation("修改中标数量后的额外处理")
    public void doHandlerAfterChangeWinAmount(List<ApiSouChangeWinAmountDTO> params, String souType, List<SouOrderItem> orderItemList) {
    }

    @ApiOperation("公开本轮结果前的额外处理")
    public void doHandlerBeforeOpenResult(long projectId, String souType) {
    }

    @ApiOperation("公开本轮结果后的额外处理")
    public void doHandlerAfterOpenResult(long projectId, String souType) {
    }

    @ApiOperation("生成价格审批单前的数据组装")
    public ApiSouCreatePricingDTO doHandlerBeforeCreatePricing(long projectId, String souType, List<SouOrderItem> souOrderItemList) {
        // 1: 查询数据
        Set<Long> orderIds = souOrderItemList.stream().map(SouOrderItem::getOrderId).collect(Collectors.toSet());
        Set<Long> souItemIds = souOrderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet());

        // 1.1: 查询寻源信息
        SouProject souProject = souProjectDao.getById(projectId);
        // 1.2: 查询物料需求
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDao.listByIds(souItemIds)
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        // 1.3: 查询邀请供应商信息
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDao.list(SouVendor::getProjectId, projectId)
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 1.4: 查询可用外币
        Map<String/* currencyCode */, SouCurrency> currencyMap = souCurrencyDao.list(SouCurrency::getProjectId, projectId)
                .stream().collect(Collectors.toMap(SouCurrency::getCurrencyCode, Function.identity()));
        // 1.5: 查询供应商报价附件
        List<SouOrderFile> orderFileList = souOrderFileDao.lambdaQuery()
                .in(SouOrderFile::getOrderId, orderIds)
                .list();
        // 1.6: 查询阶梯价
        Map<Long/* orderItemId */, List<SouOrderItemHis>> orderItemLadderMap = souOrderItemLadderDAO.lambdaQuery()
                .in(SouOrderItemHis::getOrderItemId, souOrderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .orderByAsc(SouOrderItemHis::getOrder_round)
                .list().stream().collect(Collectors.groupingBy(SouOrderItemHis::getOrderItemId));

        ApiSouCreatePricingDTO dto = ApiSouCreatePricingDTO.convertApiVO(souProject, souItemMap, vendorMap, currencyMap, souOrderItemList, orderFileList);


        return dto;
    }

    @ApiOperation("调用价格中台执行生成价格审批单")
    public ApprovalHeader doHandlerForCreatePricing(long projectId, String souType, ApiSouCreatePricingDTO pricingDTO) {
        throw new IllegalArgumentException("请自定义实现");
    }

    @ApiOperation("生成价格审批单后的额外处理")
    public void doHandlerAfterCreatePricing(long projectId, String souType, List<SouOrderItem> souOrderItemList,
                                            ApiSouCreatePricingDTO pricingDTO, ApprovalHeader approvalHeader) {
    }

    @ApiOperation("生成价格审批单前的数据组装(回迁版价格审批单)")
    public PriceApprovalDTO doHandlerBeforeCreatePricingNew(ApiSouCreatePricingApprovalDTO param, List<SouOrderItem> souOrderItemList) {
        // 1: 查询数据
        Set<Long> orderIds = souOrderItemList.stream().map(SouOrderItem::getOrderId).collect(Collectors.toSet());
        Set<Long> souItemIds = souOrderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet());
        // 1.1: 查询寻源信息
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        // 1.2: 查询物料需求
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDao.listByIds(souItemIds)
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        // 1.3: 查询邀请供应商信息
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDao.list(SouVendor::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 1.4: 查询可用外币
        Map<String/* currencyCode */, SouCurrency> currencyMap = souCurrencyDao.list(SouCurrency::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouCurrency::getCurrencyCode, Function.identity()));
        // 1.5: 查询供应商报价附件
        List<SouOrderFile> orderFileList = souOrderFileDao.lambdaQuery()
                .in(SouOrderFile::getOrderId, orderIds)
                .list();
        // 1.6: 查询阶梯报价
        Map<Long/* orderItemId */, List<SouOrderItemHis>> ladderPriceMap = souOrderItemLadderDAO.lambdaQuery()
                .in(SouOrderItemHis::getOrderItemId, souOrderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .list().stream().collect(Collectors.groupingBy(SouOrderItemHis::getOrderItemId));

        // 2: 组装数据
        PriceApprovalDTO dto = new PriceApprovalDTO();
        // 2.1: 价格审批单
        {
            // 2.1.1: ID(略)
            // 2.1.2: 价格审批单号(略)
            // 2.1.3: 价格审批单名称
            dto.setApprovalName(souProject.getSouName());
            // 2.1.4: 价格审批单状态(略)
            // 2.1.5: 本位币
            dto.setStandardCurrency(souProject.getStandardCurrency());
            // 2.1.6: 本位币价格精度
            dto.setPricePrecision(souProject.getPricePrecision());
            // 2.1.7: 汇率类型(略)
            // 2.1.8: 币种转换日期(略)
            // 2.1.9: 是否未税报价
            dto.setIsPriceNotax(souProject.getIsPriceNotax());
            // 2.1.10: 是否同步至价格库
            dto.setIsSyncToPriceLibrary(souProject.getIsSyncToPriceLibrary());
            // 2.1.11: 需求概述
            dto.setDemandSummary(souProject.getRemark());
            // 2.1.12: 说明(略)
            // 2.1.13: 本币未税中标总金额 TODO
            // 2.1.14: 本币含税中标总金额 TODO
            // 2.1.15: 来源单据类型
            dto.setSourceFromType(souProject.getSouType());
            // 2.1.16: 来源单据ID
            dto.setSourceFromId(souProject.getProjectId().toString());
            // 2.1.17: 来源单据编号
            dto.setSourceFromNo(souProject.getSouNo());
            // 2.1.18: 来源单据名称
            dto.setSourceFromName(souProject.getSouName());
        }
        // 2.2: 价格审批单明细/阶梯价
        {
            List<PriceApprovalItemDTO> itemList = new ArrayList<>(souOrderItemList.size());
            dto.setItemList(itemList);
            for (SouOrderItem orderItem : souOrderItemList) {
                PriceApprovalItemDTO approvalItem = new PriceApprovalItemDTO();
                itemList.add(approvalItem);

                SouItem souItem = souItemMap.get(orderItem.getSouItemId());
                SouVendor vendor = vendorMap.get(orderItem.getVendorId());
                SouCurrency souCurrency = currencyMap.get(orderItem.getOrderCurrency());

                // 2.2.1: ID(略)
                // 2.2.2: 价格审批单ID(略)
                // 2.2.3: 业务实体
                approvalItem.setOrgOuId(souItem.getOrgOuId());
                approvalItem.setOrgOuCode(souItem.getOrgOuCode());
                approvalItem.setOrgOuName(souItem.getOrgOuName());
                // 2.2.4: 库存组织
                approvalItem.setOrgInvId(souItem.getOrgInvId());
                approvalItem.setOrgInvCode(souItem.getOrgInvCode());
                approvalItem.setOrgInvName(souItem.getOrgInvName());
                // 2.2.5: 供应商
                approvalItem.setVendorId(vendor.getVendorId());
                approvalItem.setVendorCode(vendor.getVendorCode());
                approvalItem.setVendorName(vendor.getVendorName());
                // 2.2.6: 是否无编码物料
                approvalItem.setNoCodeItem(souItem.getNoCodeItem());
                // 2.2.7: 是否已将无编码物料转化为实际物料
                approvalItem.setHasRefreshNoCodeItem(Enable.N);
                // 2.2.8: 物料/品类/单位
                approvalItem.setItemId(souItem.getItemId());
                approvalItem.setItemCode(souItem.getItemCode());
                approvalItem.setItemDesc(souItem.getItemDesc());
                approvalItem.setCategoryId(souItem.getCategoryId());
                approvalItem.setCategoryCode(souItem.getCategoryCode());
                approvalItem.setCategoryName(souItem.getCategoryName());
                approvalItem.setUnit(souItem.getUnit());
                // 2.2.9: 是否阶梯报价
                approvalItem.setLadderPrice(souItem.getIsLadder());
                // 2.2.10: 报价币种
                approvalItem.setOrderCurrency(orderItem.getOrderCurrency());
                // 2.2.11: 报价精确度
                approvalItem.setOrderPricePrecision(souCurrency.getPricePrecision());
                // 2.2.12: 税率
                approvalItem.setTaxKey(orderItem.getTaxKey());
                approvalItem.setTaxRate(orderItem.getTaxRate());
                // 2.2.13: 汇率(略)
                // 2.2.14: 原币未税单价
                approvalItem.setOrderNotaxPrice(orderItem.getOrderNotaxPrice());
                // 2.2.15: 原币含税单价
                approvalItem.setOrderTaxPrice(orderItem.getOrderNotaxPrice());
                // 2.2.16: 本币未税单价
                approvalItem.setStandardNotaxPrice(orderItem.getStandardNotaxPrice());
                // 2.2.17: 本币含税单价
                approvalItem.setStandardTaxPrice(orderItem.getStandardTaxPrice());
                // 2.2.18: 价格类型(略)
                // 2.2.19: 到货地点(略)
                // 2.2.20: 供货周期(略)
                // 2.2.21: 价格有效期
                if (orderItem.getPriceStartTime() != null) {
                    approvalItem.setPriceStartTime(orderItem.getPriceStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                if (orderItem.getPriceEndTime() != null) {
                    approvalItem.setPriceEndTime(orderItem.getPriceEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                // 2.2.22: MOQ(略)
                // 2.2.23: 贸易术语(略)
                // 2.2.24: 保质期(略)
                // 2.2.25: 需求数量
                approvalItem.setNeedNum(souItem.getRequireQuantity());
                // 2.2.26: 中标数量
                approvalItem.setWinNum(orderItem.getWinAmount());
                // 2.2.27: 备注
                approvalItem.setRemark(orderItem.getOrderRemark());
                // 2.2.28: 来源单据类型
                approvalItem.setSourceFromType(souProject.getSouType());
                // 2.2.29: 来源单据ID
                approvalItem.setSourceFromId(souProject.getProjectId().toString());
                // 2.2.30: 来源单据编号
                approvalItem.setSourceFromNo(souProject.getSouNo());
                // 2.2.31: 来源单据名称
                approvalItem.setSourceFromName(souProject.getSouName());
                // 2.2.32: 来源单据行ID
                approvalItem.setSourceFromLineId(orderItem.getOrderItemId().toString());

            }
        }
        // 2.3: 价格审批单附件
        {
            List<PriceApprovalFile> approvalFileList = new ArrayList<>(orderFileList.size());
            dto.setFileList(approvalFileList);
            for (SouOrderFile orderFile : orderFileList) {
                PriceApprovalFile approvalFile = new PriceApprovalFile();
                approvalFileList.add(approvalFile);
                // 2.3.1: ID(略)
                // 2.3.2: 价格审批单ID(略)
                // 2.3.3: 供应商
                if (orderFile.getVendorId() != null) {
                    SouVendor vendor = vendorMap.get(orderFile.getVendorId());
                    approvalFile.setVendorId(vendor.getVendorId());
                    approvalFile.setVendorCode(vendor.getVendorCode());
                    approvalFile.setVendorName(vendor.getVendorName());
                }
                // 2.3.4: 文件ID
                approvalFile.setFileDocId(orderFile.getOrderDocId());
                // 2.3.5: 文件名称
                approvalFile.setFileName(orderFile.getOrderFileName());
                // 2.3.6: 备注
                approvalFile.setRemark(orderFile.getOrderRemark());
            }
        }

        return dto;
    }

    @ApiOperation("调用价格中台执行生成价格审批单(回迁版价格审批单)")
    public PriceApprovalDTO doHandlerForCreatePricingNew(ApiSouCreatePricingApprovalDTO param, PriceApprovalDTO pricingDTO) {
        return pricingDTO;
    }

    @ApiOperation("生成价格审批单后的额外处理(回迁版价格审批单)")
    public void doHandlerAfterCreatePricingNew(ApiSouCreatePricingApprovalDTO param, List<SouOrderItem> souOrderItemList, PriceApprovalDTO pricingDTO) {
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.PRICING)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        Map<Long/* orderItemId */, Long/* approvalItemId */> orderItemApprovalMap = pricingDTO.getItemList().stream()
                .collect(Collectors.toMap(e -> Long.valueOf(e.getSourceFromLineId()), PriceApprovalItemDTO::getApprovalItemId));
        List<SouOrderItemFollow> followList = new ArrayList<>(souOrderItemList.size());
        for (SouOrderItem orderItem : souOrderItemList) {
            SouOrderItemFollow follow = new SouOrderItemFollow();
            followList.add(follow);

            // ID
            follow.setOrderItemFollowId(IdGenrator.generate());
            // 寻源单ID
            follow.setProjectId(orderItem.getProjectId());
            // 物料需求ID
            follow.setSouItemId(orderItem.getSouItemId());
            // 报价单ID
            follow.setOrderId(orderItem.getOrderId());
            // 报价明细ID
            follow.setOrderItemId(orderItem.getOrderItemId());
            // 后续单据类型
            follow.setFollowType(SouOrderItemFollowTypeEnum.PRICE_APPROVAL.name());
            // 后续单据状态
            follow.setFollowStatus(SouOrderItemFollowStatusEnum.VALID.name());
            // 后续单据ID
            follow.setFollowId(pricingDTO.getApprovalId().toString());
            // 后续单据编号
            follow.setFollowNo(pricingDTO.getApprovalNo());
            // 后续单据名称
            follow.setFollowName(pricingDTO.getApprovalName());
            // 后续单据明细ID
            follow.setFollowLineId(orderItemApprovalMap.get(orderItem.getOrderItemId()).toString());
        }
        souOrderItemFollowDao.saveBatch(followList);
    }

    @ApiOperation("根据价格审批单的审批情况更新寻源单状态后的额外处理")
    public void doHandlerAfterChangePricingResult(ApiSouSelectChangePricingResultDTO param, String souType) {
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
