package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ex_yipeng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouPlaceOnFileDTO extends BaseObjectX {

    @ApiModelProperty("ID")
    private Long fileId;

    @ApiModelProperty("竞价单ID")
    private Long projectId;

    @ApiModelProperty("附件类型")
    private SouFileTypeEnum fileType;

    @ApiModelProperty("文件ID")
    private Long docId;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("备注")
    private String fileRemark;

}
