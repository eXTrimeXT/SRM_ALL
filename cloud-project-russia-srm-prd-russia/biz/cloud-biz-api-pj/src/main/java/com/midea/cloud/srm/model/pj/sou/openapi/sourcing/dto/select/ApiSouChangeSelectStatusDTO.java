package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 寻源openAPI - 中标/落标
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/03
 */
@Data
@ApiModel(description = "中标/落标")
@EqualsAndHashCode(callSuper = true)
public class ApiSouChangeSelectStatusDTO extends BaseObjectX {

    @ApiModelProperty("竞价单ID")
    protected Long projectId;
    @ApiModelProperty("报价行信息")
    protected List<ApiSouChangeSelectStatusItemDTO> selects;
    @SuppressWarnings("AlibabaPojoMustUsePrimitiveField")
    @ApiModelProperty("true-中标/false-落标")
    protected boolean toWin;

    public void formatParams() {
        if (CollectionUtils.isEmpty(selects)) {
            throw new IllegalArgumentException("请选择数据");
        }
    }

}
