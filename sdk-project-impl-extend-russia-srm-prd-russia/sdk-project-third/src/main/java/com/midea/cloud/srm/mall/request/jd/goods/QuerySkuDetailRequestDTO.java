package com.midea.cloud.srm.mall.request.jd.goods;

import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <pre>
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/2/28 14:28
 *  修改内容:
 * </pre>
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuerySkuDetailRequestDTO extends BaseRequestDTO {
    @ApiModelProperty("商品编号，只支持单个查询")
    private String sku;
    @ApiModelProperty("以下为商品维度扩展字段，当入参输入某个扩展字段后，出参会返回该字段对应的出参。可以根据需要选用")
    private Integer queryExts;
}
