package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 寻源openAPI - 用于修改最早开标时间
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/01
 */
@Data
@ApiModel(description = "用于修改最早开标时间")
@EqualsAndHashCode(callSuper = true)
public class ApiSouChangeEarliestBusinessOpenTimeDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /**
     * @see SouProject#getEarliestBusinessOpenTime
     * @see SouRound#getEarliestBusinessOpenTime
     */
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;

    @ApiModelProperty("调整原因")
    private String changeReason;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (earliestBusinessOpenTime == null) {
            throw new IllegalArgumentException("请选择最早开标时间");
        }
        changeReason = StringUtils.trimToNull(changeReason);
        int length = 300;
        if (changeReason != null && changeReason.length() > length) {
            throw new IllegalArgumentException("调整最早开标时间的原因输入长度不能超过300");
        }
    }

}
