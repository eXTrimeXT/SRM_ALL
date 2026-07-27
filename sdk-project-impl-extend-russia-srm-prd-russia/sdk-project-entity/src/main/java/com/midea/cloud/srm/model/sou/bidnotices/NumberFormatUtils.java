package com.midea.cloud.srm.model.sou.bidnotices;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 100014336
 */
@Slf4j
public class NumberFormatUtils {

    /**
     * 把以万为单位转化为千的格式
     * @param wan
     * @return
     */
    public static String wanToThousand(BigDecimal wan){
        //真实数字
        BigDecimal intBigDecimal = wan.multiply(new BigDecimal("10000"));
        // 整数部分
        int trueNumber = intBigDecimal.intValue();
        //小数部分
        int decimalPart = intBigDecimal.subtract(intBigDecimal.setScale(0, RoundingMode.DOWN)).multiply(new BigDecimal("100")).intValue();
        //开始转化为字符串，每1000补一个逗号;
        char[] digits = Integer.toString(trueNumber).toCharArray();
        List<String> thousands = new ArrayList<>();
        List<Character> oneThousand = new ArrayList<>();
        for (int i= 1;i<=digits.length;i++) {
            oneThousand.add(digits[digits.length-i]);
            if(i%3==0||i==digits.length){
                //添加逗号
                Collections.reverse(oneThousand);
                if(i/3>0&&i/3!=1){
                    oneThousand.add(',');
                }
                StringBuilder sb = new StringBuilder();
                for (Character c:oneThousand){
                    sb.append(c);
                }
                thousands.add(sb.toString());
                oneThousand.clear();
            }
        }
        Collections.reverse(thousands);
        StringBuilder sb = new StringBuilder();
        for (String th:thousands){
            sb.append(th);
        }
        sb.append('.');
        sb.append(decimalPart);
        return sb.toString();
    }

    public static void main(String[] args) {
        log.info(wanToThousand(new BigDecimal("1000.8")));
    }
}
