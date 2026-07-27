package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 供应商端: 查看招投标项目信息的查询条件
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
public class MqlBidSouVendorViewOrderDetailQueryWebDTO {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID(必填)")
    private Long projectId;
    /** @see SouOrderItem#getRound */
    @ApiModelProperty("轮次")
    private Integer round;
    /** @see SouOrderItem#getItemDesc */
    @ApiModelProperty("物料描述")
    private String itemDesc;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        itemDesc = StringUtils.trimToNull(itemDesc);
    }

}
