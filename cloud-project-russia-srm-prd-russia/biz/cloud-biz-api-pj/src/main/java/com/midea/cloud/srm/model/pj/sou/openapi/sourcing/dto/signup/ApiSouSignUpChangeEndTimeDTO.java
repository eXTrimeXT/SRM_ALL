package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 寻源openAPI - 修改报名截止时间
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/03
 */
@Data
@ApiModel(description = "修改报名截止时间")
@EqualsAndHashCode(callSuper = true)
public class ApiSouSignUpChangeEndTimeDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    /** @see SouProject#getSignUpEndTime */
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;
    @SuppressWarnings({"AlibabaPojoNoDefaultValue", "AlibabaPojoMustUsePrimitiveField"})
    @ApiModelProperty("是否立即截止(true/false)")
    private boolean stopNow = true;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (stopNow) {
            signUpEndTime = null;
        } else {
            if (signUpEndTime == null) {
                throw new IllegalArgumentException("缺少signUpEndTime参数");
            } else {
                if (signUpEndTime.before(new Date())) {
                    stopNow = true;
                }
            }
        }
    }

}
