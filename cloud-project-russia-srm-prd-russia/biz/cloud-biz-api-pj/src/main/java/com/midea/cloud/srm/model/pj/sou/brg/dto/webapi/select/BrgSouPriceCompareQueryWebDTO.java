package com.midea.cloud.srm.model.pj.sou.brg.dto.webapi.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 项目式询价 - 报价信息查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/07
 */
@Data
public class BrgSouPriceCompareQueryWebDTO {

    /** @see SouProject#getProjectId */
    @ApiModelProperty(value = "寻源单ID", required = true)
    private Long projectId;

    /** @see SouProject#getCurrentRound */
    @ApiModelProperty("轮次(为空时默认最新轮次)")
    private Integer round;

    /** @see SouItem#getItemCode */
    @ApiModelProperty("物料编码(等值查询)")
    private String itemCode;

    /** @see SouItem#getItemDesc */
    @ApiModelProperty("物料描述")
    private String itemDesc;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        itemCode = StringUtils.trimToNull(itemCode);
        itemDesc = StringUtils.trimToNull(itemDesc);
    }

}
