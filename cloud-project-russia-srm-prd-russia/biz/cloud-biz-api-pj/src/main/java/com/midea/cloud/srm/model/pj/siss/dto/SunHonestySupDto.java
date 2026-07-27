package com.midea.cloud.srm.model.pj.siss.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**对接阳光诚信自助平台相关-推送用户信息实体
 * @author GW00311146
 */
@Data
@ApiModel(description = "对接阳光诚信自助平台相关-推送用户信息实体")
public class SunHonestySupDto {
    @ApiModelProperty("外部用户来源类型（1：渠道  2：DPS）必填，默认为 3 ")
    private String sourceType;

    @ApiModelProperty("联系电话必填")
    private String username;

    @ApiModelProperty("公司统一社会信用码必填")
    private String nickName;

    @ApiModelProperty("公司名称必填")
    private String companyName;

    @ApiModelProperty("邮箱必填")
    private String email;

    @ApiModelProperty("注册人姓名必填")
    private String contact;

    @ApiModelProperty("阳光诚信平台id")
    private String resultId;


}
