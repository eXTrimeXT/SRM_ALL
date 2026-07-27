package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 寻源供方必须上传附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSouFileConfigEditDTO extends BaseObjectX {

    /** @see SouFileConfig#getSouFileConfigId */
    @ApiModelProperty("sou_file_config_id")
    private Long souFileConfigId;

    /** @see SouFileConfig#getFileType */
    @ApiModelProperty(value = "附件类型[字典值: BRG_FILE_CONFIG_TYPE]", required = true)
    private SouFileConfigTypeEnum fileType;

    /** @see SouFileConfig#getRequireDocId */
    @ApiModelProperty(value = "文件ID", required = true)
    private Long requireDocId;

    /** @see SouFileConfig#getRequireFileName */
    @ApiModelProperty(value = "文件名(长度限制150)", required = true)
    private String requireFileName;

    /** @see SouFileConfig#getFileRequire */
    @ApiModelProperty(value = "资料要求(长度限制150)", required = true)
    private String fileRequire;

    /** @see SouFileConfig#getRequireRemark */
    @ApiModelProperty("备注(长度限制300)")
    private String requireRemark;

    /** @see SouFileConfig#getSortIndex */
    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

}
