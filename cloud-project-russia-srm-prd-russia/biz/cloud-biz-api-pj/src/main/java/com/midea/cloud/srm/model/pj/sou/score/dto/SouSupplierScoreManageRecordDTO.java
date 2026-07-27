package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author huangbf3
 */
@Data
@ApiModel("供应商评分管理记录")
public class SouSupplierScoreManageRecordDTO  {

    @ApiModelProperty("ID")
    @TableId("supplier_score_manage_record_id")
    private Long supplierScoreManageRecordId;

    @ApiModelProperty("评分值")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("评分说明")
    @TableField("instructions")
    private String instructions;

}
