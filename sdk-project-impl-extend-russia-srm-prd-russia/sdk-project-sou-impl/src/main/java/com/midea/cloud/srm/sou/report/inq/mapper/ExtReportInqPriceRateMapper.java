package com.midea.cloud.srm.sou.report.inq.mapper;

import com.midea.cloud.srm.model.sou.report.inq.dto.ExtInquiryPriceRateDto;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm 报价率报表-mapper接口
 *
 * @author srm
 * @date 2024-05-20
 */
public interface ExtReportInqPriceRateMapper {

    /**
     * 报价率报表底表查询
     * @param params
     * @return
     */
    public List<ExtInquiryPriceRateDto> listPriceRate(Map<String, Object> params);

    /**
     * 统计询价次数
     * @param params
     * @return
     */
    public List<ExtInquiryPriceRateDto> statisticsInqTimes(Map<String, Object> params);

    /**
     * 统计报价次数
     * @param params
     * @return
     */
    public List<ExtInquiryPriceRateDto> statisticsOrderTimes(Map<String, Object> params);

    /**
     * 统计物资次数
     * @param params
     * @return
     */
    public List<ExtInquiryPriceRateDto> statisticsItemTimes(Map<String, Object> params);
}
