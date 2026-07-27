package com.midea.cloud.srm.mall.result.jd.goods;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <pre>
 *  商品可采校验响应实体类
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
public class TotalCheckNewResultDTO extends JDBaseResult implements CommonResultDTO {

    private List<TotalCheckNew> result;

    @Data
    public static class TotalCheckNew {
        @ApiModelProperty("skuId")
        private Long skuId;

        @ApiModelProperty("true 代表可采 false 代表不可采")
        private Boolean canPurchase;

        @ApiModelProperty("不可采的时候需要返回具体的不可采原因")
        private String message;
    }
}
