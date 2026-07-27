package com.midea.cloud.srm.supcooperate.ext.requirementcancles.utils;

import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm 采购需求取消实现类
 *
 * @author srm
 * @date 2024-05-20
 */
public class RequirementCancleUtils {

    private static final int NUM2 = 2;
    /**
     * 取消原因
     * @param cancleReson
     * @param requirementHeadNum
     * @return
     */
    public static String cancleReason(String cancleReson, String requirementHeadNum) {
        if(StringUtils.isBlank(cancleReson)) {
            return formatCancleReason(requirementHeadNum);
        }
        if(!cancleReson.startsWith(StringUtils.joinWith("", SrmConstant.PR_ABANDON_DEAULT_REASON, SrmConstant.COLON))) {
            return formatCancleReason(requirementHeadNum);
        }
        String[] cancleResonSplit = unFormatCancleReason(cancleReson);
        if(cancleResonSplit.length != NUM2) {
            return formatCancleReason(requirementHeadNum);
        }
        String appendRequirementHeadNum = StringUtils.joinWith(SrmConstant.SIG_3, cancleResonSplit[1], requirementHeadNum);
        List<String> headNumList = new ArrayList<>(Arrays.asList(appendRequirementHeadNum.split(SrmConstant.SIG_3)));
        appendRequirementHeadNum = headNumList.stream().distinct().collect(Collectors.joining(SrmConstant.SIG_3));
        return formatCancleReason(appendRequirementHeadNum);
    }

    private static String formatCancleReason(String requirementHeadNum) {
        if(StringUtils.isBlank(requirementHeadNum)) {
            return SrmConstant.PR_ABANDON_DEAULT_REASON;
        }
        return StringUtils.joinWith(SrmConstant.COLON, SrmConstant.PR_ABANDON_DEAULT_REASON, requirementHeadNum);
    }

    private static String[] unFormatCancleReason(String cancleReson) {
        String[] cancleResonSplit = cancleReson.split(SrmConstant.COLON);
        return cancleResonSplit;
    }


    /**
     * 缓存值
     * @param cache
     * @param key
     * @param value
     */
    public static void cacheKeyValue(HashMap<String, Object> cache, String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 获取缓存
     * @param cache
     * @param key
     * @param defualt
     * @param <T>
     * @return
     */
    public static  <T> T getCacheValue(HashMap<String, Object> cache, String key, T defualt) {
        if(!cache.containsKey(key) || Objects.isNull(cache.get(key))) {
            cache.put(key, defualt);
        }
        return (T) cache.get(key);
    }

    public static List<BidDataSubmit> dataSubmitSortDesc(Map<String, BidDataSubmit> dataSubmitMap, List<String> requirementHeadNumList) {
        List<BidDataSubmit> dataSubmitList = new ArrayList<>(16);
        if(CollectionUtils.isNotEmpty(requirementHeadNumList) && MapUtils.isNotEmpty(dataSubmitMap)) {
            requirementHeadNumList.stream().distinct().filter(o -> dataSubmitMap.containsKey(o)).forEach(o -> dataSubmitList.add(dataSubmitMap.get(o)));
        }
        List<BidDataSubmit> dataSubmitWirtSortList = dataSubmitList.stream().sorted(new Comparator<BidDataSubmit>() {
            @Override
            public int compare(BidDataSubmit o1, BidDataSubmit o2) {

                return ObjectUtils.defaultIfNull(o2.getTotalBudget(), BigDecimal.ZERO).compareTo(ObjectUtils.defaultIfNull(o1.getTotalBudget(), BigDecimal.ZERO));
            }
        }).collect(Collectors.toList());
        return dataSubmitWirtSortList;
    }

}
