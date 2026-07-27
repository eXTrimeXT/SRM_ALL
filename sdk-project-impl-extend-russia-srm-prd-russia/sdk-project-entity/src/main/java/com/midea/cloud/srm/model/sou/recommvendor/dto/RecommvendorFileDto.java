package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "供应商推荐附件表DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RecommvendorFileDto extends BaseDTO {

    @ApiModelProperty("ID")
    private Long souFileId;
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("文件ID")
    private Long souDocId;
    @ApiModelProperty("文件名称")
    private String souFileName;
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("备注")
    private String souRemark;
    @ApiModelProperty("排序")
    private Integer sortIndex;
    @ApiModelProperty("包名")
    private String extPackageName;
    @ApiModelProperty("推荐供应商单号")
    private String extRecommendNo;

}
