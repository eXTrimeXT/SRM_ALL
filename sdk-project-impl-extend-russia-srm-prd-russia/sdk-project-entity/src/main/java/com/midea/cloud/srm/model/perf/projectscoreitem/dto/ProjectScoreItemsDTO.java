package com.midea.cloud.srm.model.perf.projectscoreitem.dto;

import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItemsPerson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *  项目化绩效项目 模型
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-11-07 15:10:37
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "项目化绩效项目DTO")
public class ProjectScoreItemsDTO extends ProjectScoreItems {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "驳回信息")
    private String rejectRemark;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "项目化绩效项目评分人明细")
    List<ProjectScoreItemsPerson> personList = new ArrayList<>();
}
