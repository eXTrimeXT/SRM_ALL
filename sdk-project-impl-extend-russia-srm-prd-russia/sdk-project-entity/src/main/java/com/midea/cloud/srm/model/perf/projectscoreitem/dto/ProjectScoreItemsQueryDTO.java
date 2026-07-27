package com.midea.cloud.srm.model.perf.projectscoreitem.dto;

import com.midea.cloud.srm.model.perf.projectscoreitem.entity.ProjectScoreItems;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
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
 *  修改日期: 2023/11/7 16:51
 *  修改内容:
 * </pre>
 */
@Data
public class ProjectScoreItemsQueryDTO extends ProjectScoreItems {

    @ApiModelProperty("创建时间从")
    private LocalDate createStartDate;

    @ApiModelProperty("创建时间到")
    private LocalDate createEndDate;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("项目状态数组")
    private List<String> projectStatusList = new ArrayList<>();

}
