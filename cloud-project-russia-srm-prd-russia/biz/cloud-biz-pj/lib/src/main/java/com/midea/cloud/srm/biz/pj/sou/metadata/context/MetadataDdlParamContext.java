package com.midea.cloud.srm.biz.pj.sou.metadata.context;

import java.util.ArrayList;
import java.util.List;

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
 *  修改日期: 2022/7/8 12:46
 *  修改内容:
 * </pre>
 */
public class MetadataDdlParamContext {
    private static ThreadLocal<List<Object>> currentParam = new ThreadLocal<>();

    public static void init() {
        currentParam.set(new ArrayList<>());
    }

    public static void add(Object param) {
        currentParam.get().add(param);
    }

    public static void remove() {
        currentParam.remove();
    }

    public static List<Object> get() {
        return currentParam.get();
    }
}
