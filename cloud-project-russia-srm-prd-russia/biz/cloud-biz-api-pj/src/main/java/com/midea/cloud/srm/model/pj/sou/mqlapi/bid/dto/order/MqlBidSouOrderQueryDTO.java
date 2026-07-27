package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.order;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * MQL - 供应商列表查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouOrderQueryDTO extends BasePage {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("寻源单名称")
    private String souName;

    /** @see SouProject#getProjectStatus */
    @ApiModelProperty("寻源状态")
    private SouProjectStatusEnum projectStatus;

    /** @see SouOrder#getOrderStatus */
    @ApiModelProperty("报价单状态")
    private SouOrderStatusEnum orderStatus;

    /** @see SouItem#getItemId */
    @ApiModelProperty("物料ID")
    private Long itemId;

    /** @see SouItem#getItemDesc */
    @ApiModelProperty("物料名称")
    private String itemDesc;

    @ApiModelProperty("当前供应商ID(必填)")
    private Long vendorId;

    /** 入参格式化 */
    public void formatParams() {
        souNo = StringUtils.trimToNull(souNo);
        souName = StringUtils.trimToNull(souName);
        itemDesc = StringUtils.trimToNull(itemDesc);
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
    }

}