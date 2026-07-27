package com.midea.cloud.srm.model.sou.designplans.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ex_liuxy46
 */
@ApiModel(description = "集采台账-需求信息-项目策划方案-工作日程")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_demand_work")
public class SccSouChDemandWork extends BaseEntity<SccSouChDemandWork> {

    @ApiModelProperty("工作日程id")
    @TableId("WORK_ID")
    private Long workId;

    @ApiModelProperty("提报策划方案id")
    @TableField("DESIGN_ID")
    private Long designId;

    @ApiModelProperty("数据统计")
    @TableField("DATA_STA")
    private String dataSta;

    @ApiModelProperty("需求分析")
    @TableField("REQ_STA")
    private String reqSta;

    @ApiModelProperty("供方资源开发")
    @TableField("SUP_DEV")
    private String supDev;

    @ApiModelProperty("策划方案编写")
    @TableField("PLAN_WRITE")
    private String planWrite;

    @ApiModelProperty("询比价环节")
    @TableField("INQ_PRO")
    private String inqPro;

    @ApiModelProperty("定厂申请")
    @TableField("FAC_APL")
    private String facApl;

    @ApiModelProperty("方案签批")
    @TableField("PRO_SIGN")
    private String proSign;

    @ApiModelProperty("合同签署")
    @TableField("CON_SIGN")
    private String conSign;

}
