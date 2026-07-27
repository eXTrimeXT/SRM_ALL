package com.midea.cloud.srm.mall.result.jd.goods;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
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
public class SellPriceResultDTO extends JDBaseResult implements CommonResultDTO {

    private List<SellPrice> result;

    @Data
    public static class SellPrice {
        @ApiModelProperty("skuId")
        private Long skuId;

        @ApiModelProperty("京东价。仅供参考")
        private BigDecimal jdPrice;

        @ApiModelProperty("京东销售价（含税价下单模式时返回含税单价；未税价下单模式时返回未税单价）。")
        private BigDecimal price;

        @ApiModelProperty("价格描述")
        private String priceDesc;

        @ApiModelProperty("入参中的queryExts中包含marketPrice时，输出此字段。京东的前台划线价。现在只有图书频道能露出，其他的因政策原因已不允许展示。仅供参考")
        private BigDecimal marketPrice;

        @ApiModelProperty("税率。当queryExts中包含containsTax时，出参中有此字段。例如：此值为16时，代表税率为“16%”。")
        private BigDecimal tax;

        @ApiModelProperty("预估税额。当queryExts中包含containsTax时，出参中有此字段。")
        private BigDecimal taxPrice;

        @ApiModelProperty("未税价。当queryExts中包含containsTax或nakedPrice时，出参中有此字段。① 入参containsTax：此字段代表含税价订单体系下，单品的预估未税价，仅作页面展示，不作为订单、票面中最终的未税单价，因为下单后会有运费分摊、发票尾差校验等处理逻辑；② 入参nakedPrice：此字段代表未税价订单体系下，单品的未税单价，此时price= nakedPrice。")
        private BigDecimal nakedPrice;
    }
}
