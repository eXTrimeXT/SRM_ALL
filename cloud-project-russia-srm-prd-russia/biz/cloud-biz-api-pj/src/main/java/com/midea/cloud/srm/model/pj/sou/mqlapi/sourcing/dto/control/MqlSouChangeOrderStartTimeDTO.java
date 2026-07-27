package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.control;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouRound;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * MQL - 用于修改报价开始时间
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/10
 */
@Data
@ApiModel(description = "用于修改报价开始时间")
@EqualsAndHashCode(callSuper = true)
public class MqlSouChangeOrderStartTimeDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouRound#getOrderStartTime */
    @ApiModelProperty("投标开始时间(用于修改报价开始时间(延迟报价))")
    private Date orderStartTime;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("用于立即开始报价")
    private Boolean startNow = true;

    @ApiModelProperty("寻源场景")
    private String souType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (orderStartTime == null) {
            if (!startNow) {
                throw new IllegalArgumentException("参数错误");
            }
        } else {
            if (!orderStartTime.after(new Date())) {
                startNow = true;
            }
        }
    }

}
