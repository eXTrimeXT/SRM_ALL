package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangbf3
 */
@Data
@ApiModel("保存技术决议")
public class SaveTechnicalResolutionDTO {

    @ApiModelProperty("供应商评分管理ID")
    @TableId("supplier_score_manage_id")
    private Long supplierScoreManageId;

    /** OK , NG */
    @ApiModelProperty("评分结果")
    @TableField("result")
    private String result;

    @ApiModelProperty("评分说明")
    @TableField("instructions")
    private String instructions;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("评分人上传附件")
    private List<SceneFile> sceneFiles = new ArrayList<>();


}
