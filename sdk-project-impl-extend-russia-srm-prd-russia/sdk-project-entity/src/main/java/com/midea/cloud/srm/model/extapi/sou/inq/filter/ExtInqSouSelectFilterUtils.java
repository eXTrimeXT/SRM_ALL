package com.midea.cloud.srm.model.extapi.sou.inq.filter;

import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@ApiModel("询价管理评选结果过滤工具类")
public class ExtInqSouSelectFilterUtils {

    /**
     * 评选时过滤报价为空和报价为0的过滤器
     * @return
     */
    public static Predicate<SouOrderItem> selectOrderPriceWithoutNullOrZero() {
        /** 过滤报价为0的数据 */
        return new Predicate<SouOrderItem>() {
            @Override
            public boolean test(SouOrderItem souOrderItem) {
                if(Objects.isNull(souOrderItem.getOrderNotaxPrice()) || BigDecimal.ZERO.compareTo(souOrderItem.getOrderNotaxPrice()) == 0) {
                    return false;
                }
                return true;
            }
        };
    }
}
