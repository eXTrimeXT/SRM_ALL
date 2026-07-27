package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.importquotetemp;

import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempField;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 寻源核心 - 供应商物料维度报价模板导入上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouOrderImportQuoteTempDtoContext extends BaseObjectX {

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("是否采购商端")
    private boolean isBuyer;

    @ApiModelProperty("查询轮次")
    private Integer searchRound;

    @ApiModelProperty("导入文件")
    private MultipartFile file;

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

    private static final ThreadLocal<SouOrderImportQuoteTempDtoContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setContextHolder(SouOrderImportQuoteTempDtoContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static SouOrderImportQuoteTempDtoContext getContextHolder() {
        return CONTEXT_HOLDER.get();
    }

    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
