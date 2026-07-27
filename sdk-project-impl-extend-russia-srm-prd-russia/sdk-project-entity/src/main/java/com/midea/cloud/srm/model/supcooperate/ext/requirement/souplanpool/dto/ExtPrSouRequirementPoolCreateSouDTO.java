package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto;

import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 招标计划池 - 创建寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementPoolCreateSouDTO extends BaseObjectX {

    /** @see SouTypeEnum **/
    @ApiModelProperty("寻源单类型")
    private String souType;

    @ApiModelProperty("计划池信息")
    private List<ExtPrSouRequirementHeadDTO> reqHeadList;

    /**
     * 入参格式化
     */
    public void formatParams() {
        souType = StringUtils.trimToNull(souType);
        if (souType == null) {
            throw new IllegalArgumentException("缺少souType参数");
        }
        if (CollectionUtils.isEmpty(reqHeadList)) {
            throw new IllegalArgumentException("缺少reqHeadList参数");
        }
    }

}
