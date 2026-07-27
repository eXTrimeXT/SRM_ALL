package com.midea.cloud.srm.model.sou.deposit.entity;

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
@ApiModel(description = "财务-调用记录")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_sou_finance_use_record")
public class FinanceUseRecord extends BaseEntity<FinanceUseRecord> {

    @ApiModelProperty("主键")
    @TableId("RECORD_ID")
    private Long recordId;

    @ApiModelProperty("来源系统单据号")
    @TableField("SYSTEM_SOURCE_NO")
    private String systemSourceNo;

    @ApiModelProperty("意向金Y/保证金B")
    @TableField("RECORD_TYPE")
    private String recordType;

    /*@ApiModelProperty("应收单撤销查询调用标识Y/N")
    @TableField("RECEIVABLE_FLAG")
    private String receivableFlag;

    @ApiModelProperty("认领结果查询调用标识Y/N")
    @TableField("CLAIM_FLAG")
    private String claimFlag;*/

    @ApiModelProperty("认领状态")
    @TableField("CLAIM_STATUS")
    private String claimStatus;

}
