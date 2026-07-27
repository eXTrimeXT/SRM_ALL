package com.midea.cloud.srm.model.perf.projectscore.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreDim;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ProjectScoreHeaderDTO extends ProjectScoreHeader {

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "维度信息")
    private List<ProjectScoreDim> dimList = new ArrayList<>();

}
