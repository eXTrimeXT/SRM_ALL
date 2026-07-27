package com.midea.cloud.srm.sou.report.bid.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.pagehelper.PageInfo;
import com.google.common.base.CaseFormat;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BeanMapUtils;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.perf.enums.PerformanceCodeEnum;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerStatusEnum;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.bidnotices.enums.BidNoticeStatusEnum;
import com.midea.cloud.srm.model.sou.ca.enums.CaTypeEnum;
import com.midea.cloud.srm.model.sou.enums.*;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectExtendDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.report.bid.dto.ScheduleReportDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.ScheduleReportQueryDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportQueryDto;
import com.midea.cloud.srm.model.sou.report.souschedules.dto.SccNpmSouScheduleReportDto;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.report.bid.mapper.ExtReportBidScheduleMapper;
import com.midea.cloud.srm.sou.report.bid.service.ExtReportBidScheduleService;
import com.midea.cloud.srm.sou.report.utils.ReportUtils;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * <pre>
 *
 * </pre>
 *
 * @author panmq
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/01/19 14:54:30
 *  修改内容:
 * </pre>
 */
@Api("招标报表-进度表-实现类")
@Service
@Slf4j
public class ExtReportBidScheduleServiceImpl implements ExtReportBidScheduleService<ScheduleReportDto> {

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouOrderService iExtSouOrderService;

    @Autowired
    private IExtNpmSouOrderService iExtNpmSouOrderService;

    @Autowired
    private ExtReportBidScheduleMapper extReportBidScheduleMapper;

    /**
     * 中标供应商
     */
    private static final String BID_NOTICE_WIN_VENDOR = "bidNoticeWinVendor";

    /**
     * 中标供应商联系人
     */
    private static final String BID_NOTICE_WIN_VENDOR_LINK_MAN = "bidNoticeWinVendorLinkMan";

    /**
     * 中标供应商联系电话
     */
    private static final String BID_NOTICE_WIN_VENDOR_PHONE = "bidNoticeWinVendorPhone";

    /**
     * 中标供应商中标通知金额（万元）
     */
    private static final String BID_NOTICE_WIN_VENDOR_AMOUNT = "bidNoticeWinVendorAmount";


    @Override
    public PageInfo<ScheduleReportDto> listPage(Map<String, Object> query) {
        PageUtil.startPage(MapUtils.getInteger(query, ExtSouBidComponent.fieldName(ExtSouProject::getPageNum), 1), MapUtils.getInteger(query, ExtSouBidComponent.fieldName(ExtSouProject::getPageSize), 15));
        ScheduleReportQueryDto queryDto = BeanMapUtils.mapToBean(query, new ScheduleReportQueryDto());
        List<SccNpmSouScheduleReportDto> projectList = extReportBidScheduleMapper.listProjectPage(queryDto);
        PageInfo pageInfo = new PageInfo(projectList);
        /** 对象转换 */
        List<ScheduleReportDto> dataList = convertBean(projectList);
        /** 填充报表数据 */
        fillReportData(dataList);

        pageInfo.setList(dataList);
        return pageInfo;
    }

    /**
     * 报表实体类转换
     * @param dataList
     * @return
     */
    private List<ScheduleReportDto> convertBean(List<SccNpmSouScheduleReportDto> dataList) {
        List<ScheduleReportDto> convertList = new ArrayList<>(50);
        if(CollectionUtils.isNotEmpty(dataList)) {
             dataList.stream().forEach(data -> {
                 ScheduleReportDto reportDto = convertScheduleReportDto(data);
                 convertList.add(reportDto);
             });
        }
        return convertList;
    }

    /**
     * 实体类转换
     * @param reportDto
     * @return
     */
    private ScheduleReportDto convertScheduleReportDto(SccNpmSouScheduleReportDto reportDto) {

        ScheduleReportDto scheduleReportDto = new ScheduleReportDto();

        reflectBean(scheduleReportDto, reportDto);

        scheduleReportDto.setCreationDate(reportDto.getBidCreationDate());
        scheduleReportDto.setTotalBudget(Objects.isNull(reportDto.getExtBudget())?SrmConstant.SHORT_LINE:reportDto.getExtBudget().stripTrailingZeros().toPlainString());

        return scheduleReportDto;
    }

