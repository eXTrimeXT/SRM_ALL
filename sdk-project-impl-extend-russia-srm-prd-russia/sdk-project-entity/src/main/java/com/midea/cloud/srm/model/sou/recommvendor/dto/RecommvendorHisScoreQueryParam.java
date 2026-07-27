package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 备注
 * @author bs
 */
@ApiModel("供应商推荐快速查询请求参数")
@Data
public class RecommvendorHisScoreQueryParam extends BaseDTO {

    @ApiModelProperty("ID")
    private Long projectId;

    @ApiModelProperty("合并申请单号")
    private String applicantNo;

    @ApiModelProperty("供应商推荐单供应商列表")
    private List<RecommvendorDto> recommvendorList;

    /**
     * 评分时间大于-查询条件
     */
    @ApiModelProperty(value = "评分时间大于-查询条件")
    @TableField(exist = false)
    private LocalDate calcDateStart = LocalDate.now().minusYears(3);

    /**
     * 评分时间小于-查询条件
     */
    @ApiModelProperty(value = "评分时间小于-查询条件")
    @TableField(exist = false)
    private LocalDate calcDateEnd = LocalDate.now();

}
