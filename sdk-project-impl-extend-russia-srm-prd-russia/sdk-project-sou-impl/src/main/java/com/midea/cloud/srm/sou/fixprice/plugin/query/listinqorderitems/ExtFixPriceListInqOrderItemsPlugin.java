package com.midea.cloud.srm.sou.fixprice.plugin.query.listinqorderitems;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementOrg;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.vo.ExtFixPriceInqOrderItemsQueryVO;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceHeadMapper;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtFixPriceListInqOrderItemsPlugin {

    @Autowired
    private ExtFixPriceHeadMapper extFixPriceHeadMapper;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @ApiModelProperty("查询数据")
    public void queryData(ExtFixPriceListInqOrderItemsContext context) {
        context.getQueryParam().formatParams();

        // 查询当前操作人所在公司
        Long createUserOrgOuId; {
            // 写入创建人所在公司信息
            HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(AppUserUtil.getLoginAppUser().getCeeaEmpNo());
            if (userOrgnizationDto == null || userOrgnizationDto.getOuOrganization() == null) {
                throw new IllegalArgumentException("查询采购员hr信息失败");
            }
            createUserOrgOuId = userOrgnizationDto.getOuOrganization().getOrganizationId();
        }
        context.getQueryParam().setCreateUserOrgOuId(createUserOrgOuId);

        // 1: 查询数据
        if (context.getQueryParam().getPageNum() != null && context.getQueryParam().getPageSize() != null) {
            PageMethod.startPage(context.getQueryParam().getPageNum(), context.getQueryParam().getPageSize());
        }
        List<ExtFixPriceInqOrderItemsQueryVO> orderItemList = extFixPriceHeadMapper.listSouInqOrderItems(context.getQueryParam());
        listSouInqOrderItemsOrderCount(orderItemList);
        context.setResultList(orderItemList);
    }

    /**
     * 报价次数
     * @param dataList
     */
    private void listSouInqOrderItemsOrderCount(List<ExtFixPriceInqOrderItemsQueryVO> dataList) {
        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }
        Map<String, Object> params = new HashMap<>(15);
        List<Map<String, Object>> souItemIdRoundList = new ArrayList<>();
        dataList.forEach(data -> {
            Map<String, Object> param = new HashMap<>(15);
            param.put("souItemId", data.getSouItemId());
            param.put("round", data.getRound());
            souItemIdRoundList.add(param);
        });
        params.put("souItemIdRoundList", souItemIdRoundList);

        List<ExtFixPriceInqOrderItemsQueryVO> orderCountList = extFixPriceHeadMapper.listSouInqOrderItemsOrderCount(params);
        Map<String, ExtFixPriceInqOrderItemsQueryVO> orderCountMap = orderCountList.stream().collect(Collectors.toMap(
                k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getSouItemId(), k.getRound()), Function.identity(), (k1, k2) -> k2
        ));

        dataList.stream().forEach(data -> {
            String key = StringUtils.joinWith(SrmConstant.UNDER_LINE, data.getSouItemId(), data.getRound());
            ExtFixPriceInqOrderItemsQueryVO vo = orderCountMap.getOrDefault(key, new ExtFixPriceInqOrderItemsQueryVO());
            data.setExtOrderCount(vo.getExtOrderCount());
        });
    }

    @ApiModelProperty("额外的数据填补")
    public void extraData(ExtFixPriceListInqOrderItemsContext context) {
        if (context.getResultList().isEmpty()) { return; }
        // 2: 填补额外信息
        // 2.1: 填补询比价物料需求额外字段
        Set<Long> souItemIds = context.getResultList().stream().map(ExtFixPriceInqOrderItemsQueryVO::getSouItemId).collect(Collectors.toSet());
        Map<Long/* souItemId */, SouItem> souItemMa = souItemDAO.listByIds(souItemIds).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        context.getResultList().forEach(result -> BeanUtils.copyProperties(souItemMa.get(result.getSouItemId()), result));
        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(souItemIds).stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
        context.getResultList().forEach(result -> BeanUtils.copyProperties(inqSouItemMap.get(result.getSouItemId()), result));
        // 2.2: 填补询比价报价明细额外字段
        Set<Long> orderItemIds = context.getResultList().stream().map(ExtFixPriceInqOrderItemsQueryVO::getOrderItemId).collect(Collectors.toSet());
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = inqSouOrderItemDAO.listByIds(orderItemIds).stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
        context.getResultList().forEach(result -> BeanUtils.copyProperties(inqOrderItemMap.get(result.getOrderItemId()), result));
        // 2.3: 填补采购申请明细信息
        Map<Long/* orderItemId */, Set<Long/* requirementLineId */>> orderItemReqLineIdMap = new HashMap<>(context.getResultList().size());
        Set<Long> requirementLineIds = new HashSet<>(context.getResultList().size() << 2);
        context.getResultList().forEach(result -> {
            if (StringUtils.isNotBlank(result.getExtSourceFromLineIds())) {
                Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(result.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                orderItemReqLineIdMap.put(result.getOrderItemId(), ids);
                requirementLineIds.addAll(ids);
            }
        });
        if (!requirementLineIds.isEmpty()) {
            Map<Long/* requirementLineId */, RequirementLine> reqLineMap = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
                            .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds))), RequirementLine.class)
                    .stream().collect(Collectors.toMap(RequirementLine::getRequirementLineId, Function.identity()));
            Map<Long/* requirementHeadId */, RequirementHead> reqHeadMap = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementHead")
                            .in(RequirementHead::getRequirementHeadId, new ArrayList<>(reqLineMap.values().stream().map(RequirementLine::getRequirementHeadId).collect(Collectors.toSet())))), RequirementHead.class)
                    .stream().collect(Collectors.toMap(RequirementHead::getRequirementHeadId, Function.identity()));
            context.getResultList().forEach(result -> {
                Set<Long/* requirementLineId */> reqLineIds = orderItemReqLineIdMap.get(result.getOrderItemId());
                if (CollectionUtils.isNotEmpty(reqLineIds)) {
                    RequirementHead reqHead;
                    RequirementLine minPriceReqLine;
                    List<RequirementLine> reqLineList = new ArrayList<>(reqLineIds.size()); {
                        for (Long reqLineId : reqLineIds) {
                            RequirementLine reqLine = reqLineMap.get(reqLineId);
                            if (reqLine != null) { reqLineList.add(reqLine); }
                        }
                        reqHead = reqLineList.isEmpty() ? null : reqHeadMap.get(reqLineList.get(0).getRequirementHeadId());
                        // 按照最低价供应商进行排序
                        List<RequirementLine> tempReqLineList = reqLineList.stream()
                                .filter(e -> e.getX("extHistoryVendorPrice1") != null)
                                .sorted((a, b) -> {
                                    BigDecimal aVendorMinPrice = new BigDecimal(StringUtils.trimToNull(a.getX("extHistoryVendorPrice1").toString()));
                                    BigDecimal bVendorMinPrice = new BigDecimal(StringUtils.trimToNull(b.getX("extHistoryVendorPrice1").toString()));
                                    return aVendorMinPrice.compareTo(bVendorMinPrice);
                                })
                                .collect(Collectors.toList());
                        minPriceReqLine = tempReqLineList.isEmpty() ? null : tempReqLineList.get(0);
                    }

                    // 近期最低价格(未税)
                    result.setLatestMinNotaxPrice(minPriceReqLine != null && minPriceReqLine.getX("latestMinNotaxPrice") != null ? new BigDecimal(minPriceReqLine.getX("latestMinNotaxPrice").toString()) : null);
                    // 浮动比例
                    if (result.getLatestMinNotaxPrice() != null) {
                        result.setPriceFloatScale(result.getStandardNotaxPrice().subtract(result.getLatestMinNotaxPrice()).divide(result.getLatestMinNotaxPrice(), 2, RoundingMode.HALF_UP));
                    }
                    // 近期最低供应商
                    result.setLatestMinVendorId(null);
                    result.setLatestMinVendorCode(minPriceReqLine != null ? minPriceReqLine.getX("extHistoryVendorCode1") : null);
                    result.setLatestMinVendorName(minPriceReqLine != null ? minPriceReqLine.getX("extHistoryVendorName1") : null);
                    // 近期最低价品牌
                    result.setLatestMinBrand(minPriceReqLine != null ? minPriceReqLine.getX("extHistoryVendorBrand1") : null);
                    // 预估单价
                    BigDecimal predictNotaxPrice = BigDecimal.ZERO;
                    for (RequirementLine reqLine : reqLineList) {
                        String extPredictPrice = StringUtils.trimToNull(reqLine.getX("extPredictPrice") != null ? reqLine.getX("extPredictPrice").toString() : null);
                        if (extPredictPrice != null) {
                            predictNotaxPrice = predictNotaxPrice.add(new BigDecimal(extPredictPrice));
                            break;
                        }
                    }
                    result.setExtPredictPrice(predictNotaxPrice);
                    // 预估总价
                    BigDecimal totalPredictPrice = BigDecimal.ZERO;
                    for (RequirementLine reqLine : reqLineList) {
                        String extPredictAmount = StringUtils.trimToNull(reqLine.getX("extPredictAmount") != null ? reqLine.getX("extPredictAmount").toString() : null);
                        if (extPredictAmount != null) {
                            totalPredictPrice = totalPredictPrice.add(new BigDecimal(extPredictAmount));
                        }
                    }
                    result.setExtPredictAmount(totalPredictPrice);
                    // 购买类型
                    result.setExtBuyType(reqLineList.isEmpty() ? null : reqLineList.get(0).getX("extBuyType"));
                    // 申请类型
                    result.setApplyType(reqHead != null ? reqHead.getCeeaPrType() : null);
                    //使用部门
                    List<Long> rlList = reqLineList.stream().map(RequirementLine::getRequirementLineId).collect(Collectors.toList());
                    List<Record> recordList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
                            .in(RequirementLine::getRequirementLineId, new ArrayList<>(rlList)), Record.class);
                    List<String> idStr = new ArrayList<>();
                    recordList.forEach(e -> idStr.add(e.getString("extUseDepartmentName")));
                    String deptNames = String.join(",", idStr);
                    result.setUseDeptName(deptNames);
                }
            });
        }
        context.getResultList().forEach(result -> {
            InqSouItem inqSouItem = inqSouItemMap.get(result.getSouItemId());
            result.setBuyerUsername(inqSouItem.getCreatedBy());
            result.setBuyerNickname(inqSouItem.getCreatedFullName());
            // 申请类型??
            result.setSourceFromType(ExtFixPriceSourceFromTypeEnum.INQ);
            // 未税总价
            if (result.getOrderNotaxPrice() != null && result.getRequireQuantity() != null) {
                result.setStandardNotaxTotalPrice(result.getOrderNotaxPrice().multiply(result.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP));
            }
        });
    }

}
