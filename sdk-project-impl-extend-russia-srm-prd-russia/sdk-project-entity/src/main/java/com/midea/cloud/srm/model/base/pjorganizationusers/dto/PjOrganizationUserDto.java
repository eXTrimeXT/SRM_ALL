package com.midea.cloud.srm.model.base.pjorganizationusers.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 备注
 * @author huangbf3
 */
@Data
@Slf4j
@ApiModel("用户组织权限DTO")
public class PjOrganizationUserDto extends BaseDTO {

    @ApiModelProperty("组织与用户关系ID")
    private Long organizationUserRelId;
    @ApiModelProperty("组织ID")
    private Long organizationId;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("来源")
    private String sourceFrom;
    @ApiModelProperty("全路径虚拟ID")
    private String fullPathId;
    @ApiModelProperty("生效日期")
    private Date startDate;
    @ApiModelProperty("失效日期")
    private Date endDate;


}
