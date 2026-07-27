package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * MQL - 供应商报价结果查询条件
 *
 * @author ex_nongtb@partner.midea.com
 * @since 2023/03/09
 */
@Data
@ApiModel(description = "供应商报价结果查询条件")
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderResultQueryDTO extends BasePage {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouOrderItem#getItemCode */
    @ApiModelProperty("物料编码")
    private String itemCode;

    /** @see SouOrderItem#getItemDesc */
    @ApiModelProperty("物料名称")
    private String itemDesc;

    /** @see SouOrder#getRound */
    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        itemCode = StringUtils.trimToNull(itemCode);
        itemDesc = StringUtils.trimToNull(itemDesc);
    }
}
