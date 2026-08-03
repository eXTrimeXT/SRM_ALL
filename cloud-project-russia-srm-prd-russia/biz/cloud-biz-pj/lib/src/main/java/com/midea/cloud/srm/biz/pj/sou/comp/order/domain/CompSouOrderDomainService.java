package com.midea.cloud.srm.biz.pj.sou.comp.order.domain;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 竞价 - 供应商报价服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
public interface CompSouOrderDomainService {

    /**
     * 计算公式报价
     *
     * @param projectId        询价单ID
     * @param orderItemList    公式报价信息
     * @param availableItemMap 当前供应商在当前轮次的可报价物料(可为空)
     * @param currencyMap      可用外币信息(可为空)
     * @param vendorId         供应商ID
     */
    void computeFormulaPrice(long projectId,
                             List<ApiCompSouOrderItemVO> orderItemList,
                             @Nullable Map<Long/* suoItemId */, SouItem> availableItemMap,
                             @Nullable Map<String/* currencyCode */, SouCurrency> currencyMap,
                             long vendorId);

    /**
     * 计算原币含税单价、本币含税/未税单价
     *
     * @param projectId     寻源单ID
     * @param orderItemList 报价信息
     * @param souProject    寻源单(可为空)
     * @param taxMap        税率信息(可为空)
     */
    void computeTaxPriceAndStandardPrice(long projectId,
                                         List<ApiCompSouOrderItemVO> orderItemList,
                                         @Nullable SouProject souProject,
                                         @Nullable Map<String/* taxKey */, BigDecimal> taxMap);

}
