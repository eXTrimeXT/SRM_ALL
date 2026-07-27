package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 招标资料递交指定评标人表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_bid_data_submit_evaluator")
public class BidDataSubmitEvaluator extends BaseEntity {
    /**
     * ID
     */
    @TableId
    private Long submitEvaluatorId;
    /**
     * 员工用户ID
     */
    private Long userId;

    /**
     * 招标资料递ID
     */
    private Long dataSubmitId;

    /**
     * 员工工号
     */
    private String ceeaEmpNo;

    /**
     * 姓名
     */
    private String evaluatorName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 工作年限
     */
    private BigDecimal workYears;

    /**
     * 专家等级
     */
    private String expertLevel;

    /**
     * 角色
     */
    private String evaluatorRole;
}
