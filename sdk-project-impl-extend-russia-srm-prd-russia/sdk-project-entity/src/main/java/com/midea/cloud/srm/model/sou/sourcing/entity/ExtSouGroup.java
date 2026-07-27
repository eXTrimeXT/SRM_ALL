package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@TableName("scc_sou_group")
@Data
@ApiModel("招标&评标工作小组")
public class ExtSouGroup extends SouGroup {

    /**
     * 专家等级
     */
    @ApiModelProperty("专家等级")
    private String extExpertLevel;

    /**
     * 包名
     */
    @ApiModelProperty("包名")
    private String extPackageName;

    /**
     * 是否是招标工作组，Y-是
     */
    @ApiModelProperty("是否是招标工作组，Y-是")
    private String extGroupFlag;

    /**
     * 是否是评标工作组，Y-是，N-否（移除）
     */
    @ApiModelProperty("是否是评标工作组，Y-是，N-否（移除）")
    private String extEvaFlag;

    @ApiModelProperty("移除招标工作组原因")
    private String extRemoveReason;

}
