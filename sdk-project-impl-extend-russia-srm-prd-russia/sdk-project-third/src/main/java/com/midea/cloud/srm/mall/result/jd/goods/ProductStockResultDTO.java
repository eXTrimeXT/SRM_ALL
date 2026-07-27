package com.midea.cloud.srm.mall.result.jd.goods;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <pre>
 *  查询商品池编号响应实体类
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
public class ProductStockResultDTO extends JDBaseResult implements CommonResultDTO {

    private List<ProductStock> result;

    @Data
    public static class ProductStock {
        @ApiModelProperty("skuId")
        private Long skuId;

        @ApiModelProperty("入参时传入的区域码area。因京东目前是3、4级地址均支持，存在areaId在传入的3级地址后填充4级地址“_0“后返回的情况")
        private String areaId;

        @ApiModelProperty("库存状态编号。参考枚举值：33,39,40,36,34,99")
        private BigDecimal stockStateId;

        @ApiModelProperty("库存状态描述。以下为stockStateId不同时，此字段不同的返回值：33 有货 现货-下单立即发货 39 有货 在途-正在内部配货，预计2~6天到达本仓库 40 有货 可配货-下单后从有货仓库配货 36 预订 34 无货99 无货开预定，此时desc返回的数值代表预计到货天数，并且该功能需要依赖合同上有无货开预定权限的用户，到货周期略长，谨慎采用该功能")
        private String StockStateDesc;

        @ApiModelProperty("剩余数量。当此值为-1时，为未查询到。StockStateDesc为33：入参的skuNums字段，skuId对应的num<50，此字段为实际库存。入参的skunums字段，skuid对应的50<=num<100，此字段为-1。入参的skunums字段，skuid对应的num>100，此字段等于num。(此种情况并未返回真实京东库存)")
        private Integer remainNum;
    }
}
