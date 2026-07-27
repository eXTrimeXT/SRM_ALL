package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 查询质量维度，研发维度是否已经评分
 * @author: hesl41
 * @Date: 2022/10/26 17:20
 */
@Data
@Accessors(chain = true)
public class GetSupplierScoreManageStatusDTO {

    @ApiModelProperty("ID")
    @TableId("project_id")
    private Long projectId;

    @ApiModelProperty("供应商ID")
    @TableField("vendor_id")
    private Long vendorId;
    /**
     * 质量维度，研发维度，都是一轮。
     * @author: hesl41
     * @Date: 2022/10/26 18:11
     */
    @ApiModelProperty("评分维度编码")
    @TableField("dimension_code")
    private String dimensionCode;


    @ApiModelProperty("轮次")
    @TableField("round")
    private Integer round;


}
