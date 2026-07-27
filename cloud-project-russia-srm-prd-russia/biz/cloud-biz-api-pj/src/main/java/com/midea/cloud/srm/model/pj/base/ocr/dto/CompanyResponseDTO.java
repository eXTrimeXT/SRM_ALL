package com.midea.cloud.srm.model.pj.base.ocr.dto;

import com.mideacloud.common.util.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author huangbf3
 */
@Data
@Slf4j
public class CompanyResponseDTO {

    @ApiModelProperty("公司地址")
    private String address;

    @ApiModelProperty("统一社会信用代码（三合一之前为注册号）")
    private String regNum;

    @ApiModelProperty("注册资本")
    private String capital;

    @ApiModelProperty("法定代表人")
    private String person;

    @ApiModelProperty("经营范围")
    private String business;

    @ApiModelProperty("营业期限")
    private String period;

    @ApiModelProperty("组成形式")
    private String composingForm;

    @ApiModelProperty("成立日期")
    private String setDate;

    @ApiModelProperty("主体类型")
    private String type;

    @ApiModelProperty("公司名称")
    private String name;

    @ApiModelProperty("营业时间开始")
    private Date businessStartDate;

    @ApiModelProperty("营业时间结束")
    private Date businessEndDate;

    /**
     *
     * @param companyResponseDTO 要转换的CompanyResponseDTO对象
     * @return CompanyResultVo对象
     */
    public static CompanyResponseDTO convertbusinessDate(CompanyResponseDTO companyResponseDTO) throws ParseException {
        if(StringUtils.isNotBlank(companyResponseDTO.getPeriod())){
            Date[] dateRangeArray = parseDateRange(companyResponseDTO.getPeriod());
            Date businessStartDate = dateRangeArray[0];
            Date businessEndDate = dateRangeArray[1];
            companyResponseDTO.setBusinessStartDate(businessStartDate);
            companyResponseDTO.setBusinessEndDate(businessEndDate);
        }
        return companyResponseDTO;
    }

    public static Date[] parseDateRange(String dateRange) throws ParseException {
        Date[] dateRangeArray = new Date[2];

        // 使用正则表达式拆分日期范围
        String[] dateParts = dateRange.split("至");
        int length = 2;
        if (dateParts.length == length) {
            String startDateStr = dateParts[0].trim();
            String endDateStr = dateParts[1].trim();
            // 解析开始日期
            dateRangeArray[0] = parseDateWithoutError(startDateStr);

            // 解析结束日期，如果是"长期"则设置为9999年12月
            dateRangeArray[1] = "长期".equals(endDateStr) ?
                    new SimpleDateFormat("yyyy年MM月dd日").parse("9999年12月31日") :
                    parseDateWithoutError(endDateStr);
        } else {
            throw new ParseException("Invalid date range format.", 0);
        }
        return dateRangeArray;
    }

    private static Date parseDateWithoutError(String dateStr) {
        try {
            return DateUtil.parseDate(dateStr,"yyyy年MM月dd日");
        } catch (Exception e) {
            log.error("parseDateWithoutError Exception", e);
        }
        return null;
    }
}
