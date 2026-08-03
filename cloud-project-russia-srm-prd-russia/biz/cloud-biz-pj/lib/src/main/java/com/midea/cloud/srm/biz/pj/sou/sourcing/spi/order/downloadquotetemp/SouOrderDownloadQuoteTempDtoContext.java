package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.downloadquotetemp;

import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempLine;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemQuoteTempDownloadDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 寻源核心 - 供应商物料维度报价模板下载上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouOrderDownloadQuoteTempDtoContext extends BaseObjectX {

    @ApiModelProperty("入参信息")
    private ApiSouOrderItemQuoteTempDownloadDTO param;

    @ApiModelProperty("寻源单")
    private SouProject souProject;

    @ApiModelProperty("物料需求")
    private SouItem souItem;

    @ApiModelProperty("报价模板明细")
    private List<SouQuoteTempLine> tempLineList;

    /** attrId */
    @ApiModelProperty("报价属性")
    private Map<Long, SouQuoteTempAttr> attrMap;

    /** attrId */
    @ApiModelProperty("报价属性字段信息")
    private Map<Long, List<SouQuoteTempField>> attrFieldMap;

    @ApiModelProperty("模板报价数据")
    private SouQuoteTempDataVO quoteData;
    /** dictCode */
    @ApiModelProperty("字典")
    private Map<String, List<DictItemDTO>> dictMap;

    private static final ThreadLocal<SouOrderDownloadQuoteTempDtoContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setContextHolder(SouOrderDownloadQuoteTempDtoContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static SouOrderDownloadQuoteTempDtoContext getContextHolder() {
        return CONTEXT_HOLDER.get();
    }

    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
