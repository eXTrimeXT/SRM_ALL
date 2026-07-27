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
@ApiModel(description = "集采台账-调价申请-调价申请附件")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_ch_paa_adjust_att")
public class SccSouChPaaAdjustAtt extends BaseEntity<SccSouChPaaAdjustAtt> {

    @ApiModelProperty("调价申请附件id")
    @TableId("ADJUST_ATT_ID")
    private Long adjustAttId;

    @ApiModelProperty("调价申请id")
    @TableField("ADJUST_ID")
    private Long adjustId;

    @ApiModelProperty("文件id")
    @TableField("FILE_ID")
    private Long fileId;

    @ApiModelProperty("文件名称")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty("备注")
    @TableField("REMAKE")
    private String remake;
}
