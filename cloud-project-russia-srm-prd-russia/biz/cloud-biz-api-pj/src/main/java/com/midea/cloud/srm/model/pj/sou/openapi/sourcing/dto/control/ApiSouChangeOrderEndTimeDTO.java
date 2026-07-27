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
 * 寻源openAPI - 用于修改报价截止时间
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/01
 */
@Data
@ApiModel(description = "用于修改报价截止时间")
@EqualsAndHashCode(callSuper = true)
public class ApiSouChangeOrderEndTimeDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    /** @see SouRound#getOrderEndTime */
    @ApiModelProperty("投标截止时间(用于修改报价开始时间(立即截止/延长))")
    private Date orderEndTime;
    @SuppressWarnings({"AlibabaPojoNoDefaultValue", "AlibabaPojoMustUsePrimitiveField"})
    @ApiModelProperty("用于立即截止报价")
    private boolean endNow = true;
    /** @see SouRound#getChangeOrderEndTimeReason */
    @ApiModelProperty("修改报价截止时间原因")
    private String changeOrderEndTimeReason;

    /** 当前登录人ID */
    private Long currentUserId;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (orderEndTime == null) {
            if (!endNow) {
                throw new IllegalArgumentException("参数错误");
            }
        } else {
            if (!orderEndTime.after(new Date())) {
                endNow = true;
            }
        }
        changeOrderEndTimeReason = StringUtils.trimToNull(changeOrderEndTimeReason);
        int lenght = 300;
        if (changeOrderEndTimeReason != null && changeOrderEndTimeReason.length() > lenght) {
            throw new IllegalArgumentException("修改报价截止时间原因的输入长度不能超过300");
        }
    }

}
