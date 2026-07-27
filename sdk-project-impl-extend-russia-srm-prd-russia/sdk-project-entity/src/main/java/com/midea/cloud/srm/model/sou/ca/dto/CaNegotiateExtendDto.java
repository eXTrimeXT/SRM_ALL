package com.midea.cloud.srm.model.sou.ca.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@ApiModel(description = "定标申请投标时间DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CaNegotiateExtendDto extends BaseObjectX {

    @ApiModelProperty(value = "投标谈判表头", example = "投标谈判表头")
    private List<CaNegotiateDto> title;

    @ApiModelProperty("谈判内容")
    private List<List<String>> data;

}
