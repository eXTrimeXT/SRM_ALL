package com.midea.cloud.common.pj.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * 对集合的处理
 * 1. 集合分组(使用场景：如批量保存数据)
 * 2. 获取两个集合的交集
 *
 * @author zhangwk12@meicloud.com
 */
public class PjCollectionUtils {

    private PjCollectionUtils() {}

    /**
     * 对集合进行分组
     * 例如集合包含300数据，需要每150条数据一组
     * @param collection 需要进行分组的集合
     * @param size 每组数据的上限
     */
    public static <T> List<List<T>> group(final Collection<T> collection, final int size) {
        if (collection.isEmpty()) { return Collections.emptyList(); }

        List<List<T>> resultList = new ArrayList<>(8);

        List<T> tempL = null;
        for (T t : collection) {
            if (tempL == null) {
                tempL = new ArrayList<>(size);
            }
            tempL.add(t);
            if (tempL.size() % size == 0 && tempL.size() / size > 0) {
                resultList.add(tempL);
                tempL = null;
            }
        }
        if (tempL != null && !tempL.isEmpty()) {
            // 还有剩余的
            resultList.add(tempL);
        }
        return resultList;
    }

    /**
     * 一个用于分组查询的便捷方法，能自动组合分组操作的结果集(List)
     * PS: 避免拿到{@link #group(Collection, int)}的结果集后，遍历操作，并组合最终结果
     * @param collection 需要分组的集合
     * @param size 每组的数量上限
     * @param handler 对于每一组数据，应该进行什么样的操作
     * @param <T> 源集合类型
     * @param <R> 目标集合类型
     */
    public static <T, R> List<R> getListResultByGroup(final Collection<T> collection, final int size,
                                                      Function<List<T>, Collection<R>> handler) {
        List<R> resultList = new ArrayList<>(collection.size() << 1);
        List<List<T>> groupList = group(collection, size);

        groupList.forEach(group -> resultList.addAll(handler.apply(group)));

        return resultList;
    }

    /**
     * 一个用于分组查询的便捷方法，能自动组合分组操作的结果集(Map)
     * PS: 避免拿到{@link #group(Collection, int)}的结果集后，遍历操作，并组合最终结果
     * @param collection 需要分组的集合
     * @param size 每组的数量上限
     * @param handler 对于每一组数据，应该进行什么样的操作
     * @param <T> 源集合类型
     * @param <K> 目标集合类型
     * @param <V> 目标集合类型
     */
    public static <T, K, V> Map<K, V> getMapResultByGroup(final Collection<T> collection, final int size,
                                                          Function<List<T>, Map<K, V>> handler) {
        Map<K, V> resultMap = new HashMap<>(collection.size() << 2);
        List<List<T>> groupList = group(collection, size);

        groupList.forEach(group -> resultMap.putAll(handler.apply(group)));

        return resultMap;
    }

    /**
     * 获取交集
     * @param groupList 需要筛选交集的源数据
     * @param <T> 基本类型包装类或String
     * @return 交集
     */
    public static <T> List<T> intersection(final Collection<List<T>> groupList) {
        if (groupList == null || groupList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Set<T>> group2 = new ArrayList<>(groupList.size());
        Set<T> set;
        for (List<T> group : groupList) {
            set = new HashSet<>(group);
            group2.add(set);
        }
        return intersection(group2);
    }

    /**
     * 获取交集
     * @param groupList 需要筛选交集的源数据
     * @param <T> 基本类型包装类或String
     * @return 交集
     */
    public static <T> List<T> intersection(final List<Set<T>> groupList) {
        if (groupList == null || groupList.isEmpty()) {
            return Collections.emptyList();
        }
        int groupCount = groupList.size();
        Map<T, AtomicInteger/* 重复次数 */> repeatNodeCountMap = new HashMap<>(groupCount << 3);

        // 计算每个元素出现的次数
        AtomicInteger repeatCount;
        int matchCount = 0;
        for (Set<T> group : groupList) {
            for (T t : group) {
                repeatCount = repeatNodeCountMap.computeIfAbsent(t, (k) -> new AtomicInteger(0));
                if (repeatCount.incrementAndGet() == groupCount) {
                    matchCount++;
                }
            }
        }

        // 过滤出出现次数==groupCount的元素
        if (matchCount <= 0) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(matchCount);
        repeatNodeCountMap.forEach((t, count) -> {
            if (count.get() == groupCount) {
                result.add(t);
            }
        });
        return result;
    }

    /**
     * 获取交集
     * @param groups 需要筛选交集的源数据
     * @param <T> 基本类型包装类或String
     * @return 交集
     */
    public static <T> List<T> intersection(final Collection<T>... groups) {
        if (groups == null || groups.length <= 0) {
            return Collections.emptyList();
        }
        List<Set<T>> group2 = new ArrayList<>(groups.length);
        Set<T> set;
        for (Collection<T> group : groups) {
            set = new HashSet<>(group);
            group2.add(set);
        }
        return intersection(group2);
    }

    /**
     * 获取并集
     */
    public static <T> List<T> union(final Collection<List<T>> lists) {
        if (lists.isEmpty()) {
            return Collections.emptyList();
        }

        Set<T> set = new HashSet<>(lists.size() << 3);
        lists.forEach(set::addAll);
        return new ArrayList<>(set);
    }

    /**
     * 把对象放到map对应的list中
     * @param map
     * @param key
     * @param obj
     * @param <T>
     */
    public static <T> void putMapList(Map<String, List<T>> map, String key, T obj){
        List<T> objList = map.get(key);
        if(objList == null){
            objList = new ArrayList<T>();
        }
        objList.add(obj);
        map.put(key, objList);
    }
    /**
     * 把BigDecimal按照类型汇总
     * @param map
     * @param key
     * @param bigDecimal
     */
    public static void putMap(Map<String, BigDecimal> map, String key, BigDecimal bigDecimal){
        if(bigDecimal == null){
            return;
        }
        BigDecimal sum = map.get(key);
        if(sum == null){
            sum = BigDecimal.ZERO;
        }
        map.put(key, sum.add(bigDecimal));
    }
    /**
     * 把对象放到map对应的set中
     * @param map
     * @param key
     * @param obj
     * @param <T>
     */
    public static <T> void putMapSet(Map<String, Set<T>> map, String key, T obj){
        Set<T> objs = map.get(key);
        if(objs == null){
            objs = new HashSet<>();
        }
        objs.add(obj);
        map.put(key, objs);
    }

}
