package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo;

import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招标计划池 - 创建寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementCreateSouVO extends BaseObjectX {

    /** @see SouTypeEnum */
    @ApiModelProperty("寻源类型")
    private String souType;

    @ApiModelProperty("寻源单信息")
    private Object souVO;

}
