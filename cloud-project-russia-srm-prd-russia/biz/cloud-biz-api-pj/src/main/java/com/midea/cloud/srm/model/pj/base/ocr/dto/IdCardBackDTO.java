package com.midea.cloud.srm.model.pj.base.ocr.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.mideacloud.common.util.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huangbf3
 */
@Data
public class IdCardBackDTO {

    @ApiModelProperty("发证机关")
    @JSONField(name = "Authority")
    private String authority;

    @ApiModelProperty("证件有效期，示例2017.08.12-2037.08.12")
    @JSONField(name = "ValidDate")
    private String validDate;


    @ApiModelProperty("证件有效期开始")
    private Date businessStartDate;

    @ApiModelProperty("证件有效期结束")
    private Date businessEndDate;

    /**
     * 日期转换
     * @param idCardBackDTO
     * @return
     * @throws ParseException
     */
    public static IdCardBackDTO convertvalidDateToBusinessDate(IdCardBackDTO idCardBackDTO) throws ParseException {
        if(StringUtils.isNotBlank(idCardBackDTO.getValidDate())){
            Date[] dateRangeArray = parseDateRange(idCardBackDTO.getValidDate());
            Date businessStartDate = dateRangeArray[0];
            Date businessEndDate = dateRangeArray[1];
            idCardBackDTO.setBusinessStartDate(businessStartDate);
            idCardBackDTO.setBusinessEndDate(businessEndDate);
        }
        return idCardBackDTO;
    }

    public static Date[] parseDateRange(String dateRange) throws ParseException {
        Date[] dateRangeArray = new Date[2];

        // 使用正则表达式拆分日期范围
        String[] dateParts = dateRange.split("-");
        int length = 2;
        if (dateParts.length == length) {
            String startDateStr = dateParts[0].trim();
            String endDateStr = dateParts[1].trim();
            // 解析开始日期
            dateRangeArray[0] = DateUtil.parseDate(startDateStr.replace(".","-"));

            // 解析结束日期，如果是"长期"则设置为9999年12月
            dateRangeArray[1] = "长期".equals(endDateStr) ?
                    new SimpleDateFormat("yyyy年MM月").parse("9999年12月") :
                    DateUtil.parseDate(endDateStr.replace(".","-"));
        } else {
            throw new ParseException("Invalid date range format.", 0);
        }
        return dateRangeArray;
    }
}
