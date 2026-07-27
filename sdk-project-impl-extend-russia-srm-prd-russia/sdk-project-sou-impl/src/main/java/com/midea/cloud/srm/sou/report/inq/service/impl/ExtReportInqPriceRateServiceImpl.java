package com.midea.cloud.srm.sou.report.inq.service.impl;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.report.inq.dto.ExtInquiryPriceRateDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.report.inq.mapper.ExtReportInqPriceRateMapper;
import com.midea.cloud.srm.sou.report.inq.service.ExtReportInqPriceRateService;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@Api("报价率报表-实现类")
@Slf4j
public class ExtReportInqPriceRateServiceImpl implements ExtReportInqPriceRateService<ExtInquiryPriceRateDto> {
    @Resource
    private ExtReportInqPriceRateMapper reportInqPriceRateMapper;

    private static final String CREATION_DATE_FROM = "creationDateFrom";

    private static final String CREATION_DATE_TO = "creationDateTo";

    private static final String DATA_LIST = "dataList";

    @Override
    public PageInfo<ExtInquiryPriceRateDto> listPage(Map<String, Object> query) {
        log.info("ExtReportInqPriceRateService 报价率报表开始...");
        /** 格式化日期 */
        formateDateAsQueryParam(query);
        /** 分页查询-底表数据 */
        PageUtil.startPage(MapUtils.getInteger(query, ExtSouBidComponent.fieldName(ExtSouProject::getPageNum), 1), MapUtils.getInteger(query, ExtSouBidComponent.fieldName(ExtSouProject::getPageSize), 15));
        List<ExtInquiryPriceRateDto> dataList = reportInqPriceRateMapper.listPriceRate(query);

        PageInfo<ExtInquiryPriceRateDto> pageInfo = new PageInfo<>(dataList);
        /** 填充报表数据 */
        extFillReportData(dataList, query);
        log.info("ExtReportInqPriceRateService 报价率报表结束...");
        return pageInfo;
    }

    /**
     * 格式化日期类
     * @param query
     */
    @SneakyThrows(value = {Exception.class})
    private void formateDateAsQueryParam(Map<String, Object> query) {
        if(query.containsKey(CREATION_DATE_FROM) && StringUtils.isNotBlank(MapUtils.getString(query, CREATION_DATE_FROM))) {
            Date date = DateUtil.parseDate(MapUtils.getString(query, CREATION_DATE_FROM));
            query.put(CREATION_DATE_FROM, DateUtil.format(date, DateUtil.DATE_FORMAT_10));
        }

        if(query.containsKey(CREATION_DATE_TO) && StringUtils.isNotBlank(MapUtils.getString(query, CREATION_DATE_TO))) {
            Date date = DateUtil.parseDate(MapUtils.getString(query, CREATION_DATE_TO));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
            query.put(CREATION_DATE_TO, DateUtil.format(date, DateUtil.DATE_FORMAT_10));
        }
    }

    @Override
    public void fillReportData(List<ExtInquiryPriceRateDto> dataList) {

    }

    /**
     * 填充数据
     * @param dataList
     * @param query
     */
    public void extFillReportData(List<ExtInquiryPriceRateDto> dataList, Map<String, Object> query) {
        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }
        query.put(DATA_LIST, dataList);
        log.info("ExtReportInqPriceRateService 报价率报表统计询价单次数开始...");
        /** 统计询价单次数 */
        List<ExtInquiryPriceRateDto> inqTimesDataList = reportInqPriceRateMapper.statisticsInqTimes(query);
        Map<String, ExtInquiryPriceRateDto> inqTimesMap = inqTimesDataList.stream().collect(Collectors.toMap(o -> StringUtils.joinWith(SrmConstant.UNDER_LINE, o.getVendorId(), o.getOrgOuId()), Function.identity(), (k1, k2)->k2));
        log.info("ExtReportInqPriceRateService 报价率报表统计询价单次数结束...");

        log.info("ExtReportInqPriceRateService 报价率报表统计报价单次数开始...");
        /** 统计报价单次数 */
        List<ExtInquiryPriceRateDto> orderTimesDataList = reportInqPriceRateMapper.statisticsOrderTimes(query);
        Map<String, ExtInquiryPriceRateDto> orderTimesMap = orderTimesDataList.stream().collect(Collectors.toMap(o -> StringUtils.joinWith(SrmConstant.UNDER_LINE, o.getVendorId(), o.getOrgOuId()), Function.identity(), (k1, k2)->k2));
        log.info("ExtReportInqPriceRateService 报价率报表统计报价单次数结束...");

        log.info("ExtReportInqPriceRateService 报价率报表统计报价物资开始...");
        /** 统计报价物资 */
        List<ExtInquiryPriceRateDto> itemTimesDataList = reportInqPriceRateMapper.statisticsItemTimes(query);
        Map<String, ExtInquiryPriceRateDto> itemTimesMap = itemTimesDataList.stream().collect(Collectors.toMap(o -> StringUtils.joinWith(SrmConstant.UNDER_LINE, o.getVendorId(), o.getOrgOuId()), Function.identity(), (k1, k2)->k2));
        log.info("ExtReportInqPriceRateService 报价率报表统计报价物资结束...");

        /** 计算报表 */
        dataList.stream().forEach(data -> {
            String dataKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, data.getVendorId(), data.getOrgOuId());
            if(inqTimesMap.containsKey(dataKey)) {
                data.setInqTimes(inqTimesMap.get(dataKey).getInqTimes());
            }
            if(orderTimesMap.containsKey(dataKey)) {
                data.setOrderTimes(orderTimesMap.get(dataKey).getOrderTimes());
            }
            if(itemTimesMap.containsKey(dataKey)) {
                data.setItemTimes(itemTimesMap.get(dataKey).getItemTimes());
            }
            /** 报价率 */
            if(ObjectUtils.allNotNull(data.getInqTimes()) && BigDecimal.ZERO.compareTo(data.getInqTimes()) != 0) {
                BigDecimal orderJoinRate = ObjectUtils.defaultIfNull(data.getOrderTimes(), BigDecimal.ZERO).divide(data.getInqTimes(), 4, RoundingMode.HALF_UP);
                orderJoinRate = new BigDecimal(100).multiply(orderJoinRate);
                data.setOrderJoinRate(StringUtils.joinWith("", orderJoinRate.stripTrailingZeros().toPlainString(), SrmConstant.PER_CENT));
            }
        });
    }
}
