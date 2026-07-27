package com.midea.cloud.srm.sou.report.bid.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.base.CaseFormat;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BeanMapUtils;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.NpmSouBidProjectNoUtils;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.ca.dto.CaDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSupplierDTO;
import com.midea.cloud.srm.model.sou.ca.enums.CaStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtPrRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.ScheduleReportDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportQueryDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportToStrDto;
import com.midea.cloud.srm.model.sou.report.souschedules.dto.SccNpmSouScheduleReportDto;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.report.bid.mapper.ExtReportBidSuperviseMapper;
import com.midea.cloud.srm.sou.report.bid.service.ExtReportBidSuperviseService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouItemService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouPriceTemplateService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Service
@Api("上报监察报表-实现类")
public class ExtReportBidSuperviseServiceImpl implements ExtReportBidSuperviseService<SuperviseReportDto> {

    @Resource
    private ExtReportBidSuperviseMapper reportBidSuperviseMapper;
    
    @Autowired
    private QlService qlService;
    
    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IExtSouGroupService groupService;

    @Autowired
    private IExtSouPriceTemplateService priceTemplateService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private BaseClient baseClient;

    private static final String REUIREMENT_INFO_KEY_DEMAND = "REUIREMENT_INFO_KEY_DEMAND";
    private static final String REUIREMENT_INFO_KEY_REQ = "REUIREMENT_INFO_KEY_REQ";
    private static final String REUIREMENT_INFO_KEY_EXT_REQ = "REUIREMENT_INFO_KEY_EXT_REQ";
    private static final String REUIREMENT_INFO_KEY_GROUP = "REUIREMENT_INFO_KEY_GROUP";
    private static final String REUIREMENT_INFO_KEY_SUBMIT = "REUIREMENT_INFO_KEY_SUBMIT";
    private static final String REUIREMENT_INFO_KEY_SUBMIT_MAX = "REUIREMENT_INFO_KEY_SUBMIT_MAX";
    private static final String REUIREMENT_INFO_KEY_BID_GROUP = "REUIREMENT_INFO_KEY_BID_GROUP";
    private static final String ORDER_INFO_KEY_ORDER_ITEM = "ORDER_INFO_KEY_ORDER_ITEM";
    private static final String ORDER_INFO_KEY_ITEM = "ORDER_INFO_KEY_ITEM";
    private static final String CA_NOTICE_KEY_CADTO = "CA_NOTICE_KEY_CADTO";
    private static final String CA_NOTICE_KEY_NOTICEDTO = "CA_NOTICE_KEY_NOTICEDTO";

    private static final String COMPANY_SHORT_CODE = "GW";

    private final static int NUM50=50;

    @Override
    public PageInfo<SuperviseReportDto> listPage(Map<String, Object> query) {
        /** 分页查询-底表数据 */
        PageUtil.startPage(MapUtils.getInteger(query, ExtSouBidComponent.fieldName(ExtSouProject::getPageNum), 1), MapUtils.getInteger(query, ExtSouBidComponent.fieldName(ExtSouProject::getPageSize), 15));
        SuperviseReportQueryDto queryDto = BeanMapUtils.mapToBean(query, new SuperviseReportQueryDto());
        List<SuperviseReportDto> dataList = reportBidSuperviseMapper.listSuperviseReport(queryDto);
        PageInfo<SuperviseReportDto> pageInfo = new PageInfo<>(dataList);
        /** 补充报表数据 */
        fillReportData(dataList);
        return pageInfo;
    }

    @Override
    public List<SuperviseReportToStrDto> convertStrList(List<SuperviseReportDto> dataList) {
        List<SuperviseReportToStrDto> list = new ArrayList<>(16);
        if(CollectionUtils.isNotEmpty(dataList)) {
            dataList.stream().forEach(data -> {
                SuperviseReportToStrDto superviseReportToStrDto = new SuperviseReportToStrDto();
                list.add(superviseReportToStrDto);
                reflectBean(superviseReportToStrDto, data);
            });
        }
        return list;
    }

    /**
     * 通过反射设置属性值
     * @param superviseReportToStrDto
     * @param superviseReportDto
     */
    private void reflectBean(SuperviseReportToStrDto superviseReportToStrDto, SuperviseReportDto superviseReportDto) {
        Field[] fields = SuperviseReportToStrDto.class.getDeclaredFields();
        Map<String, Field> sourceFieldMap = new HashMap<>(50);
        Map<String, Field> targetFieldMap = new HashMap<>(50);
        Arrays.stream(fields).forEach(targetField -> {
            Field sourceField = getField(SuperviseReportDto.class, targetField.getName(), false);
            if(!Objects.isNull(sourceField)) {
                sourceFieldMap.put(sourceField.getName(), sourceField);
                targetFieldMap.put(targetField.getName(), targetField);
            }
        });

        for(String fieldName : sourceFieldMap.keySet()) {
            Method sourceGet = ReflectionUtils.findMethod(SuperviseReportDto.class, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, StringUtils.joinWith(SrmConstant.UNDER_LINE, SrmConstant.REFLECTION_GET, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))));
            if(Objects.isNull(sourceGet)) {
                continue;
            }

            Object value = ReflectionUtils.invokeMethod(sourceGet, superviseReportDto);

            Field targetField = targetFieldMap.get(fieldName);

            Class targetType = targetField.getType();

