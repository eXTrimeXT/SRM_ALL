package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItemFollow;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源openAPI - 评选列表信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSelectQueryVO extends ApiSouOrderItemVO {

    /** @see SouProject#getStandardCurrency */
    @ApiModelProperty("本位币")
    private String standardCurrency;

    /** @see SouProject#getScoreRuleType  */
    @ApiModelProperty("评分规则")
    private SouScoreRuleTypeEnum scoreRuleType;

    @ApiModelProperty("后续单据记录信息")
    private List<SouOrderItemFollow> followList;

    /**原型字段*/
    @ApiModelProperty("计算供应商及金额")
    private ApiSouSelectResultVO selectVendorPriceDTO;
    /**单价*月产量*/
    @ApiModelProperty("月总金额")
    private BigDecimal monthlyTotalAmount;

}
