package com.midea.cloud.srm.model.sou.bidnotices;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 100014336 ganyh19
 */
@Slf4j
public class NumberToChinese {

    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] CN_UPPER_MONETRAY_UNIT = {"分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾亿", "佰亿", "仟亿"};
    private static final String CN_FULL = "整";
    private static final String CN_NEGATIVE = "负";
    private static final String CN_ZEOR_FULL = "零元" + CN_FULL;

    public static void main(String[] args) {
        // 要转换的数字
        BigDecimal number = new BigDecimal("1.205678");

        log.info(convertNumberToChineseAmountWithWan(number));
    }

    /**
     * 数字金额转中文大写金额
     * @param number 单位是万元
     * @return
     */
    public static String convertNumberToChineseAmountWithWan(BigDecimal number) {
        number = number.multiply(new BigDecimal("10000"));
        return numberToChineseUnit(number.doubleValue());
    }

    /**
     * 数字金额转中文大写金额
     * @param number 单位元
     * @return
     */
    public static String numberToChineseUnit(double number) {
        StringBuffer sb = new StringBuffer();

        int signum = (number > 0 ? 1 : (number < 0 ? -1 : 0));
        if (signum == 0) {
            return CN_ZEOR_FULL;
        }

        long numberLong = Math.round(number * 100);
        long scale = numberLong % 100;
        int numUnit = 0;
        int numIndex = 0;
        boolean getZero = false;
        if (!(scale > 0)) {
            numIndex = 2;
            numberLong = numberLong / 100;
            getZero = true;
        }
        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            numberLong = numberLong / 10;
            getZero = true;
        }

        int zeroSize = 0;
        while (true) {
            if (numberLong <= 0) {
                break;
            }
            numUnit = (int) (numberLong % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (!getZero) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                }
                if (numIndex == 2) {
                    if (numberLong > 0) {
                        sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                    }
                } else if (((numIndex - 2) % 4 == 0) && (numberLong % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                }
                getZero = true;
            }
            numberLong = numberLong / 10;
            ++numIndex;
        }

        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        if (!(scale > 0)) {
            sb.append(CN_FULL);
        }
        return sb.toString();
    }

}