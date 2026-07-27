package com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ExtPrSouRequirementForDataSubmit {

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("需求单号")
    private String requirementHeadNum;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty("递交招标资料时间")
    private LocalDate sendSouProfileEndDate;


}
