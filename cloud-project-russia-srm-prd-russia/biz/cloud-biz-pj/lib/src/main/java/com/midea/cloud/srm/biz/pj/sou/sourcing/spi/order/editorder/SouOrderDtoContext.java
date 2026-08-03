package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 寻源核心 - 供应商报价校验上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/05
 */
@Data
public class SouOrderDtoContext {

    @ApiModelProperty("供应商ID")
    protected Long vendorId;

    @ApiModelProperty("寻源单")
    protected SouProject souProject;

    /** souItemId */
    @ApiModelProperty("供应商可报价物料集合")
    protected Map<Long, SouItem> availableItemMap;
    @ApiModelProperty("供应商可报价币种信息")
    /** currencyCode */
    protected Map<String, SouCurrency> currencyMap;
    /** fileConfigId */
    @ApiModelProperty("供方必须上传附件")
    protected Map<Long, SouFileConfig> fileConfigMap;
    /** fromCurrency_toCurrency */
    @ApiModelProperty("汇率信息")
    protected Map<String, BigDecimal> exchangeRateMap;
    /** souItemId  souItemLadderId */
    @ApiModelProperty("阶梯价模板信息")
    protected Map<Long, Map<Long, SouItemLadder>> ladderTemplateMap;
    /** taxKey */
    @ApiModelProperty("可用税率信息")
    protected Map<String, BigDecimal> taxMap;
    @ApiModelProperty("已存在的当前轮次报价单")
    protected SouOrder existOrder;
    /** souItemId */
    @ApiModelProperty("已存在的当前轮次报价明细")
    protected Map<Long, SouOrderItem> existOrderItemMap;
    /** souItemId  attrId  fieldId */
    @ApiModelProperty("现有的料费分离报价数据")
    protected Map<Long, Map<Long, List<Map<String, Object>>>> quoteDataMap;

    private static final ThreadLocal<SouOrderDtoContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setContextHolder(SouOrderDtoContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static SouOrderDtoContext getContextHolder() {
        return CONTEXT_HOLDER.get();
    }

    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
