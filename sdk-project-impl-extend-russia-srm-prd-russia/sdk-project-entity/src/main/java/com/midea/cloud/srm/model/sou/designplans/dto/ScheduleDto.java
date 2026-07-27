package com.midea.cloud.srm.model.sou.designplans.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChLedger;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ex_liuxy46
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ScheduleDto extends SccSouChLedger {

    @ApiModelProperty("项目轮次结束")
    @TableField(exist = false)
    private Integer endNum;

    @ApiModelProperty("项目策划方案状态")
    @TableField(exist = false)
    private String mainPlanStatus;
    @ApiModelProperty("询比价")
    @TableField(exist = false)
    private String mainIsXbj;
    @ApiModelProperty("定厂定价申请")
    @TableField(exist = false)
    private String mainIsSq;

    @ApiModelProperty("询比价")
    @TableField(exist = false)
    private String xIsXbj;
    @ApiModelProperty("调价申请")
    @TableField(exist = false)
    private String xIsSq;
    @ApiModelProperty("第几次")
    @TableField(exist = false)
    private Long xNum;

    @ApiModelProperty("调价申请")
    @TableField(exist = false)
    private String sIsSq;
    @ApiModelProperty("第几次")
    @TableField(exist = false)
    private Long sNum;
}
