package com.midea.cloud.srm.sou.bid.init.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
public class ExtAnalysisEventListenerImpl<E> extends AnalysisEventListener<E> {

    private List<E> datas = new ArrayList();
    private Map<Integer, String> headMap = new HashMap<>(50);

    @Override
    public void invoke(E e, AnalysisContext analysisContext) {
        this.datas.add(e);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);
        this.headMap = headMap;
    }
}
