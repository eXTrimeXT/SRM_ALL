package com.midea.cloud.srm.model.base.entity;

import com.midea.cloud.srm.model.base.material.dto.MaterialItemErrorDto;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/12/12 11:11
 *  修改内容:
 * </pre>
 */
@Data
@Accessors(chain = true)
public class ExtMaterialItemDto implements Serializable {
    @Valid
    private ExtMaterialItem materialItem;

    private List<MaterialItemErrorDto> errorDtos;

    private int number;
}
