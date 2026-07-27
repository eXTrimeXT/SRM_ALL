package com.midea.cloud.srm.model.sou.timertasks.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Data
@ApiModel("定时执行任务")
public class TimerTaskEntity extends BaseEntity {

    @ApiModelProperty("任务ID")
    private Long taskId;

    @ApiModelProperty("单据ID")
    private Long businessId;

    @ApiModelProperty("单据类型")
    private String businessType;

    @ApiModelProperty("任务执行时间")
    private Date taskStartTime;

}
