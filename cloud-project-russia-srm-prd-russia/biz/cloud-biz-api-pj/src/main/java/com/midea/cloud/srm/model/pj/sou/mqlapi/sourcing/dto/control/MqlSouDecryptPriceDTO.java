package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * MQL - 报价解密
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@ApiModel(description = "商务开标参数")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MqlSouDecryptPriceDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @Nullable
    @ApiModelProperty("当前登录人ID")
    private Long currentUserId;

    @ApiModelProperty("寻源类型")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}
