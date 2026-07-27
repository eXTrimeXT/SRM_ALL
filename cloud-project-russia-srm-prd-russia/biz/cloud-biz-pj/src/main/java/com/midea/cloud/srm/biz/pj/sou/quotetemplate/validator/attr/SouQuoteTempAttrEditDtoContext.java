package com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.attr;

import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempApi;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttrTable;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Builder
class SouQuoteTempAttrEditDtoContext {

    /** 可用的报价属性集合 */
    private Map<Long, SouQuoteTempAttr> attrs;
    /** 报价属性对应的动态表定义 */
    private Map<Long, SouQuoteTempAttrTable> availableAttrTables;
    /** 报价属性对应的api定义 */
    private Map<Long, SouQuoteTempApi> apiMap;
    /** 字典集 */
    private Map<String, Set<String>> dictMap;

    static final ThreadLocal<SouQuoteTempAttrEditDtoContext> CONTEXT_HOLDER = new ThreadLocal<>();
    public static SouQuoteTempAttrEditDtoContext getContext() {
        return CONTEXT_HOLDER.get();
    }
    public static void setContextHolder(SouQuoteTempAttrEditDtoContext context) {
        CONTEXT_HOLDER.set(context);
    }
    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
