package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalFile;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select.MqlSouCreatePricingItemDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * MQL - 寻源转价格审批单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouCreatePricingDTO extends BaseObjectX {

    /** 定价头 */
    private ApprovalHeader approvalHeader;
    /** 中标行 */
    private List<MqlSouCreatePricingItemDTO> approvalBiddingItemList;
    /** 定价附件 */
    private List<ApprovalFile> approvalFileList;

    /**
     * 数据转化
     * @param souProject 寻源单
     * @param souItemMap 物料需求信息
     * @param vendorMap 邀请供应商信息
     * @param currencyMap 可用外币信息
     * @param souOrderItemList 供应商报价行信息
     * @param orderFileList 供应商报价附件
     */
    public static MqlSouCreatePricingDTO convertMqlVO(SouProject souProject,
                                                      Map<Long/* souItemId */, SouItem> souItemMap,
                                                      Map<Long/* vendorId */, SouVendor> vendorMap,
                                                      Map<String/* currencyCode */, SouCurrency> currencyMap,
                                                      List<SouOrderItem> souOrderItemList,
                                                      List<SouOrderFile> orderFileList) {
        MqlSouCreatePricingDTO pricingDTO = new MqlSouCreatePricingDTO();

        pricingDTO.setApprovalHeader(convertHeader(souProject, souOrderItemList));
        pricingDTO.setApprovalBiddingItemList(convertItems(pricingDTO.getApprovalHeader(), souProject, souItemMap, vendorMap, currencyMap, souOrderItemList));
        pricingDTO.setApprovalFileList(convertFiles(orderFileList, vendorMap));

        return pricingDTO;
    }

    private static ApprovalHeader convertHeader(SouProject souProject, List<SouOrderItem> souOrderItemList) {
        ApprovalHeader approval = new ApprovalHeader();
        /* ID(略 - 后端处理) */
        /* 价格审批单号(略 - 后端处理) */
        /* 价格审批标题 */
        approval.setApprovalTitle(souProject.getSouName());
        /* 寻源单ID */
        approval.setSourceId(souProject.getProjectId());
        /* 寻源单号 */
        approval.setSourceNo(souProject.getSouNo());
        /* 寻源方式(非寻源核心处理 - 略) */
        /* 审批状态 */
        approval.setStatus("DRAFT");
        /* 决标方式(字典值有所差别，需要转换) */
        switch (souProject.getOrderWay()) {
//            单项决标
            case SINGLE:
                approval.setAwareWay("QUOTE_BY_LINE");
                break;
//            组合决标
            case COMBINED:
                approval.setAwareWay("QUOTE_BY_SUPPLIER");
                break;
            default:
                break;
        }
        /* 报价精确度 */
        approval.setPriceNum(souProject.getPricePrecision());
        /* 本位币 */
        approval.setStandardCurrency(souProject.getStandardCurrency());
        /* 汇率类型(非寻源核心处理 - 略) */
        /* 汇率转换日期(非寻源核心处理 - 略) */
        /* 是否更新价格库 */
        approval.setIfUpdatePriceLibrary(souProject.getIsSyncToPriceLibrary() != null ?
                souProject.getIsSyncToPriceLibrary().name() : Enable.N.name());
        /* 需求概述 */
        approval.setDemandSummary(souProject.getRemark());
        /* 说明 */
        approval.setDescription(souProject.getRemark());
        /* 流程ID(非寻源核心处理 - 略) */
        /* 合同转换状态(非寻源核心处理 - 略) */
        /* 配额分配类型(非寻源核心处理 - 略) */
        /* 中标总金额 */
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SouOrderItem orderItem : souOrderItemList) {
            if (orderItem.getWinAmount() != null && orderItem.getOrderNotaxPrice() != null && souProject.getPricePrecision() != null) {
                totalPrice = totalPrice.add(orderItem.getWinAmount().multiply(orderItem.getOrderNotaxPrice())
                        .setScale(souProject.getPricePrecision(), RoundingMode.HALF_UP));
            }
        }
        approval.setBidAmount(totalPrice);
        /* 报表转换状态(非寻源核心处理 - 略) */
        /* 起草人意见(非寻源核心处理 - 略) */

        return approval;
    }

    private static List<MqlSouCreatePricingItemDTO> convertItems(ApprovalHeader approvalHeader,
                                                                 SouProject souProject,
                                                                 Map<Long/* souItemId */, SouItem> souItemMap,
                                                                 Map<Long/* vendorId */, SouVendor> vendorMap,
                                                                 Map<String/* currencyCode */, SouCurrency> currencyMap,
                                                                 List<SouOrderItem> souOrderItemList) {
        List<MqlSouCreatePricingItemDTO> approvalItemList = new ArrayList<>(souOrderItemList.size());

        for (SouOrderItem souOrderItem : souOrderItemList) {
            MqlSouCreatePricingItemDTO approvalItem = new MqlSouCreatePricingItemDTO();
            approvalItemList.add(approvalItem);

            SouItem souItem = souItemMap.get(souOrderItem.getSouItemId());
            SouVendor vendor = vendorMap.get(souOrderItem.getVendorId());
            SouCurrency currency = currencyMap.get(souOrderItem.getOrderCurrency());

            /* ID(略 - 后端处理) */
            /* 关联价格审批单ID(略 - 后端处理) */
            /* 价格审批单号(略 - 后端处理) */
            /* 寻源类型(非寻源核心处理 - 略) */
            /* 寻源单ID */
            approvalItem.setSourceId(approvalHeader.getSourceId());
            /* 寻源单号 */
            approvalItem.setSourceNo(approvalHeader.getSourceNo());
            /* 寻源评选行ID */
            approvalItem.setSourceSelectId(null);
            /* 寻源报价行ID */
            approvalItem.setSourceQuoteItemId(souOrderItem.getOrderItemId());
            /* 寻源需求行ID */
            approvalItem.setSourceRequirementItemId(souOrderItem.getSouItemId());
            /* 价格类型(非寻源核心处理 - 略) */
            /* 业务实体 */
            approvalItem.setOrgOuId(souItem.getOrgOuId());
            approvalItem.setOrgOuCode(souItem.getOrgOuCode());
            approvalItem.setOrgOuName(souItem.getOrgOuName());
            /* 库存组织 */
            approvalItem.setOrgInvId(souItem.getOrgInvId());
            approvalItem.setOrgInvCode(souItem.getOrgInvCode());
            approvalItem.setOrgInvName(souItem.getOrgInvName());
            /* 到货地点(非寻源核心处理 - 略) */
            /* 供应商 */
            approvalItem.setVendorId(vendor.getVendorId());
            approvalItem.setVendorCode(vendor.getVendorCode());
            approvalItem.setVendorName(vendor.getVendorName());
            /* 是否无编码物料 */
            approvalItem.setIsNoCodeItem(souItem.getNoCodeItem().name());
            /* 物料 */
            approvalItem.setItemId(souItem.getItemId());
            approvalItem.setItemCode(souItem.getItemCode());
            approvalItem.setItemDesc(souItem.getItemDesc());
            /* 采购分类 */
            approvalItem.setCategoryId(souItem.getCategoryId());
            approvalItem.setCategoryCode(souItem.getCategoryCode());
            approvalItem.setCategoryName(souItem.getCategoryName());
            /* 需求数量 */
            approvalItem.setNeedNum(souItem.getRequireQuantity());
            /* 单位 */
            approvalItem.setUnit(souItem.getUnit());
            /* 原币未税单价 */
            approvalItem.setOriginalNotaxPrice(souOrderItem.getOrderNotaxPrice());
            /* 原币含税单价 */
            approvalItem.setOriginalTaxPrice(souOrderItem.getOrderTaxPrice());
            /* 原币报价精确度 */
            approvalItem.setOriginalPricePrecision(currency.getPricePrecision());
            /* 供应商的报价币种 */
            approvalItem.setOriginalCurrency(souOrderItem.getOrderCurrency());
            /* 本币未税单价 */
            approvalItem.setStandardNotaxPrice(souOrderItem.getStandardNotaxPrice());
            /* 本币含税单价 */
            approvalItem.setStandardTaxPrice(souOrderItem.getStandardTaxPrice());
            /* 本币报价精确度 */
            approvalItem.setStandardPricePrecision(souProject.getPricePrecision());
            /* 本币币种 */
            approvalItem.setStandardCurrency(souProject.getStandardCurrency());
            /* 税率 */
            approvalItem.setTaxKey(souOrderItem.getTaxKey());
            approvalItem.setTaxRate(souOrderItem.getTaxRate());
            /* 是否阶梯报价 */
            approvalItem.setIsLadder(Enable.N.name());
            /* 阶梯价类型 */
            approvalItem.setLadderPriceType(null);
            /* 是否公式报价 */
            approvalItem.setIsFormula(SouOrderTypeEnum.FORMULA.equals(souProject.getOrderType()) ? Enable.Y.name() : Enable.N.name());
            /* 公式ID(非寻源核心处理 - 略) */
            /* 公式名称(非寻源核心处理 - 略) */
            /* 公式值(非寻源核心处理 - 略) */
            /* 供应商填写的公式报价(非寻源核心处理 - 略) */
            /* 是否模型报价 */
            approvalItem.setIsTemplate(Objects.equals(souProject.getOrderType(), SouOrderTypeEnum.TEMPLATE) ? Enable.Y : Enable.N);
            /* 配额分配类型(非寻源核心处理 - 略) */
            /* 配额比例(非寻源核心处理 - 略) */
            /* L/T(非寻源核心处理 - 略) */
            /* 价格生效日期 */
            approvalItem.setStartTime(souOrderItem.getPriceStartTime());
            /* 价格失效日期 */
            approvalItem.setEndTime(souOrderItem.getPriceEndTime());
            /* 采购申请号 */
            approvalItem.setPurchaseRequestNum(souItem.getSourceFromNo());
            /* 采购申请行号 */
            approvalItem.setPurchaseRequestRowNum(souItem.getSourceFromLineNo() != null ? souItem.getSourceFromLineNo().toString() : null);
            /* 来源合同ID */
            approvalItem.setFromContractId(souItem.getSourceFromId());
            /* 来源合同编码 */
            approvalItem.setFromContractCode(souItem.getSourceFromNo());
            /* 来源合同行ID */
            approvalItem.setFromContractLineId(souItem.getSourceFromLineId());
            /* 最小起订量(非寻源核心处理 - 略) */
            /* 中标数量 */
            approvalItem.setQuotaQuantity(souOrderItem.getWinAmount());
            /* 备注 */
            approvalItem.setComments(souOrderItem.getOrderRemark());
            /* 贸易术语(非寻源核心处理 - 略) */
            /* 保质期(非寻源核心处理 - 略) */
            /* 是否代理报价 */
            approvalItem.setIsProxyBidding(souOrderItem.getIsProxy().name());
            /* 处理账期信息(非寻源核心处理 - 略) */
        }
        return approvalItemList;
    }

    /** 转换得到定价附件 */
    private static List<ApprovalFile> convertFiles(List<SouOrderFile> orderFileList,
                                                   Map<Long/* vendorId */, SouVendor> vendorMap) {
        if (orderFileList.isEmpty()) { return Collections.emptyList(); }

        List<ApprovalFile> approvalFileList = new ArrayList<>(orderFileList.size());
        ApprovalFile approvalFile;
        SouVendor vendor;
        for (SouOrderFile orderHeadFile : orderFileList) {
            approvalFile = new ApprovalFile();
            approvalFileList.add(approvalFile);
            vendor = vendorMap.get(orderHeadFile.getVendorId());
            /* ID(略) */
            /* 审批单ID(略) */
            /* 供应商ID */
            approvalFile.setVendorId(vendor.getVendorId());
            /* 供应商编码 */
            approvalFile.setVendorCode(vendor.getVendorCode());
            /* 供应商名称 */
            approvalFile.setVendorName(vendor.getVendorName());
            /* 文件ID */
            approvalFile.setFileRelationId(orderHeadFile.getOrderDocId());
            /* 文件名 */
            approvalFile.setFileName(orderHeadFile.getOrderFileName());
            /* 备注 */
            approvalFile.setRemark(orderHeadFile.getOrderRemark());
        }

        return approvalFileList;
    }

}