            Method targetSet = ReflectionUtils.findMethod(SuperviseReportToStrDto.class, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, StringUtils.joinWith(SrmConstant.UNDER_LINE, SrmConstant.REFLECTION_SET, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))), targetType);
            if(Objects.isNull(targetSet)) {
                continue;
            }

            if(Objects.isNull(value)) {
                if(targetType.getSimpleName().equals(String.class.getSimpleName())) {
                    ReflectionUtils.invokeMethod(targetSet, superviseReportToStrDto, SrmConstant.SHORT_LINE);
                }
            } else {
                if(targetType.getSimpleName().equals(value.getClass().getSimpleName())) {
                    ReflectionUtils.invokeMethod(targetSet, superviseReportToStrDto, value);
                } else {
                    //报表字符串转换
                    if(targetType.getSimpleName().equals(String.class.getSimpleName())) {
                        if(BigDecimal.class.getSimpleName().equals(value.getClass().getSimpleName())) {
                            BigDecimal bigDecimal = (BigDecimal) value;
                            String strValue = bigDecimal.stripTrailingZeros().toPlainString();
                            ReflectionUtils.invokeMethod(targetSet, superviseReportToStrDto, strValue);
                        } else if(Long.class.getSimpleName().equals(value.getClass().getSimpleName())) {
                            ReflectionUtils.invokeMethod(targetSet, superviseReportToStrDto, value.toString());
                        } else if(Date.class.getSimpleName().equals(value.getClass().getSimpleName())) {
                            ReflectionUtils.invokeMethod(targetSet, superviseReportToStrDto, DateUtil.format((Date)value, DateUtil.DATE_FORMAT_10));
                        }
                    }
                }
            }
        }
    }

    private Field getField(Class clazz, String fieldName, boolean superClazz) {
        if(Objects.isNull(clazz)) {
            return null;
        }
        Field field = ReflectionUtils.findField(clazz, fieldName);
        if(!superClazz && Objects.isNull(field)) {
            return getField(clazz.getSuperclass(), fieldName, true);
        }
        return field;
    }

    @Override
    public void fillReportData(List<SuperviseReportDto> dataList) {

        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }

        /** 招标单ID集合 */
        List<Long> projectIdList = dataList.stream().map(SuperviseReportDto::getProjectId).distinct().collect(Collectors.toList());

        /** 招标负责人	供应商负责人	评标组长	技术负责人 */
        Map<String, Object> reuirementInfoMap = queryRequirementInfo(projectIdList);

        /** 第一轮供应商报价总价（含税）	第二轮供应商报价总价（含税）	第三轮供应商报价总价（含税）*/
        Map<String, Object> bidOrderInfoMap = queryOrderInfo(projectIdList);

        /** 技术得分	综合得分	是否中标	"审批定标金额（万元）"	"中标通知金额（万元）" */
        Map<String, Object> caNoticeMap = queryCaAndNotice(projectIdList);

        /** 查询分类 */
        Map<Long, PurchaseCategory> purchaseCategoryMap = queryCategoryInfo(dataList);
        //字典 SOU_RECOMM_VENDOR_NATRUE
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode("SOU_RECOMM_VENDOR_NATRUE");
        Map<String, DictItemDTO> dictItemMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(gyqyList)){
            dictItemMap = gyqyList.stream().collect(Collectors.toMap(item -> item.getDictItemCode(), Function.identity(), (k1, k2) -> k1));

        }

        //可以查看的供应商报价状态
        List<String> showPriceStatus = new ArrayList<>();
        showPriceStatus.add(SouBiddingProStatusEnum.BUS_BID_OPEN.getCode());
        showPriceStatus.add(SouBiddingProStatusEnum.CONFIRM_BID.getCode());
        showPriceStatus.add(SouBiddingProStatusEnum.WIN_LOSS_NOTICE.getCode());
        showPriceStatus.add(SouBiddingProStatusEnum.ARCHIVE_TODO.getCode());
        showPriceStatus.add(SouBiddingProStatusEnum.ARCHIVE_DONE.getCode());
        for (SuperviseReportDto data : dataList) {
            /** 招标负责人	供应商负责人	评标组长	技术负责人 */
            fillDataWithRequirementInfo(data, reuirementInfoMap);
            /** 第一轮供应商报价总价（含税）	第二轮供应商报价总价（含税）	第三轮供应商报价总价（含税）*/
            if(showPriceStatus.contains(data.getProjectStatus())){
                fillDataWithOrderInfo(data, bidOrderInfoMap);
            }
            /** 技术得分	综合得分	是否中标	"审批定标金额（万元）"	"中标通知金额（万元）" */
            fillDataWithCaAndNotice(data, caNoticeMap, bidOrderInfoMap);
            /** 一级品类名称 */
            fillClassification(data, purchaseCategoryMap);
            /** 供应商来源字段转换 */
            getDictName(dictItemMap,data);
        }
    }

    /**
     * 分类--改成一级品类名称
     * @param data
     * @param purchaseCategoryMap
     */
    private void fillClassification(SuperviseReportDto data, Map<Long, PurchaseCategory> purchaseCategoryMap) {
        PurchaseCategory purchaseCategory = purchaseCategoryMap.getOrDefault(data.getExtCategoryId(), new PurchaseCategory());
        if(StringUtils.isNotBlank(purchaseCategory.getCategoryFullName())) {
            String[] categoryFullNames = purchaseCategory.getCategoryFullName().split(SrmConstant.SHORT_LINE);
            data.setClassification(categoryFullNames[0]);
        }
    }

    /**
     * 查询采购分类
     * @param dataList
     * @return
     */
    private Map<Long, PurchaseCategory> queryCategoryInfo(List<SuperviseReportDto> dataList) {

        List<Long> purchaseCategoryIds = dataList.stream().map(data -> data.getExtCategoryId()).distinct().collect(Collectors.toList());
        List<PurchaseCategory> purchaseCategoryList = baseClient.listCategoryByIds(purchaseCategoryIds);
        if(CollectionUtils.isNotEmpty(purchaseCategoryList)) {
            Map<Long, PurchaseCategory> purchaseCategoryMap = purchaseCategoryList.stream().collect(Collectors.toMap(k->k.getCategoryId(), Function.identity(), (k1, k2)->k2));
            return purchaseCategoryMap;
        }
        return new HashMap<>(50);
    }

    /**
     * 技术得分	综合得分	是否中标	"审批定标金额（万元）"	"中标通知金额（万元）"
     * @param data
     * @param caNoticeMap
     * @param bidOrderInfoMap
     */
    private void fillDataWithCaAndNotice(SuperviseReportDto data, Map<String, Object> caNoticeMap, Map<String, Object> bidOrderInfoMap) {
        Map<Long, CaDTO> caMap = (Map<Long, CaDTO>) caNoticeMap.get(CA_NOTICE_KEY_CADTO);
        if(MapUtils.isEmpty(caMap) || !caMap.containsKey(data.getProjectId())) {
            return;
        }
        CaDTO caInfo = caMap.get(data.getProjectId());
        if(CollectionUtils.isEmpty(caInfo.getCaSuppliers()) || !caInfo.getCaSuppliers().stream().filter(s -> Long.compare(s.getVendorId(), data.getVendorId()) == 0).findAny().isPresent()) {
            return;
        }
        //技术得分	综合得分
        CaSupplierDTO supplier = caInfo.getCaSuppliers().stream().filter(s -> Long.compare(s.getVendorId(), data.getVendorId()) == 0).findAny().get();
        data.setTechSocre(supplier.getTechScore());
        data.setComprehensiveScore(supplier.getCompositeScore());

        Optional<CaSelectionResultDTO> selectionResultOptional = caInfo.getCaSelectionResults().stream().filter(s -> Long.compare(s.getVendorId(), data.getVendorId()) == 0).findAny();
        if(selectionResultOptional.isPresent()) {
            CaSelectionResultDTO selectionResult = selectionResultOptional.get();
            data.setIsWin(selectionResult.getIsWin());
            //中标范围
            formateItemDesc(data, bidOrderInfoMap, selectionResult.getWinRange());
        }

        /** 完成月份取最早的中落标审批通过时间 */
        Map<Long, BidNoticeDTO> firstPassNoticeMap = (Map<Long, BidNoticeDTO>) caNoticeMap.get(StringUtils.joinWith(SrmConstant.UNDER_LINE, CA_NOTICE_KEY_NOTICEDTO, ExtSouBidComponent.fieldName(BidNoticeDTO::getPassTime)));
        if(MapUtils.isNotEmpty(firstPassNoticeMap) && firstPassNoticeMap.containsKey(data.getProjectId())) {
            BidNoticeDTO noticeDto = firstPassNoticeMap.get(data.getProjectId());
            data.setCompleteMonth(DateUtil.format(noticeDto.getPassTime(), DateUtil.DATE_FORMAT_7));
        }

        Map<Long, BidNoticeDTO> noticeMap = (Map<Long, BidNoticeDTO>) caNoticeMap.get(CA_NOTICE_KEY_NOTICEDTO);
        if(MapUtils.isNotEmpty(noticeMap) && noticeMap.containsKey(data.getProjectId())) {
            BidNoticeDTO noticeInfo = noticeMap.get(data.getProjectId());
            if(CollectionUtils.isNotEmpty(noticeInfo.getBidNoticeDetails())) {
                Optional<BidNoticeDetailDTO> noticeDetailOptional = noticeInfo.getBidNoticeDetails().stream().filter(noticeDetail -> Long.compare(noticeDetail.getVendorId(), data.getVendorId()) == 0).findAny();
                if(noticeDetailOptional.isPresent()) {
                    /** 中标通知金额（万元） */
                    data.setNoticePrice(noticeDetailOptional.get().getWinAmount());
                }
            }
        }


    }

    private void formateItemDesc(SuperviseReportDto data, Map<String, Object> bidOrderInfoMap, String winRange) {

        if(StringUtils.isBlank(winRange)) {
            return;
        }
        winRange = winRange.replaceAll(SrmConstant.SIG_3, SrmConstant.SIG_1);
        String[] winRangeArrarys = winRange.split(SrmConstant.SIG_1);
        Map<Long, List<ApiExtSouOrderItemDto>> orderItemGroup = (Map<Long, List<ApiExtSouOrderItemDto>>) bidOrderInfoMap.get(ORDER_INFO_KEY_ORDER_ITEM);
        if(MapUtils.isEmpty(orderItemGroup) || !orderItemGroup.containsKey(data.getProjectId())) {
            return;
        }
        Map<Long, ExtSouItem> itemMap = (Map<Long, ExtSouItem>) bidOrderInfoMap.get(ORDER_INFO_KEY_ITEM);
        if(MapUtils.isEmpty(itemMap)) {
            return;
        }

        List<String> itemDescList = new ArrayList<>(winRangeArrarys.length);
        List<ApiExtSouOrderItemDto> orderItemDtoList = orderItemGroup.get(data.getProjectId());
        /** 取最大轮次 */
        AtomicReference<Integer> maxRound = new AtomicReference<>(SrmConstant.NUM_ONE);
        List<Long> souItemIdList = new ArrayList<>(50);

        orderItemDtoList.stream().filter(orderItemDto -> Long.compare(orderItemDto.getVendorId(), data.getVendorId()) == 0).forEach(orderItemDto -> {
            maxRound.set(Math.max(maxRound.get(), ObjectUtils.defaultIfNull(orderItemDto.getRound(), SrmConstant.NUM_ONE)));
            if(!souItemIdList.contains(orderItemDto.getSouItemId())) {
                souItemIdList.add(orderItemDto.getSouItemId());
            }
        });

        Map<String, ExtSouItem> itemDescMap = new HashMap<>(50);
        Map<String, List<ExtSouItem>> packageNameMap = new HashMap<>(50);

        souItemIdList.stream().filter(souItemId -> itemMap.containsKey(souItemId)).forEach(souItemId -> {
            ExtSouItem souItem = itemMap.get(souItemId);
            itemDescMap.put(souItem.getItemDesc(), souItem);
            if(StringUtils.isNotBlank(souItem.getExtPackageName())) {
                if(!packageNameMap.containsKey(souItem.getExtPackageName())) {
                    packageNameMap.put(souItem.getExtPackageName(), new ArrayList<>());
                }
                packageNameMap.get(souItem.getExtPackageName()).add(souItem);
            }
        });

        /** 中标范围 */
        List<Long> winSouItemIdList = new ArrayList<>(50);

        for(String range : winRangeArrarys) {
            if(itemDescMap.containsKey(range)) {
                itemDescList.add(itemDescMap.get(range).getItemDesc());
                winSouItemIdList.add(itemDescMap.get(range).getSouItemId());
            } else if(packageNameMap.containsKey(range)) {
                packageNameMap.get(range).stream().forEach(item -> {
                    itemDescList.add(item.getItemDesc());
                    winSouItemIdList.add(item.getSouItemId());
                });
            }
        }

        /*if(CollectionUtils.isNotEmpty(itemDescList)) {
            data.setItemDesc(itemDescList.stream().distinct().collect(Collectors.joining(SrmConstant.SIG_3)));
        }*/

        /** 中标金额 */
        AtomicReference<BigDecimal> winPriceTax = new AtomicReference<>(BigDecimal.ZERO);
        orderItemDtoList.stream().filter(orderItemDto -> Long.compare(orderItemDto.getVendorId(), data.getVendorId()) == 0).filter(orderItemDto -> winSouItemIdList.contains(orderItemDto.getSouItemId()) && Integer.compare(maxRound.get(), ObjectUtils.defaultIfNull(orderItemDto.getRound(), SrmConstant.NUM_ONE)) == 0).forEach(orderItemDto -> {
            winPriceTax.set(winPriceTax.get().add(ObjectUtils.defaultIfNull(orderItemDto.getExtPriceSumTax(), BigDecimal.ZERO)));
        });
        if(BigDecimal.ZERO.compareTo(winPriceTax.get()) != 0) {
            data.setCaPrice(winPriceTax.get());
        }
    }

    /**
     * 技术得分	综合得分	是否中标	"审批定标金额（万元）"	"中标通知金额（万元）"
     * @param projectIdList
     * @return
     */
    private Map<String, Object> queryCaAndNotice(List<Long> projectIdList) {
        Map<String, Object> caNoticeMap = new HashMap<>(50);

        /** 定标申请 */
        List<CaDTO> caList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.Ca.getCode()).in(CaDTO::getProjectId, projectIdList).eq(CaDTO::getType, CaTypeEnum.APPLY.getCode()).eq(CaDTO::getStatus, CaStatusEnum.APPROVED.getCode()), CaDTO.class);
        if(CollectionUtils.isNotEmpty(caList)) {
            //定标明细
            List<CaSupplierDTO> caSupplierList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSupplier.getCode()).in(CaSupplierDTO::getCaId, caList.stream().map(CaDTO::getCaId).collect(Collectors.toList())), CaSupplierDTO.class);
            List<CaSelectionResultDTO> caSelectionResultList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.CaSelectionResult.getCode()).in(CaSelectionResultDTO::getCaId, caList.stream().map(CaDTO::getCaId).collect(Collectors.toList())), CaSelectionResultDTO.class);

            Map<Long, List<CaSupplierDTO>> caSupplierMap = caSupplierList.stream().collect(Collectors.groupingBy(CaSupplierDTO::getCaId));
            Map<Long, List<CaSelectionResultDTO>> caSelectionResultMap = caSelectionResultList.stream().collect(Collectors.groupingBy(CaSelectionResultDTO::getCaId));

            caList.stream().forEach(ca -> {
                ca.setCaSuppliers(caSupplierMap.get(ca.getCaId()));
                ca.setCaSelectionResults(caSelectionResultMap.get(ca.getCaId()));
            });

            Map<Long, CaDTO> caMap = caList.stream().collect(Collectors.toMap(CaDTO::getProjectId, Function.identity(), (k1, k2)->k2));
            caNoticeMap.put(CA_NOTICE_KEY_CADTO, caMap);
        }

        /** 中落标 */
        List<BidNoticeDTO> noticeList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode())
                .in(BidNoticeDTO::getProjectId, projectIdList).eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode())
                .orderByAsc(BidNoticeDTO::getCreationDate), BidNoticeDTO.class);
        /** 取最早的中标通知审批时间 */
        Map<Long, BidNoticeDTO> firstPassNoticeMap = new HashMap<>(16);
        noticeList.stream().filter(n -> !Objects.isNull(n.getPassTime())).forEach(n -> {
            if(!firstPassNoticeMap.containsKey(n.getProjectId())) {
                firstPassNoticeMap.put(n.getProjectId(), n);
            }
        });
        /** 缓存最早的中标通知审批时间 */
        caNoticeMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, CA_NOTICE_KEY_NOTICEDTO, ExtSouBidComponent.fieldName(BidNoticeDTO::getPassTime)), firstPassNoticeMap);

        /** 取审批通过的数据 */
        noticeList = noticeList.stream().filter(n -> BidNoticeStatusEnum.APPROVED.getCode().equals(n.getStatus())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(noticeList)) {
            //中落标明细
            List<BidNoticeDetailDTO> noticeDetaiList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode()).in(BidNoticeDetailDTO::getBidNoticeId, noticeList.stream().map(BidNoticeDTO::getBidNoticeId).collect(Collectors.toList())), BidNoticeDetailDTO.class);

            Map<Long, List<BidNoticeDetailDTO>> noticeDetaiMap = noticeDetaiList.stream().collect(Collectors.groupingBy(BidNoticeDetailDTO::getBidNoticeId));

            noticeList.stream().forEach(notice -> {
                notice.setBidNoticeDetails(noticeDetaiMap.get(notice.getBidNoticeId()));
            });

            Map<Long, BidNoticeDTO> noticeMap = noticeList.stream().collect(Collectors.toMap(BidNoticeDTO::getProjectId, Function.identity(), (k1, k2)->k2));
            caNoticeMap.put(CA_NOTICE_KEY_NOTICEDTO, noticeMap);
        }
        return caNoticeMap;
    }

    /**
     * 第一轮供应商报价总价（含税）	第二轮供应商报价总价（含税）	第三轮供应商报价总价（含税）
     * @param data
     * @param bidOrderInfoMap
     */
    private void fillDataWithOrderInfo(SuperviseReportDto data, Map<String, Object> bidOrderInfoMap) {
        Map<Long, List<ApiExtSouOrderItemDto>> orderItemGroup = (Map<Long, List<ApiExtSouOrderItemDto>>) bidOrderInfoMap.get(ORDER_INFO_KEY_ORDER_ITEM);
        if(MapUtils.isNotEmpty(orderItemGroup) && orderItemGroup.containsKey(data.getProjectId())) {
            List<ApiExtSouOrderItemDto> orderItemDtoList = orderItemGroup.get(data.getProjectId());
            final BigDecimal[] quotedPriceWithTaxFirst = {BigDecimal.ZERO};
            final BigDecimal[] quotedPriceWithTaxSecond = {BigDecimal.ZERO};
            final BigDecimal[] quotedPriceWithTaxThird = {BigDecimal.ZERO};
            final BigDecimal[] quotedPriceWithTaxFour = {BigDecimal.ZERO};
            final BigDecimal[] quotedPriceWithTaxFive = {BigDecimal.ZERO};
            final BigDecimal[] quotedPriceWithTaxSix = {BigDecimal.ZERO};
            final BigDecimal[] quotedPriceWithTaxSeven = {BigDecimal.ZERO};
            final BigDecimal[] quotedPriceWithTaxEight = {BigDecimal.ZERO};

            orderItemDtoList.stream().filter(orderItemDto -> Long.compare(orderItemDto.getVendorId(), data.getVendorId()) == 0).forEach(orderItem -> {
                if(Integer.compare(SrmConstant.NUM_ONE, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxFirst[0] = quotedPriceWithTaxFirst[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
                if(Integer.compare(SrmConstant.NUM_TWO, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxSecond[0] = quotedPriceWithTaxSecond[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
                if(Integer.compare(SrmConstant.NUM_THREE, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxThird[0] = quotedPriceWithTaxThird[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
                if(Integer.compare(SrmConstant.NUM_FOUR, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxFour[0] = quotedPriceWithTaxFour[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
                if(Integer.compare(SrmConstant.NUM_FIVE, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxFive[0] = quotedPriceWithTaxFive[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
                if(Integer.compare(SrmConstant.NUM_SIX, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxSix[0] = quotedPriceWithTaxSix[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
                if(Integer.compare(SrmConstant.NUM_SEVEN, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxSeven[0] = quotedPriceWithTaxSeven[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
                if(Integer.compare(SrmConstant.NUM_EIGHT, ObjectUtils.defaultIfNull(orderItem.getRound(), 1)) == 0) {
                    quotedPriceWithTaxEight[0] = quotedPriceWithTaxEight[0].add(ObjectUtils.defaultIfNull(orderItem.getExtPriceSumTax(), BigDecimal.ZERO));
                }
            });

            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxFirst[0]) != 0) {
                data.setQuotedPriceWithTaxFirst(quotedPriceWithTaxFirst[0]);
            }
            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxSecond[0]) != 0) {
                data.setQuotedPriceWithTaxSecond(quotedPriceWithTaxSecond[0]);
            }
            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxThird[0]) != 0) {
                data.setQuotedPriceWithTaxThird(quotedPriceWithTaxThird[0]);
            }
            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxFour[0]) != 0) {
                data.setQuotedPriceWithTaxFour(quotedPriceWithTaxFour[0]);
            }
            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxFive[0]) != 0) {
                data.setQuotedPriceWithTaxFive(quotedPriceWithTaxFive[0]);
            }
            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxSix[0]) != 0) {
                data.setQuotedPriceWithTaxSix(quotedPriceWithTaxSix[0]);
            }
            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxSeven[0]) != 0) {
                data.setQuotedPriceWithTaxSeven(quotedPriceWithTaxSeven[0]);
            }
            if(BigDecimal.ZERO.compareTo(quotedPriceWithTaxEight[0]) != 0) {
                data.setQuotedPriceWithTaxEight(quotedPriceWithTaxEight[0]);
            }
        }
    }


    /**
     * 查询招标单据信息
     * @param projectIdList
     * @return
     */
    private Map<String, Object> queryOrderInfo(List<Long> projectIdList) {
        Map<String, Object> bidOrderInfoMap = new HashMap<>(50);

        List<Long> templateProjectIdList = new ArrayList<>(projectIdList);
        templateProjectIdList.add(SrmConstant.LONG_MINUS_ONE);

        /** 查询模板字段 'extQuantity', 'extPriceTax', 'extFixedPriceTax', 'requireQuantity' */
        List<String> colCodeList = Arrays.asList(ExtSouBidComponent.fieldName(ExtSouItem::getExtQuantity), ExtSouBidComponent.fieldName(ExtSouItem::getExtPriceTax),
                ExtSouBidComponent.fieldName(ExtSouItem::getExtFixedPriceTax), ExtSouBidComponent.fieldName(ExtSouItem::getRequireQuantity));

        /** 查询报价模板 */
        List<ExtSouPriceTemplate> priceTemplateList = priceTemplateService.lambdaQuery().in(ExtSouPriceTemplate::getProjectId, templateProjectIdList).in(ExtSouPriceTemplate::getColumnCode, colCodeList).list();
        Map<Long, List<ExtSouPriceTemplate>> priceTemplateMap = priceTemplateList.stream().collect(Collectors.groupingBy(ExtSouPriceTemplate::getProjectId));

        /** 查询报价信息 */
        List<ExtSouItem> itemList = itemService.lambdaQuery().in(ExtSouItem::getProjectId, projectIdList).list();
        Map<Long, ExtSouItem> itemMap = itemList.stream().collect(Collectors.toMap(ExtSouItem::getSouItemId, Function.identity(), (k1, k2)->k2));
        bidOrderInfoMap.put(ORDER_INFO_KEY_ITEM, itemMap);

        /** 查询投标报价信息 */
        List<ExtSouOrderItem> orderItemList = orderItemService.lambdaQuery().in(ExtSouOrderItem::getProjectId, projectIdList).in(ExtSouOrderItem::getOrderStatus, Arrays.asList(SouOrderStatusEnum.SUBMISSION.name(), SouOrderStatusEnum.CANCEL.name())).list();

        if(CollectionUtils.isNotEmpty(orderItemList)) {
            List<ApiExtSouOrderItemDto> orderItemDtoList = JSON.parseArray(JSON.toJSONString(orderItemList), ApiExtSouOrderItemDto.class);
            orderItemDtoList.stream().forEach(orderItemDto -> {
                /** 转换报价模板字段 */
                orderItemDto.coverItemFields();
                /** 转换汇率 */
                orderItemDto.convertExchangeRateAsItemFields();
                BigDecimal priceTax = priceTax(orderItemDto, priceTemplateMap);
                BigDecimal extQuantity = extQuantity(itemMap.getOrDefault(orderItemDto.getSouItemId(), new ExtSouItem()), priceTemplateMap);
                //总价
                BigDecimal totalPriceTax = null;
                if(ObjectUtils.allNotNull(priceTax, extQuantity)) {
                    totalPriceTax = priceTax.multiply(extQuantity);
                }
                orderItemDto.setExtPriceTax(priceTax);
                orderItemDto.setExtPriceSumTax(totalPriceTax);
                orderItemDto.setExtQuantity(extQuantity);
            });
            Map<Long, List<ApiExtSouOrderItemDto>> orderItemGroup = orderItemDtoList.stream().collect(Collectors.groupingBy(ApiExtSouOrderItemDto::getProjectId));
            bidOrderInfoMap.put(ORDER_INFO_KEY_ORDER_ITEM, orderItemGroup);
        }

        return bidOrderInfoMap;
    }

    /**
     * 获取数量
     * @param item
     * @param tempateMap
     * @return
     */
    private BigDecimal extQuantity(ExtSouItem item, Map<Long, List<ExtSouPriceTemplate>> tempateMap) {
        if(tempateMap.containsKey(item.getProjectId())) {
            if(tempateMap.get(item.getProjectId()).stream().filter(t -> ExtSouBidComponent.fieldName(ExtSouItem::getExtQuantity).equals(t.getColumnCode())).findAny().isPresent()) {
                return item.getExtQuantity();
            } else {
                return item.getRequireQuantity();
            }
        } else {
            if(tempateMap.getOrDefault(SrmConstant.LONG_MINUS_ONE, new ArrayList<>(NUM50)).stream().filter(t -> YesOrNo.YES.getValue().equals(t.getColumnDefault())).filter(t -> ExtSouBidComponent.fieldName(ExtSouItem::getExtQuantity).equals(t.getColumnCode())).findAny().isPresent()) {
                return item.getExtQuantity();
            } else {
                return item.getRequireQuantity();
            }
        }
    }

    /**
     * 获取含税单价
     * @param orderItemDto
     * @param tempateMap
     * @return
     */
    private BigDecimal priceTax(ApiExtSouOrderItemDto orderItemDto, Map<Long, List<ExtSouPriceTemplate>> tempateMap) {
        if(tempateMap.containsKey(orderItemDto.getProjectId())) {
            if(tempateMap.get(orderItemDto.getProjectId()).stream().filter(t -> ExtSouBidComponent.fieldName(ExtSouItem::getExtPriceTax).equals(t.getColumnCode())).findAny().isPresent()) {
                return orderItemDto.getExtPriceTax();
            } else {
                return orderItemDto.getExtFixedPriceTax();
            }
        } else {
            if(tempateMap.getOrDefault(SrmConstant.LONG_MINUS_ONE, new ArrayList<>(NUM50)).stream().filter(t -> YesOrNo.YES.getValue().equals(t.getColumnDefault())).filter(t -> ExtSouBidComponent.fieldName(ExtSouItem::getExtPriceTax).equals(t.getColumnCode())).findAny().isPresent()) {
                return orderItemDto.getExtPriceTax();
            } else {
                return orderItemDto.getExtFixedPriceTax();
            }
        }
    }

    /**
     * 填充采购申请相关信息
     * @param data
     * @param reuirementInfoMap
     */
    private void fillDataWithRequirementInfo(SuperviseReportDto data, Map<String, Object> reuirementInfoMap) {
        /** 预算金额 */
        data.setTotalBudget(data.getExtBudget());
        /** 标的物 */
        data.setItemDesc(data.getExtCategoryName());
        /** 招标单号拆分 */
        String[] projectNoArray = NpmSouBidProjectNoUtils.resolveProjectNo(data.getExtProjectNo());
        data.setCompanyShortCode(projectNoArray[0]);
        data.setYear(projectNoArray[1]);
        data.setMonth(projectNoArray[2]);
        /** 供应商负责人 */
        Map<Long, List<ExtSouDemand>> demandMap = (Map<Long, List<ExtSouDemand>>)reuirementInfoMap.getOrDefault(REUIREMENT_INFO_KEY_DEMAND, new HashMap<>(50));
        if(demandMap.containsKey(data.getProjectId())) {
            List<ExtSouDemand> demandList = demandMap.get(data.getProjectId());
            Map<Long, BidDataSubmit> dataSubmitMaxMap = (Map<Long, BidDataSubmit>) reuirementInfoMap.get(REUIREMENT_INFO_KEY_SUBMIT_MAX);

            String requirementHeadNum = demandList.get(0).getApplicantNo();
            if(MapUtils.isNotEmpty(dataSubmitMaxMap)) {
                requirementHeadNum = dataSubmitMaxMap.get(data.getProjectId()).getRequirementHeadNum();
            }

            String vendorPrincialKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, requirementHeadNum, ExtPrRequirementGroupTypeEnum.VENDOR.getCode());

            Map<String, RecordDTO> extRequirementGroupMap = (Map<String, RecordDTO>) reuirementInfoMap.get(REUIREMENT_INFO_KEY_GROUP);
            if(MapUtils.isNotEmpty(extRequirementGroupMap) && extRequirementGroupMap.containsKey(vendorPrincialKey)) {
                /** 供应商负责人-赋值 */
                data.setVendorPrincipal(extRequirementGroupMap.get(vendorPrincialKey).get(ExtPrSouRequirementGroup::getFullName));
            }

            /** 计划类型 */
            Map<String, RecordDTO> extRequirementHeadNumMap = (Map<String, RecordDTO>) reuirementInfoMap.get(REUIREMENT_INFO_KEY_EXT_REQ);
            if(MapUtils.isNotEmpty(extRequirementHeadNumMap)) {
                RecordDTO extRequirementHead = extRequirementHeadNumMap.get(requirementHeadNum);
                if(ObjectUtils.allNotNull(extRequirementHead)) {
                    data.setRequirementPlanType(extRequirementHead.get(ExtPrSouRequirementHead::getRequireFrom));
                }
            }
        }

        /** 招标负责人 */
        data.setSouPrincipal(data.getCreatedFullName());

        /** 评标组长 */
        Map<String, ExtSouGroup> groupMap = (Map<String, ExtSouGroup>) reuirementInfoMap.get(REUIREMENT_INFO_KEY_BID_GROUP);
        String leaderGroupRoleKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, data.getProjectId(), ExtSouGroupRoleEnum.LEADER.getCode());
        if(MapUtils.isNotEmpty(groupMap) && groupMap.containsKey(leaderGroupRoleKey)) {
            data.setLeaderPrincipal(groupMap.get(leaderGroupRoleKey).getFullName());
        }

    }

    /**
     * 查询招标工作小组
     * @param projectIdList
     * @param reuirementInfoMap
     */
    protected void queryBidGroup(List<Long> projectIdList, Map<String, Object> reuirementInfoMap) {
        List<ExtSouGroup> groupList = groupService.lambdaQuery().in(ExtSouGroup::getProjectId, projectIdList).eq(ExtSouGroup::getExtGroupFlag, YesOrNo.YES.getValue()).list();
        Map<String, ExtSouGroup> groupMap = groupList.stream().collect(Collectors.toMap(k -> StringUtils.joinWith(SrmConstant.UNDER_LINE, k.getProjectId(), k.getGroupRole()), Function.identity(), (k1, k2)->k2));
        reuirementInfoMap.put(REUIREMENT_INFO_KEY_BID_GROUP, groupMap);
    }

    /**
     * 查询招标资料递交
     * @param reuirementInfoMap
     * @return
     */
    protected void queryDataSubmit(Map<String, Object> reuirementInfoMap) {
        Map<String, RecordDTO> requirementHeadNumMap = (Map<String, RecordDTO>)reuirementInfoMap.get(REUIREMENT_INFO_KEY_REQ);
        if(MapUtils.isEmpty(requirementHeadNumMap)) {
            return;
        }
        /** 查询招标资料提交单据 */
        List<BidDataSubmit> dataSubmitList = qlService.queryByWrapper(QlWrappers.query(MqlType.SUBMIT_BUYER).in(BidDataSubmit::getRequirementHeadNum, new ArrayList<>(requirementHeadNumMap.keySet())).orderByDesc(BidDataSubmit::getTotalBudget), BidDataSubmit.class);

        if(CollectionUtils.isEmpty(dataSubmitList)) {
            return;
        }

        Map<String, BidDataSubmit> dataSubmitMap = dataSubmitList.stream().collect(Collectors.toMap(BidDataSubmit::getRequirementHeadNum, Function.identity(), (k1,k2)->k2));
        reuirementInfoMap.put(REUIREMENT_INFO_KEY_SUBMIT, dataSubmitMap);

        Map<Long, List<ExtSouDemand>> demandMap = (Map<Long, List<ExtSouDemand>>)reuirementInfoMap.getOrDefault(REUIREMENT_INFO_KEY_DEMAND, new HashMap<>(50));

        /** 预算金额最大的招标资料提交单据 */
        Map<Long, BidDataSubmit> dataSubmitMaxMap = new HashMap<>(50);
        for(Long projectId : demandMap.keySet()) {
            List<String> requirementHeadNumList = demandMap.get(projectId).stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.toList());
            Optional<BidDataSubmit> dataSubmitOptional = dataSubmitList.stream().filter(data -> requirementHeadNumList.contains(data.getRequirementHeadNum())).findFirst();
            if(dataSubmitOptional.isPresent()) {
                dataSubmitMaxMap.put(projectId, dataSubmitOptional.get());
            }
        }

        reuirementInfoMap.put(REUIREMENT_INFO_KEY_SUBMIT_MAX, dataSubmitMaxMap);
    }

    /**
     * 查询采购申请相关信息
     * @param projectIdList
     * @return
     */
    protected Map<String, Object> queryRequirementInfo(List<Long> projectIdList) {
        Map<String, Object> reuirementInfoMap = new HashMap<>(50);

        /** 查询关联的采购申请单信息 */
        List<ExtSouDemand> demandList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SOU_DEMAND).in(ExtSouDemand::getProjectId, projectIdList).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO), ExtSouDemand.class);

        if(CollectionUtils.isEmpty(demandList)) {
            return reuirementInfoMap;
        }

        Map<Long, List<ExtSouDemand>> demandMap = demandList.stream().collect(Collectors.groupingBy(ExtSouDemand::getProjectId));
        reuirementInfoMap.put(REUIREMENT_INFO_KEY_DEMAND, demandMap);

        /** 采购申请单号 */
        List<String> requirementHeadNumList = demandList.stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.toList());

        /** 查询采购申请单 */
        List<RecordDTO> requirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD).in(RequirementHead::getRequirementHeadNum, requirementHeadNumList));

        /** 查询采购申请单扩展表 以及 责任人*/
        List<RecordDTO> extRequirementHeadList = new ArrayList<>(50);
        List<RecordDTO> requirementGroupList = new ArrayList<>(50);
        Map<String, RecordDTO> requirementHeadNumMap = new HashMap<>(50);
        Map<Long, RecordDTO> requirementHeadIdMap = new HashMap<>(50);

        if(CollectionUtils.isNotEmpty(requirementHeadList)) {
            List<Long> requirementHeadIdList = requirementHeadList.stream().map(r -> r.get(RequirementHead::getRequirementHeadId)).collect(Collectors.toList());
            extRequirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getRequirementHeadId, requirementHeadIdList));
            requirementGroupList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_GROUP).in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadIdList));

            requirementHeadNumMap = requirementHeadList.stream().collect(Collectors.toMap(r -> r.get(RequirementHead::getRequirementHeadNum), Function.identity(), (k1, k2)->k2));
            reuirementInfoMap.put(REUIREMENT_INFO_KEY_REQ, requirementHeadNumMap);
            requirementHeadIdMap = requirementHeadList.stream().collect(Collectors.toMap(r -> r.get(RequirementHead::getRequirementHeadId), Function.identity(), (k1, k2)->k2));
        }
        Map<Long, RecordDTO> finalRequirementHeadIdMap = requirementHeadIdMap;
        if(CollectionUtils.isNotEmpty(extRequirementHeadList)) {
            Map<String, RecordDTO> extRequirementHeadNumMap = extRequirementHeadList.stream().collect(Collectors.toMap(r -> finalRequirementHeadIdMap.get(r.get(ExtPrSouRequirementHead::getRequirementHeadId)).get(RequirementHead::getRequirementHeadNum), Function.identity(), (k1, k2)->k2));
            reuirementInfoMap.put(REUIREMENT_INFO_KEY_EXT_REQ, extRequirementHeadNumMap);
        }
        if(CollectionUtils.isNotEmpty(requirementGroupList)) {
            /** 单号 + 类型 唯一 */
            Map<String, RecordDTO> extRequirementGroupMap = requirementGroupList.stream().collect(Collectors.toMap(r -> StringUtils.joinWith(SrmConstant.UNDER_LINE, finalRequirementHeadIdMap.get(r.get(ExtPrSouRequirementGroup::getRequirementHeadId)).get(RequirementHead::getRequirementHeadNum), r.get(ExtPrSouRequirementGroup::getGroupType)), Function.identity(), (k1, k2)->k2));
            reuirementInfoMap.put(REUIREMENT_INFO_KEY_GROUP, extRequirementGroupMap);
        }

        /** 查询招标资料提交信息 */
        queryDataSubmit(reuirementInfoMap);

        /** 查询招标工作小组*/
        queryBidGroup(projectIdList, reuirementInfoMap);

        return reuirementInfoMap;
    }
    private void getDictName(Map<String, DictItemDTO> dictCodeMap, SuperviseReportDto va) {
        if(StringUtils.isNotBlank(va.getExtVendorAttr())){
            String dictName="";
            String[] extVendorAttrArr = va.getExtVendorAttr().split(";");
            for(String extVendorAttr : extVendorAttrArr){
                //获取字典
                if (dictCodeMap.get(extVendorAttr)!=null) {
                    //最后一项不需要分号结尾
                    if(extVendorAttr.equals(extVendorAttrArr[extVendorAttrArr.length-1])){
                        dictName += dictCodeMap.get(extVendorAttr).getDictItemName()+"";
                    }else{
                        dictName += dictCodeMap.get(extVendorAttr).getDictItemName()+";";
                    }

                }
            }
            va.setExtVendorAttrName(dictName);
        }else{
            va.setExtVendorAttrName("");
        }
    }
}
