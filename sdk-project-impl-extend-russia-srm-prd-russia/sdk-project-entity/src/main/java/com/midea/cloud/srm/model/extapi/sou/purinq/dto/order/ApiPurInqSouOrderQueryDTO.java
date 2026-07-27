package com.midea.cloud.srm.model.extapi.sou.purinq.dto.order;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiPurInqSouOrderQueryDTO extends BasePage {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("寻源单名称")
    private String souName;

    /** @see ExtPurInqSouProject#getExtProjectStatus */
    @ApiModelProperty("寻源状态")
    private ExtPurInqSouProjectStatusEnum extProjectStatus;

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
