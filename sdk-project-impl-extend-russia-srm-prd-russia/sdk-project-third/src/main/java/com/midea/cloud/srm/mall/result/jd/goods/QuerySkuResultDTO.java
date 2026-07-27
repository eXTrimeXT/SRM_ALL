package com.midea.cloud.srm.mall.result.jd.goods;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <pre>
 *  查询商品编号响应实体类
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
public class QuerySkuResultDTO extends JDBaseResult implements CommonResultDTO {

    private Sku result;

    @Data
    public static class Sku {
        @ApiModelProperty("偏移量")
        private Long offset;

        @ApiModelProperty("skuId集合")
        private List<String> skus;
    }
}
