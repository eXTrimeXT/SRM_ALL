package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("评分记录")
public class SouScoreManageQueryDTO extends BasePage {

    @ApiModelProperty("寻源单类型")
    @TableField("sou_type")
    private String souType;

    @ApiModelProperty("具体的单据号生成规则由具体业务模块决定")
    @TableField("sou_no")
    private String souNo;

    @ApiModelProperty("寻源单名称")
    @TableField("sou_name")
    private String souName;


    @ApiModelProperty("评分状态:已完成,未完成")
    @TableField("score_manage_status")
    private String scoreManageStatus;



    /**
     * 当前用户ID
     */
    private Long currentUserId;

    /**
     * 当前公司ID
     */
    private Long currentCompanyId;
}
