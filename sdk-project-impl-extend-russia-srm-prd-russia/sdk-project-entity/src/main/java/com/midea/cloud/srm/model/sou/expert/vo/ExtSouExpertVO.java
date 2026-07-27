package com.midea.cloud.srm.model.sou.expert.vo;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源 - 专家库
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertVO extends ExtSouExpert {

    @ApiModelProperty("专家申请信息")
    private ExtSouExpertApplyVO applyInfo;

}
