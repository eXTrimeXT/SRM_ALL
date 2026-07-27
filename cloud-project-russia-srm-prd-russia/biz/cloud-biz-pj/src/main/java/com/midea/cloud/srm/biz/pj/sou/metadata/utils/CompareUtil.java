package com.midea.cloud.srm.biz.pj.sou.metadata.utils;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/6 18:11
 *  修改内容:
 * </pre>
 */
public class CompareUtil {
    public static boolean compareString(String str1, String str2) {
        if (null == str1) {
            str1 = "";
        }
        if (null == str2) {
            str2 = "";
        }
        return str1.equals(str2);
    }

    public static boolean compareInteger(Integer int1, Integer int2) {
        return (null != int1 && int1.equals(int2) && null != int2 && int2.equals(int1)) || (null == int1 && null == int2);
    }
}
