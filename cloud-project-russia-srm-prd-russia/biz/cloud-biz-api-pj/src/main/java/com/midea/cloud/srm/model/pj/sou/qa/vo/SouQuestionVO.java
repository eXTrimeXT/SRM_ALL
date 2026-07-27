package com.midea.cloud.srm.model.pj.sou.qa.vo;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.qa.entity.SouQuestion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author zhuomb1@midea.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020/4/21 8:47
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("质疑信息")
public class SouQuestionVO extends SouQuestion {

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("质疑文件")
    private List<SceneFile> sceneFiles = new ArrayList<>();


}