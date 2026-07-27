package com.midea.cloud.srm.model.pj.sou.inq.dto.webapi.select;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItemPayment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

/**
 * 简易询价 - 用于批量修改账期参数实体
 *
 * @author zhangwk12@meicloud.com
 * @since 2021/11/3
 */
@Data
@ApiModel(description = "简易询价 - 用于批量修改账期参数实体")
public class InqSouSelectPaymentWebDTO {

    /**
     * 询价单ID
     *
     * @see InqSouOrderItemPayment#getProjectId()
     */
    @ApiModelProperty("询价单ID")
    private Long projectId;

    /**
     * 报价行ID
     *
     * @see InqSouOrderItemPayment#getOrderItemId()
     */
    @ApiModelProperty("报价行ID集合")
    private List<Long> orderItemIds;

    /**
     * 付款信息
     * @see InqSouOrderItemPayment
     */
    private List<InqSouOrderItemPayment> paymentList;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            throw new IllegalArgumentException("请选择需要修改账期的数据");
        }
        if (paymentList == null || paymentList.isEmpty()) {
            throw new IllegalArgumentException("请输入付款条款信息");
        }
        int index = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (InqSouOrderItemPayment payment : paymentList) {
            index++;
            // ID(置空)
            payment.setOrderItemPaymentId(null);
            // 询价单ID(置空)
            payment.setProjectId(null);
            // 询价行ID(置空)
            payment.setSouItemId(null);
            // 报价单ID(置空)
            payment.setOrderId(null);
            // 报价单行ID(置空)
            payment.setOrderItemId(null);
            // 付款账期
            payment.setPaymentPeriod(StringUtils.trimToNull(payment.getPaymentPeriod()));
            if (payment.getPaymentPeriod() == null) {
                throw new IllegalArgumentException(MessageFormat.format("付款条款第{0}行请选择付款账期", index));
            }
            // 付款条件
            payment.setPaymentCondition(StringUtils.trimToNull(payment.getPaymentCondition()));
            if (payment.getPaymentCondition() == null) {
                throw new IllegalArgumentException(MessageFormat.format("付款条款第{0}行请选择付款条件", index));
            }
            // 付款方式
            payment.setPaymentMode(StringUtils.trimToNull(payment.getPaymentMode()));
            if (payment.getPaymentMode() == null) {
                throw new IllegalArgumentException(MessageFormat.format("付款条款第{0}行请选择付款方式", index));
            }
            // 付款比例
            if (payment.getPaymentProportion() == null) {
                throw new IllegalArgumentException(MessageFormat.format("付款条款第{0}行请输入付款比例", index));
            } else {
                total = total.add(payment.getPaymentProportion());
            }
            // 付款阶段
            payment.setPaymentPhase(StringUtils.trimToNull(payment.getPaymentPhase()));
            if (payment.getPaymentPhase() != null) {
                if (payment.getPaymentPhase().length() > 100) {
                    throw new IllegalArgumentException(MessageFormat.format("付款条款第{0}行付款阶段长度不能超过100", index));
                }
            }
            // 排序
            payment.setSortIndex(index);
        }
        int num = 100;
        if (total.compareTo(new BigDecimal(num)) != 0) {
            throw new IllegalArgumentException("付款条款的付款比例总和必须是100%");
        }
    }
}
