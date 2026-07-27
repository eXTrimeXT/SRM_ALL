
package com.midea.cloud.srm.model.pj.aihelper;

/**
 * <pre>
 *
 * </pre>
 *
 * @author fu
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/08/52 10:49:18
 *  修改内容:
 * </pre>
 */

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ApiModel(description = "围串标识别DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class FileCheckDto extends BaseDTO {

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("投标文件附件ID")
    private List<Long> orderDocIds;


}

