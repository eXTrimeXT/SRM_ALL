package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 寻源核心 MQL - 供应商报价详情查看条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MqlSouOrderInfoQueryDTO extends BasePage {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @Nullable
    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("寻源场景")
    private String souType;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("查询的物料结果集是否包含所有物料，不仅仅是供应商已报价的物料")
    private Boolean containsAllAvailableItems = true;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否展示其他人的报价信息")
    private Boolean containsOtherVendors = true;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId");
        }
    }

    public MqlSouOrderInfoQueryDTO(Long projectId, Long vendorId, @Nullable Integer round, String souType,
                                   boolean containsAllAvailableItems, boolean containsOtherVendors) {
        this.projectId = projectId;
        this.vendorId = vendorId;
        this.round = round;
        this.souType = souType;
        this.containsAllAvailableItems = containsAllAvailableItems;
        this.containsOtherVendors = containsOtherVendors;
    }

}