    /**
     * 通过反射设置属性值
     * @param scheduleReportDto
     * @param reportDto
     */
    private void reflectBean(ScheduleReportDto scheduleReportDto, SccNpmSouScheduleReportDto reportDto) {
        Field[] fields = ScheduleReportDto.class.getDeclaredFields();
        Map<String, Field> sourceFieldMap = new HashMap<>(50);
        Map<String, Field> targetFieldMap = new HashMap<>(50);
        Arrays.stream(fields).forEach(targetField -> {
            Field sourceField = getField(SccNpmSouScheduleReportDto.class, targetField.getName(), false);
            if(!Objects.isNull(sourceField)) {
                sourceFieldMap.put(sourceField.getName(), sourceField);
                targetFieldMap.put(targetField.getName(), targetField);
            }
        });

        for(String fieldName : sourceFieldMap.keySet()) {
            Method sourceGet = ReflectionUtils.findMethod(SccNpmSouScheduleReportDto.class, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, StringUtils.joinWith(SrmConstant.UNDER_LINE, SrmConstant.REFLECTION_GET, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))));
            if(Objects.isNull(sourceGet)) {
                continue;
            }

            Object value = ReflectionUtils.invokeMethod(sourceGet, reportDto);

            Field targetField = targetFieldMap.get(fieldName);

            Class targetType = targetField.getType();

