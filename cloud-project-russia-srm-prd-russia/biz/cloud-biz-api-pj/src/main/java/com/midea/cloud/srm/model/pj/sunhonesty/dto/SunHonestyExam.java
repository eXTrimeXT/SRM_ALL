package com.midea.cloud.srm.model.pj.sunhonesty.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 是否需要考试实体
 * @author fu
 * @date 2024-08-19
 */
@Data
@ApiModel("校验是否需要考试")
public class SunHonestyExam {

    @TableId
    /**
     * 是否需要考试（YES/NO）
     */
    @ApiModelProperty("是否需要考试（YES/NO）")
    private String isExam;

    /**
     * 是否考试提醒信息
     */
    @ApiModelProperty("是否考试提醒信息")
    private String msg;

}
