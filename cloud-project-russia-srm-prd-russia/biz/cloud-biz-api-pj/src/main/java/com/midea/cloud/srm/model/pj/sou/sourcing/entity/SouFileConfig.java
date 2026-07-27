package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 寻源核心 - 供方必须上传附件配置
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_file_config")
@ApiModel("供方必须上传附件")
public class SouFileConfig extends BaseEntity<SouFileConfig> {

    @TableId("SOU_FILE_CONFIG_ID")
    @ApiModelProperty("sou_file_config_id")
    private Long souFileConfigId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @TableField("FILE_TYPE")
    @ApiModelProperty("附件类型[字典值: BRG_FILE_CONFIG_TYPE]")
    private SouFileConfigTypeEnum fileType;

    @TableField("REQUIRE_DOC_ID")
    @ApiModelProperty("文件ID")
    private Long requireDocId;

    @TableField("REQUIRE_FILE_NAME")
    @ApiModelProperty("文件名")
    private String requireFileName;

    @TableField("FILE_REQUIRE")
    @ApiModelProperty("资料要求")
    private String fileRequire;

    @TableField("REQUIRE_REMARK")
    @ApiModelProperty("备注")
    private String requireRemark;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