            Method targetSet = ReflectionUtils.findMethod(ScheduleReportDto.class, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, StringUtils.joinWith(SrmConstant.UNDER_LINE, SrmConstant.REFLECTION_SET, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName))), targetType);
            if(Objects.isNull(targetSet)) {
                continue;
            }

            if(Objects.isNull(value)) {
                if(targetType.getSimpleName().equals(String.class.getSimpleName())) {
                    ReflectionUtils.invokeMethod(targetSet, scheduleReportDto, SrmConstant.SHORT_LINE);
                }
            } else {
                if(targetType.getSimpleName().equals(value.getClass().getSimpleName())) {
                    ReflectionUtils.invokeMethod(targetSet, scheduleReportDto, value);
                } else {
                    //报表字符串转换
                    if(targetType.getSimpleName().equals(String.class.getSimpleName())) {
                        if(BigDecimal.class.getSimpleName().equals(value.getClass().getSimpleName())) {
                            BigDecimal bigDecimal = (BigDecimal) value;
                            String strValue = bigDecimal.stripTrailingZeros().toPlainString();
                            if(fieldName.endsWith(SrmConstant.SCHEDULE_FIELD_ENDWITH)) {
                                bigDecimal = bigDecimal.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                                strValue = StringUtils.joinWith("", bigDecimal.stripTrailingZeros().toPlainString(), SrmConstant.PER_CENT);
                            }
                            ReflectionUtils.invokeMethod(targetSet, scheduleReportDto, strValue);
                        } else if(Long.class.getSimpleName().equals(value.getClass().getSimpleName())) {
                            ReflectionUtils.invokeMethod(targetSet, scheduleReportDto, value.toString());
                        } else if(Date.class.getSimpleName().equals(value.getClass().getSimpleName())) {
                            ReflectionUtils.invokeMethod(targetSet, scheduleReportDto, DateUtil.format((Date)value, DateUtil.DATE_FORMAT_10));
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
    public void fillReportData(List<ScheduleReportDto> dataList) {

        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }

        Long batchId = IdGenrator.generate();
        log.info(MessageFormat.format("fillReportData填充项目进度报表数据批次{0} 开始", batchId));

        /** 项目统计 */
        log.info(MessageFormat.format("fillReportData填充项目进度报表数据批次{0} 数据预处理-查询中落标数据开始", batchId));
        Map<String, Object> winLossMap = handlerWinOrLoss(dataList.stream().map(d->d.getProjectId()).collect(Collectors.toList()));
        log.info(MessageFormat.format("fillReportData填充项目进度报表数据批次{0} 数据预处理-查询中落标数据结束", batchId));

        dataList.stream().forEach(data -> {
            /** 中标供应商	联系人	联系电话	中标通知金额（万元） */
            fillWinOrLoss(data, winLossMap);
        });
        log.info(MessageFormat.format("fillReportData填充项目进度报表数据批次{0} 结束", batchId));
    }

    /**
     * 中落标通知
     * "中标单位
     * (单位全称)"	联系人/联系方式	"审批定标金额
     * （万元）"	中标通知金额（万元）
     * @param projectIdList
     * @return
     */
    private Map<String, Object> handlerWinOrLoss(List<Long> projectIdList) {
        Map<String, Object> winLossMap = new HashMap<>(50);

        /** 查询招标单报价单 */
        List<ExtSouOrder> extorderList = iExtSouOrderService.lambdaQuery().in(ExtSouOrder::getProjectId, projectIdList).list();
        List<ExtNpmSouOrder> extNpmSouOrders = new ArrayList<>(50);
        if(CollectionUtils.isNotEmpty(extorderList)) {
            extNpmSouOrders = iExtNpmSouOrderService.lambdaQuery().in(ExtNpmSouOrder::getOrderId, extorderList.stream().map(o -> o.getOrderId()).collect(Collectors.toList())).list();
        }
        Map<Long, ExtSouOrder> extOrderMap = extorderList.stream().collect(Collectors.toMap(o -> o.getOrderId(), Function.identity(), (k1, k2)->k2));

        //已投商务标
        List<ExtSouOrder> busVendorList = extNpmSouOrders.stream().filter(e -> ExtOrderTypeEnum.BUS.getCode().equals(e.getExtOrderType())).filter(e -> SouOrderStatusEnum.SUBMISSION.name().equals(e.getOrderStatus())).map(e -> extOrderMap.get(e.getOrderId())).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtSouOrder::getOrderId))), ArrayList::new));



        List<BidNoticeDTO> bidNoticeList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode()).in(BidNoticeDTO::getProjectId, projectIdList).eq(BidNoticeDTO::getStatus, BidNoticeStatusEnum.APPROVED.getCode()).eq(BidNoticeDTO::getType, CaTypeEnum.APPLY.getCode()), BidNoticeDTO.class);
        List<BidNoticeDetailDTO> bidNoticeDetailList = new ArrayList<>(50);
        if(CollectionUtils.isNotEmpty(bidNoticeList)) {
            bidNoticeDetailList = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNoticeDetail.getCode()).in(BidNoticeDetailDTO::getBidNoticeId, bidNoticeList.stream().map(BidNoticeDTO::getBidNoticeId).collect(Collectors.toList())).eq(BidNoticeDetailDTO::getIsWin, YesOrNo.YES.getValue()), BidNoticeDetailDTO.class);
        }

        if(CollectionUtils.isNotEmpty(bidNoticeDetailList)) {
            Map<Long, BidNoticeDTO> noticeMap = bidNoticeList.stream().collect(Collectors.toMap(k->k.getBidNoticeId(), Function.identity(), (k1, k2) -> k2));
            Map<Long, List<ExtSouOrder>> busVendorGroup = busVendorList.stream().collect(Collectors.groupingBy(o -> o.getProjectId()));
            Map<Long, List<BidNoticeDetailDTO>> bidNoticeDetailGroup = bidNoticeDetailList.stream().collect(Collectors.groupingBy(d -> noticeMap.get(d.getBidNoticeId()).getProjectId()));
            for(Long projectId: bidNoticeDetailGroup.keySet()) {
                List<ExtSouOrder> orderList = busVendorGroup.getOrDefault(projectId, new ArrayList<>(50));
                Map<Long, ExtSouOrder> orderMap = orderList.stream().collect(Collectors.toMap(o -> o.getVendorId(), Function.identity(), (k1, k2) -> k2));
                List<BidNoticeDetailDTO> detailList = bidNoticeDetailGroup.get(projectId);
                
                /** 中标供应商	联系人	联系电话	中标通知金额（万元） */

                List<Map<String, Object>> winVendorInfoList = new ArrayList<>(50);

                detailList.stream().forEach(detail -> {
                    ExtSouOrder vendorOrder = orderMap.getOrDefault(detail.getVendorId(), new ExtSouOrder());

                    Map<String, Object> winData = new HashMap<>(15);
                    winData.put(BID_NOTICE_WIN_VENDOR, detail.getVendorName());
                    winData.put(BID_NOTICE_WIN_VENDOR_LINK_MAN, ObjectUtils.defaultIfNull(vendorOrder.getExtTenderName(), ""));
                    winData.put(BID_NOTICE_WIN_VENDOR_PHONE, ObjectUtils.defaultIfNull(vendorOrder.getExtTenderPhone(), ""));
                    winData.put(BID_NOTICE_WIN_VENDOR_AMOUNT, detail.getWinAmount());
                    winVendorInfoList.add(winData);
                });

                winLossMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, ExtSouBidComponent.fieldName(ScheduleReportDto::getWinVendorInfoList)), winVendorInfoList);

            }
        }

        return winLossMap;
    }

    /**
     * 填充项目统计信息 中标供应商	联系人	联系电话	中标通知金额（万元）
     * @param data
     * @param winLossMap
     */
    private void fillWinOrLoss(ScheduleReportDto data, Map<String, Object> winLossMap) {
        data.setWinVendorInfoList((List<Map<String, Object>>) winLossMap.getOrDefault(StringUtils.joinWith(SrmConstant.UNDER_LINE, data.getProjectId(), ExtSouBidComponent.fieldName(ScheduleReportDto::getWinVendorInfoList)), new ArrayList<>(50)));
    }
}
