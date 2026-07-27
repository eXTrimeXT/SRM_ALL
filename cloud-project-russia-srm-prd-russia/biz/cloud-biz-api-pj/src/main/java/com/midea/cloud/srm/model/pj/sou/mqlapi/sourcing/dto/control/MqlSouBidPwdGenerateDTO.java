package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouGroupOperateAuthEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * MQL - 开标密码生成请求数据
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MqlSouBidPwdGenerateDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    /** @see SouGroupOperateAuthEnum */
    @ApiModelProperty("开标场景(商务/技术等)")
    private Set<String> openBidTypes;

    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (CollectionUtils.isEmpty(openBidTypes)) {
            throw new IllegalArgumentException("缺少openBidTypes参数");
        } else {
            Set<String> types = new HashSet<>(openBidTypes.size());
            openBidTypes.forEach(type -> {
                type = StringUtils.trimToNull(type);
                if (type != null) {
                    types.add(type);
                }
            });
            if (types.isEmpty()) {
                throw new IllegalArgumentException("缺少openBidTypes参数");
            }
            openBidTypes = types;
        }
    }

}
