package com.midea.cloud.srm.sou.meiql.ca.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.ca.dto.*;
import com.midea.cloud.srm.model.sou.ca.dto.*;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectExtendDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementFromEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.ca.service.CaService;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.time.Year;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class CaServiceImpl implements CaService {

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private IExtSouPriceTemplateService priceTemplateService;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouScoreRuleService scoreRuleService;

    @Autowired
    private IExtSouTechScoreLineService techScoreLineService;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;


    @Override
    public CaDTO add(Long projectId) throws Exception {
        ApiExtSouProjectInfoDTO projectInfoDTO = extSouInitQueryService.getProjectInfo(projectId);
        AssertUtils.notNull(projectInfoDTO,"项目ID不存在系统中");
        //一、主表信息生成
        CaDTO caDTO = BeanCopyUtil.convertWithExtensions(projectInfoDTO.getProject(),CaDTO.class);
        //需求部门
        caDTO.setDemandDepartmentName(projectInfoDTO.getProject().getExtApplicantDepart());
        //需求人
        caDTO.setDemandUserNickname(projectInfoDTO.getProject().getExtApplicant());
        caDTO.setStatus(CaStatusEnum.DRAFT.getCode());
        caDTO.setType(CaTypeEnum.APPLY.getCode());
        if (CollectionUtils.isNotEmpty(projectInfoDTO.getPlanList())) {
            List<ExtSouPlan> planList = projectInfoDTO.getPlanList();
            for (ExtSouPlan plan : planList) {
                if (SouBidPlanTypeEnum.ACTUAL.getCode().equals(plan.getPlanType())) {
                    //发标时间（招标单发布时间）
                    if (null != plan.getPublishTime()) {
                        caDTO.setPublishTime(plan.getPublishTime());
                    }
                    //收标时间 先技术后商务：取技术标截止时间；同时收标：取商务标截止时间；
                    if(SouBidSouModeEnum.TECH_THEN_BUS.getCode().equals(projectInfoDTO.getProject().getExtSouMode())) {
                        caDTO.setBusEndTime(plan.getTechEndTime());
                    } else {
                        caDTO.setBusEndTime(plan.getBusEndTime());
                    }

                    //评标结束
                    if (null != plan.getTechEvaluationTime()) {
                        caDTO.setTechEvaluationTime(plan.getTechEvaluationTime());
                    }
                    //开价格标时间
                    if (null != plan.getPriceOpenTime()) {
                        caDTO.setPriceOpeningTime(plan.getPriceOpenTime());
                    }
                }
            }
        }

        //构造投标时间
        List<CaTenderTimeDto> tenderTimeDtos = generateCaTenderTime(projectInfoDTO);
        caDTO.setCaTenderTimes(tenderTimeDtos);


        //项目概况与招标范围  根据申请号去推荐供应商
//        caDTO.setProjectOverviewAndBidScope(getProjectRemart(projectInfoDTO.getProject().getApplicantNo()));
        //取供应商推荐单信息
        RecommvendorProjectExtendDto recommvendorProjectExtendDto = getRecommvendorProjectExtend(projectId);
        //项目概况与招标范围
        caDTO.setProjectOverviewAndBidScope(recommvendorProjectExtendDto.getProjectRemark());
        //供应商资质要求
        caDTO.setVendorFlairAdjure(recommvendorProjectExtendDto.getVendorFlairAdjure());
        //供应商业绩要求
        caDTO.setVendorBizAdjure(recommvendorProjectExtendDto.getVendorBizAdjure());
        String applicantNo = projectInfoDTO.getProject().getApplicantNo();

        //判斷是否手工填写
        //获取采购申请是否特殊招标
        String ifSpecialBid = this.getIfSpecialBid(projectInfoDTO.getProject().getApplicantNo());
        String isWriter = this.getIsWriteFlag(projectInfoDTO.getProject().getExtCategoryId(),projectInfoDTO.getProject().getExtBudget(),ifSpecialBid);
        caDTO.setIfWrite(isWriter);

        //投标供应商
        List<ExtSouOrder> souOrderDtos = extNpmSouOrderService.queryNewestOrder(projectId);

        if (CollectionUtils.isNotEmpty(souOrderDtos)) {
            List<ExtSouVendor> vendorList = vendorService.lambdaQuery()
                    .eq(ExtSouVendor::getProjectId, projectId)
                    .in(ExtSouVendor::getVendorId, souOrderDtos.stream().map(s -> s.getVendorId()).distinct().collect(Collectors.toList())).list();

            Map<Long, ExtSouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(k -> k.getVendorId(), Function.identity(), (k1, k2)->k2));
            //获取第一轮次, 存在技术标取技术标，否则取商务标
            List<CaOrderDTO> caOrderDtos = new ArrayList<>();
            CaOrderDTO caOrderDto = null;
            for (ExtSouOrder dto : souOrderDtos) {
                caOrderDto = BeanCopyUtil.convertWithExtensions(dto,CaOrderDTO.class);
                ExtSouVendor vendor = vendorMap.getOrDefault(dto.getVendorId(), new ExtSouVendor());
                caOrderDto.setVendorCode(vendor.getVendorCode());
                caOrderDto.setVendorName(vendor.getVendorName());
                caOrderDto.setExtVendorAttr(vendor.getExtVendorAttr());
                caOrderDtos.add(caOrderDto);
            }
            caDTO.setCaOrders(caOrderDtos);
        }
        //二、供应商总体情况
        //查询已投标的供应商信息
        List<ExtSouOrder> busOrderList = souOrderDtos.stream().filter(o -> SouOrderStatusEnum.SUBMISSION.equals(o.getOrderStatus())).filter(o -> ExtOrderTypeEnum.BUS.getCode().equals(o.getExtOrderType())).collect(Collectors.toList());
        //1.查询邀请供应商
        LambdaQueryWrapper<ExtSouVendor> querySouVendorWrapper = new LambdaQueryWrapper<>();
        querySouVendorWrapper.eq(ExtSouVendor::getProjectId, projectId);
        List<ExtSouVendor> souVendorList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(busOrderList)) {
            querySouVendorWrapper.in(ExtSouVendor::getVendorId, busOrderList.stream().map(ExtSouOrder::getVendorId).collect(Collectors.toList()));
            souVendorList = vendorService.list(querySouVendorWrapper);
        }


        //获取报价
        ApiExtComparePriceRespDto priceRespDto = extSouInitQueryService.getComparePrice(projectId);
        Map<Long, ApiExtCompareVendorPriceDto> priceMap = priceRespDto.getPriceMap();
        BigDecimal sumPrice = BigDecimal.ZERO;
        boolean priceFlag = false;
        BigDecimal avgPrice = BigDecimal.ZERO;
        int size = 0;
        if (null != priceRespDto.getComparePriceList() ) {
            for (ApiExtCompareVendorPriceDto dto : priceMap.values()) {
                if(null != dto.getExtTotalProvPriceSumTax() && !BigDecimal.ZERO.equals(dto.getExtTotalProvPriceSumTax()) ) {
                    sumPrice = sumPrice.add(dto.getExtTotalProvPriceSumTax());
                    size ++ ;
                }
            }
            if (size != 0) {
                priceFlag = true;
                avgPrice = sumPrice.divide(new BigDecimal(size), 6, RoundingMode.HALF_UP);
            }
        }
        //获取权重规则

        BigDecimal techWeight = null;
        String comprehensive = "COMPREHENSIVE";
        if (null != projectInfoDTO.getProject().getExtScoreRule() && projectInfoDTO.getProject().getExtScoreRule().startsWith(comprehensive)) {
            List<DictItemDTO> scoreRuleList = baseClient.listAllByDictCode("SOU_BID_SCORE_RULE");
            for (DictItemDTO dto : scoreRuleList) {
                if (dto.getDictItemCode().equals(projectInfoDTO.getProject().getExtScoreRule())){
                    techWeight = new BigDecimal(dto.getItemDescription());
                    break;
                }
            }
        }
        //2.查询技术平均评分
        Map<Long, BigDecimal> techScoreMap = extSouInitQueryService.caculateAverageScore(projectId);
        //查询技术组长的结论
        Map<Long, String> leaderConclusionMap = querySouLeaderConclusion(projectId);
        int i = 1;
        List<CaSupplierDTO> caSupplierDtos = new ArrayList<>();
        for (ExtSouVendor extSouVendor : souVendorList) {
            CaSupplierDTO caSupplierDto = BeanCopyUtil.convertWithExtensions(extSouVendor, CaSupplierDTO.class);
            caSupplierDto.setLineNum(i);
            caSupplierDtos.add(caSupplierDto);
            //2.1设置报价
            if (null != priceMap && priceMap.containsKey(caSupplierDto.getVendorId()) && null != priceMap.get(caSupplierDto.getVendorId())) {
                caSupplierDto.setBidTotalPrice(priceMap.get(caSupplierDto.getVendorId()).getExtTotalProvPriceSumTax());
            }
            //2.2设置技术平均分
            if (null != techScoreMap && techScoreMap.containsKey(caSupplierDto.getVendorId()) && null != techScoreMap.get(caSupplierDto.getVendorId())) {
                caSupplierDto.setTechScore(techScoreMap.get(caSupplierDto.getVendorId()));
            }
            if  (priceFlag) {
                if (ObjectUtils.allNotNull(caSupplierDto.getBidTotalPrice()) && BigDecimal.ZERO.compareTo(caSupplierDto.getBidTotalPrice()) != 0) {
                    //2.3计算价格得分
                    BigDecimal priceScore = new BigDecimal("100").subtract(caSupplierDto.getBidTotalPrice().subtract(avgPrice).divide(avgPrice,6, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
                    caSupplierDto.setPriceScore(priceScore);
                }
            }
            //技术组长结论
            caSupplierDto.setComprehensiveEvaluation(leaderConclusionMap.getOrDefault(caSupplierDto.getVendorId(), ""));
            i++;
        }

        if(priceFlag){
            BigDecimal finalTechWeight = techWeight;
            BigDecimal finalAvgPrice = avgPrice;
            BigDecimal maxPrice = caSupplierDtos.stream().map(CaSupplierDTO::getPriceScore).filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

            caSupplierDtos.forEach(caSupplierDto->{

                if (ObjectUtils.allNotNull(caSupplierDto.getBidTotalPrice()) && BigDecimal.ZERO.compareTo(caSupplierDto.getBidTotalPrice()) != 0) {
                    BigDecimal priceScore = caSupplierDto.getPriceScore().divide(maxPrice, 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                    //保留两位小数
                    priceScore = priceScore.setScale(2, BigDecimal.ROUND_HALF_UP);
                    //如果价格得分<0，则默认为0
                    if (priceScore.compareTo(BigDecimal.ZERO)<0) {
                        caSupplierDto.setPriceScore(BigDecimal.ZERO);
                    } else {
                        caSupplierDto.setPriceScore(priceScore);
                    }
                    //2.4 综合得分
                    if (null != finalTechWeight) {
                        caSupplierDto.setCompositeScore(getCompositeScore(caSupplierDto, finalTechWeight));
                    }
                }

            });
        }
        caDTO.setCaSuppliers(caSupplierDtos);

        if(!souVendorList.isEmpty()) {
            Map<Long, BigDecimal> sumByVendor = queryOrderInfo(projectId);
            List<CaNegotiateDto> caNegotiates = new ArrayList<>();
            CaNegotiateDto negotiateDtoTitle = new CaNegotiateDto();
            negotiateDtoTitle.setVendorId(-1L);
            negotiateDtoTitle.setVendorName("投标供应商");
            negotiateDtoTitle.setVendorCode("SRM");
            negotiateDtoTitle.setNegotiate("首次报价（万元）");
            caNegotiates.add(negotiateDtoTitle);
            //谈判对比
            for (int index = 0; index < souVendorList.size(); index++) {
                ExtSouVendor extSouVendor = souVendorList.get(index);
                CaNegotiateDto negotiateDto = new CaNegotiateDto();
                negotiateDto.setVendorId(extSouVendor.getVendorId());
                negotiateDto.setVendorName(extSouVendor.getVendorName());
                negotiateDto.setVendorCode(extSouVendor.getVendorCode());
                if(sumByVendor.containsKey(extSouVendor.getVendorId())) {
                    negotiateDto.setNegotiate(ObjectUtils.defaultIfNull(sumByVendor.get(extSouVendor.getVendorId()), BigDecimal.ZERO).stripTrailingZeros().toPlainString());
                }

                negotiateDto.setSortIndex(1);
                caNegotiates.add(negotiateDto);
            }
            caDTO.setCaNegotiates(caNegotiates);
        }


        //只展示已投商务标的供应商
        /*LambdaQueryWrapper<ExtSouOrder> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(ExtSouOrder::getProjectId, projectId);
        orderQuery.eq(ExtSouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name());
        List<ExtSouOrder> orderList = orderService.list(orderQuery);
        List<Long> vendorIds = orderList.stream().map(ExtSouOrder::getVendorId).distinct().collect(Collectors.toList());*/

        //三、供应商选定结果
        i = 1;
        List<CaSelectionResultDTO> caSelectionResultDTOS = new ArrayList<>();
        for (ExtSouVendor extSouVendor : souVendorList) {
            /*if (!vendorIds.contains(extSouVendor.getVendorId())) {
                continue;
            }*/
            CaSelectionResultDTO caResultDTO = BeanCopyUtil.convertWithExtensions(extSouVendor, CaSelectionResultDTO.class);
            caResultDTO.setLineNum(i);
            caSelectionResultDTOS.add(caResultDTO);
            i++;
        }
        caDTO.setCaSelectionResults(caSelectionResultDTOS);

        //再次创建定标申请单时，若存在已废弃的定标单，则需将最新已废弃的定标单的合同经办人、第一层级审批人、质保期、工期/交货期要求、项目概况与招标范围、付款要求、备注、附件复制至新的定标申请单中
        Record abandonCa = queryNewestAbandonCa(projectId);
        copyFieldsFromNewestAbandonCa(caDTO, abandonCa);
        //保存
        List<Serializable> ids = qlService.create(TypeEnum.Ca.getCode(), Arrays.asList(caDTO));
        caDTO.setCaId((Long) ids.get(0));
        saveFileAsCreateCa(caDTO);
        if(!Objects.isNull(abandonCa)) {
            saveCaNegotiate(abandonCa.get(CaDTO::getOriginalCaId), caDTO.getCaId());
        }

        return caDTO;
    }

    /**
     * 保存废弃单原单的报价过程
     * @param orignalCaId
     * @param caId
     */
    private void saveCaNegotiate(Long orignalCaId, Long caId) {
        List<CaNegotiateDto> caNegotiateDtoList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaNegotiate.getCode())
                .eq(CaNegotiateDto::getCaId, orignalCaId), CaNegotiateDto.class);
        if(CollectionUtils.isEmpty(caNegotiateDtoList)) {
            return;
        }
        caNegotiateDtoList.stream().forEach(caNegotiate -> {
            caNegotiate.setCaId(caId);
            caNegotiate.setNegotiateId(IdGenrator.generate());
        });
        qlService.create(TypeEnum.CaNegotiate.getCode(), caNegotiateDtoList);
    }

    /**
     * 查询招标单据信息
     * @param projectId
     * @return
     */
    private Map<Long, BigDecimal> queryOrderInfo(Long projectId) {
        Map<Long, BigDecimal> sumByVendor = new HashMap<>(50);

//        List<Long> templateProjectIdList = new ArrayList<>();
//        templateProjectIdList.add(SrmConstant.LONG_MINUS_ONE);
//        templateProjectIdList.add(projectId);
//
//        /** 查询模板字段 'extQuantity', 'extPriceTax', 'extFixedPriceTax', 'requireQuantity' */
//        List<String> colCodeList = Arrays.asList(ExtSouBidComponent.fieldName(ExtSouItem::getExtQuantity), ExtSouBidComponent.fieldName(ExtSouItem::getExtPriceTax),
//                ExtSouBidComponent.fieldName(ExtSouItem::getExtFixedPriceTax), ExtSouBidComponent.fieldName(ExtSouItem::getRequireQuantity));
//
//        /** 查询报价模板 */
//        List<ExtSouPriceTemplate> priceTemplateList = priceTemplateService.lambdaQuery().in(ExtSouPriceTemplate::getProjectId, templateProjectIdList).in(ExtSouPriceTemplate::getColumnCode, colCodeList).list();
//        Map<Long, List<ExtSouPriceTemplate>> priceTemplateMap = priceTemplateList.stream().collect(Collectors.groupingBy(ExtSouPriceTemplate::getProjectId));

        /** 查询报价信息 */
        List<ExtSouItem> itemList = itemService.lambdaQuery().eq(ExtSouItem::getProjectId, projectId).list();
        Map<Long, ExtSouItem> itemMap = itemList.stream().collect(Collectors.toMap(ExtSouItem::getSouItemId, Function.identity(), (k1, k2)->k2));
//        bidOrderInfoMap.put(ORDER_INFO_KEY_ITEM, itemMap);

        /** 查询投标报价信息 */
        List<ExtSouOrderItem> orderItemList = orderItemService.lambdaQuery().eq(ExtSouOrderItem::getProjectId, projectId)
                .eq(ExtSouOrderItem::getRound,1)
                .eq(ExtSouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name()).list();

        if(CollectionUtils.isNotEmpty(orderItemList)) {
            List<ApiExtSouOrderItemDto> orderItemDtoList = JSON.parseArray(JSON.toJSONString(orderItemList), ApiExtSouOrderItemDto.class);
            orderItemDtoList.stream().forEach(orderItemDto -> {
                /** 转换报价模板字段 */
                orderItemDto.coverItemFields();
                /** 转换汇率 */
                orderItemDto.convertExchangeRateAsItemFields();
                BigDecimal priceTax = priceTax(orderItemDto);
                BigDecimal extQuantity = extQuantity(itemMap.getOrDefault(orderItemDto.getSouItemId(), new ExtSouItem()));
                //总价
                BigDecimal totalPriceTax = null;
                if(ObjectUtils.allNotNull(priceTax, extQuantity)) {
                    totalPriceTax = priceTax.multiply(extQuantity);
                }
                orderItemDto.setExtPriceTax(priceTax);
                orderItemDto.setExtPriceSumTax(totalPriceTax);
                orderItemDto.setExtQuantity(extQuantity);
            });
            sumByVendor = orderItemDtoList.stream()
                    .filter(s -> s.getExtPriceSumTax() != null)
                    .collect(
                    Collectors.groupingBy(ApiExtSouOrderItemDto::getVendorId,
                    Collectors.mapping(ApiExtSouOrderItemDto::getExtPriceSumTax, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        }

        return sumByVendor;
    }

    /**
     * 获取数量
     * @param item
     * @return
     */
    private BigDecimal extQuantity(ExtSouItem item) {
        if(item.getExtQuantity() == null) {
            return item.getRequireQuantity();
        } else {
            return item.getExtQuantity();
        }
    }

    /**
     * 获取含税单价
     * @param orderItemDto
     * @return
     */
    private BigDecimal priceTax(ApiExtSouOrderItemDto orderItemDto) {
        if(orderItemDto.getExtPriceTax() == null) {
            return orderItemDto.getExtFixedPriceTax();
        } else {
            return orderItemDto.getExtPriceTax();
        }
    }

    private void saveFileAsCreateCa(CaDTO caDTO) {
        if(CollectionUtils.isNotEmpty(caDTO.getSceneFiles())) {
            caDTO.getSceneFiles().stream().forEach(sceneFile -> {
                sceneFile.setBusinessId(caDTO.getCaId());
            });
            qlOpenClient.save(ContextPath.BASE, MqlType.SCC_BASE_SCENE_FILE, caDTO.getSceneFiles());
        }
    }

    private void copyFieldsFromNewestAbandonCa(CaDTO caDTO, Record abandonCa) {
        if(Objects.isNull(abandonCa)) {
            return;
        }
        /**合同经办人 */
        caDTO.setContractOperatorUserId(abandonCa.get(CaDTO::getContractOperatorUserId));
        caDTO.setContractOperatorUsername(abandonCa.get(CaDTO::getContractOperatorUsername));
        caDTO.setContractOperatorNickname(abandonCa.get(CaDTO::getContractOperatorNickname));
        /**第一层级审批人 */
        caDTO.setApprovalUserId(abandonCa.get(CaDTO::getApprovalUserId));
        caDTO.setApprovalUserName(abandonCa.get(CaDTO::getApprovalUserName));
        caDTO.setApprovalNickname(abandonCa.get(CaDTO::getApprovalNickname));
        /** 质保期 */
        caDTO.setWarrantyPeriod(abandonCa.get(CaDTO::getWarrantyPeriod));
        /**工期/交货期要求 */
        caDTO.setTimeLimit(abandonCa.get(CaDTO::getTimeLimit));
        /**项目概况与招标范围 */
        caDTO.setProjectOverviewAndBidScope(abandonCa.get(CaDTO::getProjectOverviewAndBidScope));
        /**付款要求 */
        caDTO.setPaymentRequirements(abandonCa.get(CaDTO::getPaymentRequirements));
        /**备注 */
        caDTO.setRemark(abandonCa.get(CaDTO::getRemark));
        /**预算与报价差异分析*/
        caDTO.setBudgetPriceDiff(abandonCa.get(CaDTO::getBudgetPriceDiff));
        /**前期采购情况*/
        caDTO.setPreviousPurchase(abandonCa.get(CaDTO::getPreviousPurchase));
        /**厂家报价差异分析*/
        caDTO.setManufacturerAnalysis(abandonCa.get(CaDTO::getManufacturerAnalysis));
        /**附件 */
        caDTO.setSceneFiles(queryCaFile(abandonCa));

    }

    private List<SceneFile> queryCaFile(Record ca) {
        List<SceneFile> recordList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(MqlType.SCC_BASE_SCENE_FILE)
                .eq(SceneFile::getSceneModuleCode, SrmConstant.SCENE_SOU_CA_ATTACHMENT)
                .eq(SceneFile::getBusinessId, ca.get(CaDTO::getCaId)), SceneFile.class);
        if(CollectionUtils.isEmpty(recordList)) {
            return new ArrayList<>();
        }
        recordList.stream().forEach(file -> {
            file.setBusinessId(null);
            file.setSceneFileId(null);
        });
        return recordList;
    }

    /**
     * 查询最新已废弃的单据
     * @param projectId
     * @return
     */
    private Record queryNewestAbandonCa(Long projectId) {
        List<Record> recordList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.Ca.getCode()).eq(CaDTO::getProjectId, projectId)
                .eq(CaDTO::getType, CaTypeEnum.APPLY.getCode())
                .eq(CaDTO::getStatus, CaStatusEnum.ABANDON.getCode()).orderByDesc(CaDTO::getCreationDate), Record.class);
        if(CollectionUtils.isNotEmpty(recordList)) {
            return recordList.get(0);
        }
        return null;
    }

    private Map<Long, String> querySouLeaderConclusion(Long projectId) {
        Map<Long, String> leaderConclusionMap = new HashMap<>(15);
        //查询评分组长
        List<ExtSouGroup> leaderList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, projectId).eq(ExtSouGroup::getGroupRole, ExtSouGroupRoleEnum.LEADER.getCode()).eq(ExtSouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH.getCode()).eq(ExtSouGroup::getExtEvaFlag, YesOrNo.YES.getValue()).list();
        if(CollectionUtils.isEmpty(leaderList)) {
            return leaderConclusionMap;
        }
        //查询结论项
        List<ExtScoreRule> conclusionRuleList = scoreRuleService.lambdaQuery().eq(ExtScoreRule::getProjectId, projectId).eq(ExtScoreRule::getScoreItem, ScoreConfigItemEnum.COM_REVIEW.getCode()).eq(ExtScoreRule::getReviewItem, SrmConstant.SOU_SCORE_REVIEW_ITEM_CONCLUSION).list();
        if(CollectionUtils.isEmpty(conclusionRuleList)) {
            return leaderConclusionMap;
        }

        //查询技术评分
        List<ExtSouTechScoreLine> scoreLineList = techScoreLineService.lambdaQuery().eq(ExtSouTechScoreLine::getProjectId, projectId).eq(ExtSouTechScoreLine::getGroupId, leaderList.get(0).getGroupId()).eq(ExtSouTechScoreLine::getScoreRuleLineId, conclusionRuleList.get(0).getScoreRuleId()).orderByAsc(ExtSouTechScoreLine::getCreationDate).list();
        if(CollectionUtils.isEmpty(scoreLineList)) {
            return leaderConclusionMap;
        }

        //赋值
        scoreLineList.stream().forEach(extSouTechScoreLine -> {
            leaderConclusionMap.put(extSouTechScoreLine.getVendorId(), extSouTechScoreLine.getExtDescription());
        });
        return leaderConclusionMap;
    }

    private List<CaTenderTimeDto> generateCaTenderTime(ApiExtSouProjectInfoDTO projectInfoDTO) {
        List<CaTenderTimeDto> tenderTimeDtos = new ArrayList<>();

        //查询技术负责人
        List<ExtSouGroup> groupList = groupService.lambdaQuery().eq(ExtSouGroup::getProjectId, projectInfoDTO.getProject().getProjectId())
                .in(ExtSouGroup::getGroupRole, Arrays.asList(ExtSouGroupRoleEnum.LEADER.getCode(), ExtSouGroupRoleEnum.PRINCIPAL.getCode()))
                .list();
        //技术负责人
        String techPerson = projectInfoDTO.getProject().getExtTechPrincipal();
        //招标负责人
        String bidPerson = null;
        //技术组长
        String bidLeader = null;
        if(CollectionUtils.isNotEmpty(groupList)) {
            for(ExtSouGroup group : groupList) {
                if(ExtSouGroupRoleEnum.PRINCIPAL.getCode().equals(group.getGroupRole())) {
                    bidPerson = group.getFullName();
                }
                if(YesOrNo.YES.getValue().equals(group.getExtEvaFlag()) && ExtSouGroupRoleEnum.LEADER.getCode().equals(group.getGroupRole())) {
                    bidLeader = group.getFullName();
                }
            }
        }

        //查询申请单扩展信息
        List<ExtPrSouRequirementHead> requirementHeads = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                .in(ExtPrSouRequirementHead::getRequirementHeadId, new ArrayList<>(Arrays.asList(StringUtils.defaultIfBlank(projectInfoDTO.getProject().getApplicantId(), "-1").split(";"))))
                .isNotNull(ExtPrSouRequirementHead::getSendSouProfileEndDate)
                .orderByAsc(ExtPrSouRequirementHead::getSendSouProfileEndDate), ExtPrSouRequirementHead.class);
        //查询首个资料递交单据
        List<BidDataSubmit> bidDataSubmitList = qlService.queryByWrapper(QlWrappers.query(MqlType.SUBMIT_BUYER)
                .in(BidDataSubmit::getRequirementHeadNum, new ArrayList<>(Arrays.asList(StringUtils.defaultIfBlank(projectInfoDTO.getProject().getApplicantNo(), "-1").split(";"))))
                .isNotNull(BidDataSubmit::getPublishTime)
                .orderByAsc(BidDataSubmit::getPublishTime), BidDataSubmit.class);

        //递交招标资料: 计划 取申请单：资料递交时间； 实际 提交首份招标资料时间（发布时间）
        CaTenderTimeDto caTenderTimeSubmite = getCaTenderTime(CaTenderTimeTypeEnum.SUBMITE, 1);
        caTenderTimeSubmite.setDepartment(CaTenderTimeDepartmentEnum.APPLY.getName());
        //技术负责人：XXX；评标组长：XXX
        caTenderTimeSubmite.setDutyOfficer(getDutyOfficer(new String[]{CaTenderTimeRoleEnum.TECH_PERSON.getName(), techPerson}, new String[]{CaTenderTimeRoleEnum.BID_LEADER.getName(), bidLeader}));
        if(CollectionUtils.isNotEmpty(requirementHeads)) {
            caTenderTimeSubmite.setPlanTime(DateUtil.localDateToDate(requirementHeads.get(0).getSendSouProfileEndDate()));
        }
        if(CollectionUtils.isNotEmpty(bidDataSubmitList)) {
            caTenderTimeSubmite.setActualTime(bidDataSubmitList.get(0).getPublishTime());
        }
        tenderTimeDtos.add(caTenderTimeSubmite);

        //发出招标文件: 取招标书发布时间
        CaTenderTimeDto caTenderTimeSendBid = getCaTenderTime(CaTenderTimeTypeEnum.SEND_BID, 2);
        caTenderTimeSendBid.setDutyOfficer(getDutyOfficer(new String[]{CaTenderTimeRoleEnum.BID_PERSON.getName(), bidPerson}));
        caTenderTimeSendBid.setPlanTime(projectInfoDTO.getProject().getPublishTime());
        caTenderTimeSendBid.setActualTime(projectInfoDTO.getProject().getPublishTime());
        tenderTimeDtos.add(caTenderTimeSendBid);

        //收标: 计划 优先取计划技术标截止时间，无取计划商务标截止时间。   实际 优先取招标书技术标截止时间，无则取商务标截止时间
        CaTenderTimeDto caTenderTimeAcceptBid = getCaTenderTime(CaTenderTimeTypeEnum.ACCEPT_BID, 3);
        caTenderTimeAcceptBid.setDutyOfficer(getDutyOfficer(new String[]{CaTenderTimeRoleEnum.BID_PERSON.getName(), bidPerson}));
        tenderTimeDtos.add(caTenderTimeAcceptBid);
        //澄清变更及评技术标  计划 取招标书的计划技术标评标时间     实际  取招标书的实际技术标评标时间
        CaTenderTimeDto caTenderTimeClarifyTech = getCaTenderTime(CaTenderTimeTypeEnum.CLARIFY_TECH, 4);
        caTenderTimeClarifyTech.setDutyOfficer(getDutyOfficer(new String[]{CaTenderTimeRoleEnum.BID_LEADER.getName(), bidLeader}));
        caTenderTimeClarifyTech.setDepartment(CaTenderTimeDepartmentEnum.APPLY.getName());
        tenderTimeDtos.add(caTenderTimeClarifyTech);

        //商务标分析汇总  计划 取招标书汇总上报计划     实际  取招标书汇总上报实际
        CaTenderTimeDto caTenderTimeBusAnalysis = getCaTenderTime(CaTenderTimeTypeEnum.BUS_ANALYSIS, 5);
        caTenderTimeBusAnalysis.setDutyOfficer(getDutyOfficer(new String[]{CaTenderTimeRoleEnum.BID_PERSON.getName(), bidPerson}));
        tenderTimeDtos.add(caTenderTimeBusAnalysis);

        //定标 计划 取招标书定标计划     实际  取招标书定标实际
        CaTenderTimeDto caTenderTimeDecideBid = getCaTenderTime(CaTenderTimeTypeEnum.DECIDE_BID, 6);
        caTenderTimeDecideBid.setDepartment(CaTenderTimeDepartmentEnum.APPLY.getName());
        tenderTimeDtos.add(caTenderTimeDecideBid);

        if(CollectionUtils.isNotEmpty(projectInfoDTO.getPlanList())) {
            //截止时间
            AtomicReference<Date> planEndTime = new AtomicReference<>(null);
            AtomicReference<Date> actualEndTime = new AtomicReference<>(null);

            //评标时间
            AtomicReference<Date> planEvaTime = new AtomicReference<>(null);
            AtomicReference<Date> actualEvaTime = new AtomicReference<>(null);

            //汇总上报时间
            AtomicReference<Date> planReportTime = new AtomicReference<>(null);
            AtomicReference<Date> actualReportTime = new AtomicReference<>(null);

            //定标时间
            AtomicReference<Date> planPickTime = new AtomicReference<>(null);
            AtomicReference<Date> actualPickTime = new AtomicReference<>(null);

            projectInfoDTO.getPlanList().stream().forEach(p -> {
                if(SouBidPlanTypeEnum.PLAN.getCode().equals(p.getPlanType())) {
                    //计划截止时间
                    if(!Objects.isNull(p.getTechEndTime())) {
                        planEndTime.set(p.getTechEndTime());
                    } else if(!Objects.isNull(p.getBusEndTime())) {
                        planEndTime.set(p.getBusEndTime());
                    }

                    //计划技术评标时间
                    planEvaTime.set(p.getTechEvaluationTime());

                    //计划汇总上报时间
                    planReportTime.set(p.getSumReportTime());

                    //计划定标时间
                    planPickTime.set(p.getPicketageTime());
                } else {

                    //实际截止时间
                    if(!Objects.isNull(p.getTechEndFixTime())) {
                        actualEndTime.set(p.getTechEndFixTime());
                    } else if(!Objects.isNull(p.getTechEndTime())) {
                        actualEndTime.set(p.getTechEndTime());
                    } else if(!Objects.isNull(p.getBusEndTime())) {
                        actualEndTime.set(p.getBusEndTime());
                    }

                    //实际技术评标时间
                    actualEvaTime.set(p.getTechEvaluationTime());

                    //实际汇总上报时间
                    actualReportTime.set(p.getSumReportTime());

                    //实际定标时间
                    actualPickTime.set(p.getPicketageTime());
                }
            });
            //收标
            caTenderTimeAcceptBid.setPlanTime(planEndTime.get());
            caTenderTimeAcceptBid.setActualTime(planEndTime.get());

            //澄清变更及评技术标
            caTenderTimeClarifyTech.setPlanTime(planEvaTime.get());
            caTenderTimeClarifyTech.setActualTime(actualEvaTime.get());

            //商务标分析汇总
            caTenderTimeBusAnalysis.setPlanTime(planReportTime.get());
            caTenderTimeBusAnalysis.setActualTime(actualReportTime.get());

            //定标
            caTenderTimeDecideBid.setPlanTime(planPickTime.get());
            caTenderTimeDecideBid.setActualTime(actualPickTime.get());
        }

        return tenderTimeDtos;
    }

    private String getDutyOfficer(String[]... dutyOfficer) {
        List<String> officerList = new ArrayList<>();
        for(String[] duty : dutyOfficer) {
            if(StringUtils.isBlank(duty[1])) {
                continue;
            }
            officerList.add(MessageFormat.format("{0}: {1}", duty[0], duty[1]));
        }
        return officerList.stream().collect(Collectors.joining("; "));
    }

    private CaTenderTimeDto getCaTenderTime(CaTenderTimeTypeEnum typeEnum, Integer index) {
        CaTenderTimeDto caTenderTimeDto = new CaTenderTimeDto();
        caTenderTimeDto.setType(typeEnum.getCode());
        caTenderTimeDto.setSortIndex(index);
        //默认招标部门
        caTenderTimeDto.setDepartment(CaTenderTimeDepartmentEnum.BID.getName());
        return caTenderTimeDto;
    }


    public BigDecimal getCompositeScore(CaSupplierDTO dto , BigDecimal techWeight) {
       return dto.getTechScore().multiply(techWeight.multiply(new BigDecimal(0.01))).add(dto.getPriceScore()
               .multiply(new BigDecimal("1").subtract(techWeight.multiply(new BigDecimal(0.01)))));
    }

    /**
     * 判断是否填写
     * @param categoryId
     * @param extBudget
     * @param ifSpecialBid
     * @return
     */
    private String getIsWriteFlag(Long categoryId,BigDecimal extBudget,String ifSpecialBid) {
        if (null != categoryId) {
            PurchaseCategory category = new PurchaseCategory();
            category.setCategoryId(categoryId);
            category = baseClient.getPurchaseCategoryByParm(category);
            if (null != category) {
                String ifBid = (String) category.getExtensions().get("ifBid");
                if (StringUtils.isNotEmpty(ifBid)) {
                    //1、属于招标范围且金额小于10万
                    Integer num10 = 10;
                    if (YesOrNo.YES.getValue().equals(ifBid) && extBudget.compareTo(new BigDecimal(num10)) < 1) {
                        return YesOrNo.YES.getValue();
                    }
                    //2、不属于招标范围
                    if (YesOrNo.NO.getValue().equals(ifBid)) {
                        return YesOrNo.YES.getValue();
                    }
                }
            }
            //3、特殊招标
            if (YesOrNo.YES.getValue().equals(ifSpecialBid)) {
                return YesOrNo.YES.getValue();
            }
        }
        return YesOrNo.NO.getValue();
    }

    /**
     * 获取采购申请是否特殊招标
     * @param applicantNo
     * @return
     */
    private String getIfSpecialBid (String applicantNo) {
        if (StringUtils.isNotEmpty(applicantNo)) {
            QlOpenQueryWrapper wrapper = QlOpenWrappers.query("PrSouRequirementPoolForBuyer").in("requirementHeadNum", Arrays.asList(applicantNo.split(";")));
            List<ExtPrSouRequirementHead> requirements = qlOpenClient.query(ContextPath.SUP_CE, wrapper, ExtPrSouRequirementHead.class);
            if (CollectionUtils.isNotEmpty(requirements)) {
                for (ExtPrSouRequirementHead req : requirements) {
                    Record record = qlOpenClient.read(ContextPath.SUP_CE,"ExtPrSouRequirementHead",req.getRequirementHeadId(),Record.class);
                    if (PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(record.getString("requireFrom"))) {
                        return YesOrNo.YES.getValue();
                    }
                }
            }
        }
        return YesOrNo.NO.getValue();
    }

    private RecommvendorProjectExtendDto getRecommvendorProjectExtend(Long projectId) {
        //查询申请单号
        List<ExtSouDemand> demandList = qlService.query(RecommType.RecommvendorDemand.name(),
                MeiQl.newCondition().eq(ExtSouDemand::getProjectId, projectId), ExtSouDemand.class);
        if(CollectionUtils.isEmpty(demandList)) {
            return new RecommvendorProjectExtendDto();
        }
        //查询推荐单ID
        List<ExtSouDemand> recommvendorDemandList = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorDemand.name(), "d")
                .in(ExtSouDemand::getApplicantNo, demandList.stream().map(d -> d.getApplicantNo()).distinct().collect(Collectors.toList()))
                .notEq(ExtSouDemand::getProjectId, projectId)
                .exists(RecommType.RecommvendorProject.name(), "p", ql -> {
                    ql.eq(QlQueryFieldWrapper.field("p", RecommvendorProjectDto::getProjectId), QlQueryFieldWrapper.field("d", ExtSouDemand::getProjectId))
                            .eq(RecommvendorProjectDto::getSouType, SouTypeEnum.recomm.name());
                }), ExtSouDemand.class);

        if(CollectionUtils.isEmpty(recommvendorDemandList)) {
            return new RecommvendorProjectExtendDto();
        }

        //查询原供应商推荐单
        List<RecommvendorProjectExtendDto> extendDtoList = qlService.queryByWrapper(QlWrappers.query(RecommType.RecommvendorProjectExtend.name()).in(RecommvendorProjectExtendDto::getProjectId, recommvendorDemandList.stream().map(d -> d.getProjectId()).distinct().collect(Collectors.toList()))
                .eq(RecommvendorProjectExtendDto::getRcommendType, SouRecommvendorTypeEnum.RECOMM.getCode()), RecommvendorProjectExtendDto.class);
        if(CollectionUtils.isEmpty(extendDtoList)) {
            return new RecommvendorProjectExtendDto();
        }

        return extendDtoList.get(0);
    }

    private String getProjectRemart(String applicantNo) {
        List<Record> list = qlService.queryByWrapper(QlWrappers.query("RecommvendorProject")
                .eq(RecommvendorProjectDto::getSourceFromNo,applicantNo.split(";")[0])
                .eq(RecommvendorProjectDto::getSouType, SouTypeEnum.recomm.name())
                .select(RecommvendorProjectDto::getProjectId),Record.class);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Record> extList = qlService.queryByWrapper(QlWrappers.query("RecommvendorProjectExtend")
                    .eq(RecommvendorProjectExtendDto::getProjectId,list.get(0).get(RecommvendorProjectDto::getProjectId))
                    .select(RecommvendorProjectExtendDto::getProjectRemark),Record.class);
            if (CollectionUtils.isNotEmpty(extList)) {
                return extList.get(0).get(RecommvendorProjectExtendDto::getProjectRemark);
            }
        }
        return null;
    }
    /**
     * 保存招标历史价格
     * @param caHistoryPriceList
     */
    @Override
    public void saveBidHistoryPrice(List<CaHistoryPriceDto> caHistoryPriceList) {
        qlService.create(TypeEnum.CaHistoryPrice.getCode(), caHistoryPriceList);
    }

}
