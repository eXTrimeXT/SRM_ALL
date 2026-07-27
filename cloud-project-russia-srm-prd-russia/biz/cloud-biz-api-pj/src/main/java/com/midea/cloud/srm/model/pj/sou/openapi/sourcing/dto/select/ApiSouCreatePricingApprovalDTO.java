package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 寻源 - 新的生成价格审批单的参数
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouCreatePricingApprovalDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouProject#getSouType */
    @ApiModelProperty("寻源方式")
    private String souType;

    /** @see SouOrderItem#getOrderItemId */
    @ApiModelProperty("勾选的需要生成价格审批单的报价明细ID集合")
    private Set<Long> orderItemIds;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        souType = StringUtils.trimToNull(souType);
        if (souType == null) {
            throw new IllegalArgumentException("缺少souType参数");
        }
    }

}
