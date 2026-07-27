package com.midea.cloud.srm.model.perf.projectscoreman.dto;

import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItemsPerson;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreMan;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManDetail;
import com.midea.cloud.srm.model.perf.projectscoreman.entity.ProjectScoreManRejectInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/10 9:54
 *  修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
public class ProjectScoreManDTO extends ProjectScoreMan {

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "项目化绩效评分明细")
    List<ProjectScoreManDetail> detailList = new ArrayList<>();

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "驳回信息")
    List<ProjectScoreManRejectInfo> rejectInfoList = new ArrayList<>();
}
