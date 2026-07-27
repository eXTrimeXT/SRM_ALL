package com.midea.cloud.srm.model.perf.ordercheck.dto;

import com.midea.cloud.srm.model.perf.template.dto.PerfTemplateHeaderQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  绩效评分项目供应商表 模型
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:37
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "绩效模型查询dto")
public class PjPerfTemplateHeaderQueryDTO extends PerfTemplateHeaderQueryDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("扩展字段1")
    private String attribute1;

    @ApiModelProperty("扩展字段2")
    private String attribute2;
}
