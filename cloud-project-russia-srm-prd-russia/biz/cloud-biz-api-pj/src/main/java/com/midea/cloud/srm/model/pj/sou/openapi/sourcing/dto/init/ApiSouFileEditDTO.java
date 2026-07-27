package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 寻源附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSouFileEditDTO extends BaseObjectX {

    /** @see SouFile#getSouFileId */
    @ApiModelProperty("寻源核心-附件表ID")
    private Long souFileId;

    /** @see SouFile#getSouDocId */
    @ApiModelProperty(value = "文件ID", required = true)
    private Long souDocId;

    /** @see SouFile#getSouFileName */
    @ApiModelProperty(value = "文件名(长度限制150)", required = true)
    private String souFileName;

    /** @see SouFile#getFileType */
    @ApiModelProperty(value = "附件类型 [枚举：SouFileTypeEnum 字典：SOU_FILE_TYPE]", required = true)
    private SouFileTypeEnum fileType;

    /** @see SouFile#getSouRemark */
    @ApiModelProperty("说明(长度限制300)")
    private String souRemark;

    /** @see SouFile#getSortIndex */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

}
