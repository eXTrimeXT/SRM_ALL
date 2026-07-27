package com.midea.cloud.srm.sou.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author: panmq
 * @Date: 2024/04/03/ $
 * @Description: 招标字母工具类
 */
public class ExtSouBidLecterUtils {

    private static final  List<String> ALPHABET = Arrays.asList("A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z");

    private static final String VENDOR_HIDE_KEY_INFO = "供应商";

    /**
     * 获取供应商屏蔽名称
     * @param index
     * @return
     */
    public static String shieldVendorName(int index) {
        return VENDOR_HIDE_KEY_INFO + lecter(index, "");
    }

    public static String lecter(int index, String lecter) {
        int subscrite = index % 26;
        int dealar = index / 26;
        lecter = Objects.isNull(lecter) ? "" : lecter;
        lecter = StringUtils.join(getLecter(subscrite), lecter);
        if (Integer.compare(dealar, 0) > 0) {
            return lecter(dealar - 1, lecter);
        }
        return lecter;
    }

    private static String getLecter(int index) {
        return ALPHABET.get(index);
    }
}
