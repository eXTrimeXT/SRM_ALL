package com.midea.cloud.srm.model.supcooperate.ext.division.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 批量更新负责人请求参数
 * @author huangbf3
 */
@Data
public class PersonInChargeUserDto {

    @ApiModelProperty("负责人名称")
    private String personInChargeNickname;
    @ApiModelProperty("负责人用户名")
    private String personInChargeUsername;
    @ApiModelProperty("负责人用户ID")
    private Long personInChargeUserId;
    @ApiModelProperty("品类分工ID")
    List<Long> divisionCategoryIds;
}
