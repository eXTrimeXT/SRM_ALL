package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.competition.utils.DecimalUtil;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemHis;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouOrderItemWebVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouCurrencyVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderFileVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 项目式询价openAPI - 报价详情
 * PS: 参考 {@link ApiCompSouOrderDetailVO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouOrderDetailWebVO extends BaseObjectX {

    @ApiModelProperty("寻源立项信息")
    private ApiCompSouInitDetailVO initInfo;
    @ApiModelProperty("指定轮次报价单")
    private SouOrder order;
    @ApiModelProperty("物料需求")
    private List<CompSouOrderItemWebVO> itemList;
    @ApiModelProperty("报价附件")
    private List<ApiSouOrderFileVO> orderFileList;

    public static CompSouOrderDetailWebVO convertCompVO(ApiSouOrderDetailVO orderVO, List<CompSouItemHis> compItemHisList) {
        CompSouOrderDetailWebVO vo = SouObjectXUtil.convertTargetObj(orderVO, CompSouOrderDetailWebVO.class);
        Map<Long/* souItemId */, ApiCompSouItemVO> compItemMap = vo.getInitInfo().getRequireInfo().stream()
                .collect(Collectors.toMap(ApiCompSouItemVO::getSouItemId, Function.identity()));
        Map<Long/* souItemId */, CompSouItemHis> compItemHisMap = compItemHisList.stream()
                .collect(Collectors.toMap(CompSouItemHis::getSouItemId, Function.identity()));
        Map<String/* currencyCode */, ApiCompSouCurrencyVO> currencyMap = vo.getInitInfo().getProjectInfo().getCurrencyList()
                .stream().collect(Collectors.toMap(ApiCompSouCurrencyVO::getCurrencyCode, Function.identity()));
        boolean isPriceNotax = Enable.Y.equals(vo.getInitInfo().getProjectInfo().getIsPriceNotax());
        ApiCompSouInitProjectVO projectInfo = vo.getInitInfo().getProjectInfo();
        boolean isLowerPricing = SouScoreRuleTypeEnum.MIN_PRICE.equals(projectInfo.getScoreRuleType())
                || SouScoreRuleTypeEnum.COMPOSITE_PRICE.equals(projectInfo.getScoreRuleType());

        // 处理推荐报价
        vo.getItemList().forEach(orderItem -> {
            ApiCompSouItemVO compItem = compItemMap.get(orderItem.getSouItemId());
            CompSouItemHis compItemHis = compItemHisMap.get(orderItem.getSouItemId());
            ApiCompSouCurrencyVO currency = currencyMap.get(orderItem.getOrderCurrency());

            if (orderItem.getLatestOrderNotaxPrice() != null) {
                boolean hasLatestPrice = isPriceNotax ? compItemHis.getLatestOrderNotaxPrice() != null : compItemHis.getLatestOrderTaxPrice() != null;
                BigDecimal availablePrice = isPriceNotax ?
                        (hasLatestPrice ? compItemHis.getLatestOrderNotaxPrice() : compItem.getStartOrderNotaxPrice())
                        : (hasLatestPrice ? compItemHis.getLatestOrderTaxPrice() : compItem.getStartOrderTaxPrice());
                if (isLowerPricing) {
                    // 合理低价法(反向竞价)
                    if (projectInfo.getMinPercent() != null) {
                        // 如果当前轮次有最新报价，则必须小于等于(最新报价 * (100% - 降幅) / 100);否则必须大于等于(起始价 * (100% - 降幅) / 100)
                        availablePrice = DecimalUtil.B_100.subtract(projectInfo.getMinPercent())
                                .divide(DecimalUtil.B_100, 8, RoundingMode.DOWN)
                                .multiply(availablePrice)
                                .setScale(currency.getPricePrecision(), RoundingMode.DOWN);
                    } else {
                        // 如果当前轮次有最新报价，则必须大于等于(最新报价 - 最小金额);否则必须大于等于(起始价 - 最小金额)
                        // PS: 如果当前最新报价小于等于最小金额，那么就不做限制了，否则可能报0或者负数
                        availablePrice = availablePrice.compareTo(projectInfo.getMinAmount()) <= 0 ?
                                availablePrice.setScale(currency.getPricePrecision(), RoundingMode.DOWN)
                                :
                                availablePrice.subtract(projectInfo.getMinAmount()).setScale(currency.getPricePrecision(), RoundingMode.DOWN);
                    }
                } else {
                    // 合理高价发(正向竞价)
                    if (projectInfo.getMinPercent() != null) {
                        // 如果当前轮次有最新报价，则必须小于等于(最新报价 * (100% - 降幅) / 100);否则必须大于等于(起始价 * (100% - 降幅) / 100)
                        availablePrice = DecimalUtil.B_100.add(projectInfo.getMinPercent())
                                .divide(DecimalUtil.B_100, 8, RoundingMode.DOWN)
                                .multiply(availablePrice)
                                .setScale(currency.getPricePrecision(), RoundingMode.DOWN);
                    } else {
                        // 如果当前轮次有最新报价，则必须大于等于(最新报价 - 最小金额);否则必须大于等于(起始价 - 最小金额)
                        // PS: 如果当前最新报价小于等于最小金额，那么就不做限制了，否则可能报0或者负数
                        availablePrice = availablePrice.compareTo(projectInfo.getMinAmount()) <= 0 ?
                                availablePrice.setScale(currency.getPricePrecision(), RoundingMode.DOWN)
                                :
                                availablePrice.add(projectInfo.getMinAmount()).setScale(currency.getPricePrecision(), RoundingMode.DOWN);
                    }
                }
                if (isPriceNotax) {
                    orderItem.setRecommendOrderNotaxPrice(availablePrice);
                } else {
                    orderItem.setRecommendOrderTaxPrice(availablePrice);
                }
            } else {
                orderItem.setRecommendOrderNotaxPrice(compItem.getStartOrderNotaxPrice());
                orderItem.setRecommendOrderTaxPrice(compItem.getStartOrderTaxPrice());
            }
        });

        return vo;
    }

}
