package com.midea.cloud.srm.model.pj.sou.score.dto;

import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel("保存评分记录")
public class SaveSouSupplierScoreManageRecordDTO {

    @ApiModelProperty("supplierScoreManageId")
    private Long supplierScoreManageId;

    @ApiModelProperty("评分意见")
    @TableField("comments")
    private String comments;

    @ApiModelProperty("评分结果")
    @TableField("result")
    private String result;

    /** 供应商评分记录 */
    private List<SouSupplierScoreManageRecordDTO> list;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("评分人上传附件")
    private List<SceneFile> sceneFiles = new ArrayList<>();

    /** 保存true,暂存false */
    @ApiModelProperty("保存true,暂存false")
    private Boolean save;



}
