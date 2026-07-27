package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 寻源 - 专家库 - 专家退出
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertQuiteDTO extends BaseObjectX {

    /** @see ExtSouExpert#getExpertId */
    @ApiModelProperty("专家ID")
    private Long expertId;

    /** @see ExtSouExpert#getQuiteReason */
    @ApiModelProperty("退出原因")
    private String quiteReason;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (expertId == null) {
            throw new IllegalArgumentException("缺少expertId参数");
        }
        quiteReason = StringUtils.trimToNull(quiteReason);
    }

}
