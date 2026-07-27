package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@ApiModel("供应商推荐查询近期中落标情况实体类")
public class RecommvendorSouNoticeInfoDto extends BaseDTO {

    @ApiModelProperty("招标单ID")
    private Long projectId;
    @ApiModelProperty("招标单号")
    private String extProjectNo;
    @ApiModelProperty("招标单名称")
    private String souName;
    @ApiModelProperty("品类ID")
    private Long extCategoryId;
    @ApiModelProperty("招标单状态")
    private String projectStatus;
    @ApiModelProperty("供应商ID")
    private Long vendorId;
    @ApiModelProperty("供应商编码")
    private String vendorCode;
    @ApiModelProperty("供应商名称")
    private String vendorName;


}
