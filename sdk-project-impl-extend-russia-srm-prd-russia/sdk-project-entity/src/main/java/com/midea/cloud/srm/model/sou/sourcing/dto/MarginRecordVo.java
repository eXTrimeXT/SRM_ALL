package com.midea.cloud.srm.model.sou.sourcing.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/20
 */
@Data
public class MarginRecordVo {
    /**
     * 退款账户
     */
    private String refundAccount;
    /**
     * 退款户名
     */
    private String refundAccountName;
    /**
     * 退款银行
     */
    private String refundBank;
    /**
     * 扣款(退款)金额（万元）
     */
    private BigDecimal amount;
    /**
     * 附件ID
     */
    private Long fileId;
    /**
     * 附件名称
     */
    private String fileName;
    /**
     * 扣款（退款）说明
     */
    private String description;
    /**
     * 退款状态
     */
    private String refundStatus;
    /**
     * 退款时间
     */
    private Date refundPaymentDate;
}
