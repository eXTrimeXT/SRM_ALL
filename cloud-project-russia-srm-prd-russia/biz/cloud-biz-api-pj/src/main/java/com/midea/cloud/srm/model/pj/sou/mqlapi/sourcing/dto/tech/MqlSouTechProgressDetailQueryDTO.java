package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.tech;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQL - 查看评委对具体供应商的评分细则
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouTechProgressDetailQueryDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("小组成员ID")
    private Long groupId;
    @ApiModelProperty("供应商ID")
    private Long vendorId;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (groupId == null) {
            throw new IllegalArgumentException("缺少groupId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
    }

}
