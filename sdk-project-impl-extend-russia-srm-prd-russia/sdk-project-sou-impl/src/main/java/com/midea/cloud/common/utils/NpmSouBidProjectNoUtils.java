package com.midea.cloud.common.utils;

import com.midea.cloud.srm.model.constant.SrmConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: panmq
 * @Date: 2024/03/08/ $
 * @Description: 非材招标单 单号 分解工具类
 */
public class NpmSouBidProjectNoUtils {

    /**
     * 非材-招标单号分解
     * 分解规则：[公司代码,年,月]
     * 举例：招标单号 GW202403-HR_50000175008 拆解成 GW、2024、03
     * @param projectNo
     * @return
     */
    public static String[] resolveProjectNo(String projectNo) {
        String[] resolveArrarys = new String[] {SrmConstant.SHORT_LINE, SrmConstant.SHORT_LINE, SrmConstant.SHORT_LINE};
        Pattern pattern = Pattern.compile("(\\d{6})");
        Matcher matcher = pattern.matcher(projectNo);

        if(matcher.find()) {
            String yearMonth = matcher.group(1);
            resolveArrarys[1] = yearMonth.substring(0, 4);
            resolveArrarys[2] = yearMonth.substring(4);
            resolveArrarys[0] = projectNo.substring(0, projectNo.indexOf(yearMonth));
        } else {
            Pattern pattern4 = Pattern.compile("(\\d{4})");
            Matcher matcher4 = pattern4.matcher(projectNo);
            if(matcher4.find()){
                String yearMonth = matcher4.group(1);
                resolveArrarys[1] = yearMonth.substring(0, 2);
                resolveArrarys[2] = yearMonth.substring(2);
                resolveArrarys[0] = projectNo.substring(0, projectNo.indexOf(yearMonth));
            }

        }
        return resolveArrarys;
    }
}
