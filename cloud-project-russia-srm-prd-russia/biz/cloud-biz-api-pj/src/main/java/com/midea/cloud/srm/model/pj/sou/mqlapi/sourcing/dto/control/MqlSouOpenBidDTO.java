package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouGroupOperateAuthEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * MQL - 确认开标密码
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MqlSouOpenBidDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    /** @see SouGroupOperateAuthEnum */
    @ApiModelProperty("开标类型(商务/技术等)")
    private String openBidType;
    @ApiModelProperty("开标密码")
    private String pwd;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        openBidType = StringUtils.trimToNull(openBidType);
        if (openBidType == null) {
            throw new IllegalArgumentException("缺少openBidType参数");
        }
        pwd = StringUtils.trimToNull(pwd);
        if (pwd == null) {
            throw new IllegalArgumentException("缺少开标密码");
        }
    }

}
