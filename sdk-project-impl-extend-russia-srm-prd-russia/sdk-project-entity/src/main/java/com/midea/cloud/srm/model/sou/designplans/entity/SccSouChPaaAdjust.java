package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author ex_liuxy46
 */
@ApiModel(description = "集采台账-调价申请-调整")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_paa_adjust")
public class SccSouChPaaAdjust extends BaseEntity<SccSouChPaaAdjust> {

    @ApiModelProperty("调整id")
    @TableId("ADJUST_ID")
    private Long adjustId;

    @ApiModelProperty("关联提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("调价申请单号")
    @TableField("ADJUST_CODE")
    private String adjustCode;

    @ApiModelProperty("调价申请名称")
    @TableField("ADJUST_NAME")
    private String adjustName;

    @ApiModelProperty("状态拟定、提交、审批")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty("关联集采项目id")
    @TableField("JC_ID")
    private Long jcId;

    @ApiModelProperty("关联集采项目编号")
    @TableField("JC_CODE")
    private String jcCode;

    @ApiModelProperty("次数")
    @TableField("NUM")
    private Integer num;

    @ApiModelProperty("执行时间从")
    @TableField("EXECUTE_DATE_START")
    private Date executeDateStart;

    @ApiModelProperty("执行时间到")
    @TableField("EXECUTE_DATE_END")
    private Date executeDateEnd;

    @ApiModelProperty("调价形式1.询比价调整,2.市场行情调整")
    @TableField("ADJUST_TYPE")
    private String adjustType;

    @ApiModelProperty("创建时间到")
    @TableField(exist = false)
    private String createDateEnd;

    @ApiModelProperty("创建单位(操作人员单位)")
    @TableField("CREATE_UNIT_ID")
    private Long createUnitId;

    @ApiModelProperty("创建单位(操作人员单位)")
    @TableField("CREATE_UNIT_CODE")
    private String createUnitCode;

    @ApiModelProperty("创建单位(操作人员单位)")
    @TableField("CREATE_UNIT_NAME")
    private String createUnitName;

    @TableField("START_BPM_USERNAME")
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;
}
