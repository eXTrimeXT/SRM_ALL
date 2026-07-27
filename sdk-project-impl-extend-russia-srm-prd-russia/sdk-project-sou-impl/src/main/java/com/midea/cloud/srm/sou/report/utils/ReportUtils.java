package com.midea.cloud.srm.sou.report.utils;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.DateUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public class ReportUtils {

    public static final Integer NUMBER_SIXTY = 60;
    public static final Integer NUMBER_TWENTY_FOUR = 24;
    public static final Integer NUMBER_THOUSAND = 1000;
    public static final Integer NUMBER_FOUR = 4;

    /**
     * 计算两个日期相差天数
     * @param date
     * @param otherDate
     * @return
     */
    public static Long dateSubtractAsDay(Date date, Date otherDate) {
        if(ObjectUtils.anyNull(date, otherDate)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateWithoutHours(date));
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTime(dateWithoutHours(otherDate));

        Long diff = otherCalendar.getTimeInMillis() - calendar.getTimeInMillis();

        Long diffDays = diff/(NUMBER_THOUSAND*NUMBER_SIXTY*NUMBER_SIXTY*NUMBER_TWENTY_FOUR);
        return diffDays;
    }

    private static Date dateWithoutHours(Date date) {
        if(Objects.isNull(date)) {
            return date;
        }
        try {
            return DateUtil.parseDate(DateUtil.format(date, DateUtil.DATE_FORMAT_10), DateUtil.DATE_FORMAT_10);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 计算两个日期相差天数
     * @param date
     * @param otherDate
     * @return
     */
    public static Long dateSubtractAsDay(Object date, Object otherDate) {
        return dateSubtractAsDay(objectToDate(date), objectToDate(otherDate));
    }

    /**
     * 两个数相除
     * @param decimal
     * @param divisor
     * @param precision
     * @return
     */
    public static BigDecimal divideBigDecimal(BigDecimal decimal, BigDecimal divisor, Integer precision) {
        if(ObjectUtils.anyNull(decimal, divisor)) {
            return null;
        }
        if(BigDecimal.ZERO.compareTo(divisor) == 0) {
            return null;
        }
        return decimal.divide(divisor, precision, RoundingMode.HALF_UP);
    }

    /**
     * 两个数相除
     * @param decimal
     * @param divisor
     * @return
     */
    public static BigDecimal divideBigDecimal(BigDecimal decimal, BigDecimal divisor) {
        return divideBigDecimal(decimal, divisor, NUMBER_FOUR);
    }

    /**
     * 两个数相除
     * @param decimal
     * @param divisor
     * @return
     */
    public static BigDecimal divideBigDecimal(Object decimal, Object divisor) {
        return divideBigDecimal(objectToBigDecimal(decimal), objectToBigDecimal(divisor));
    }

    /**
     * 两数相减
     * @param decimal
     * @param subtraction
     * @return
     */
    public static BigDecimal subtractBigDecimal(BigDecimal decimal, BigDecimal subtraction) {
        if(ObjectUtils.anyNull(decimal, subtraction)) {
            return null;
        }
        return decimal.subtract(subtraction);
    }

    /**
     * 两数相减
     * @param decimal
     * @param subtraction
     * @return
     */
    public static BigDecimal subtractBigDecimal(Object decimal, Object subtraction) {
        return subtractBigDecimal(objectToBigDecimal(decimal), objectToBigDecimal(subtraction));
    }

    /**
     * 两数相减
     * @param decimal
     * @param subtraction
     * @return
     */
    public static Long subtractLong(Object decimal, Object subtraction) {
        BigDecimal result = subtractBigDecimal(objectToBigDecimal(decimal), objectToBigDecimal(subtraction));
        if(ObjectUtils.anyNull(result)) {
            return null;
        }
        return result.longValue();
    }

    /**
     * 转成数字
     * @param object
     * @return
     */
    public static BigDecimal objectToBigDecimal(Object object) {
        if(ObjectUtils.anyNull(object)) {
            return null;
        }
        if(object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        return new BigDecimal(object.toString());
    }

    @SneakyThrows(value = {Exception.class})
    public static Date objectToDate(Object object) {
        if(ObjectUtils.anyNull(object)) {
            return null;
        }
        if(object instanceof Date) {
            return (Date) object;
        }
        if(object instanceof LocalDate) {
            return DateUtil.localDateToDate((LocalDate) object);
        }
        return DateUtil.parseDate((String) object);
    }
}
