package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 招标资料递交竞价明细表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_bid_data_submit_details")
public class BidDataSubmitDetails extends BaseEntity {
    /**
     * ID
     */
    @TableId
    private Long submitDetailsId;

    /**
     * 招标资料递ID
     */
    private Long dataSubmitId;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 组合
     */
    private String combination;

    /**
     * 所属单位
     */
    private String affiliatedUnit;

    /**
     * 履约保证金(元)
     */
    private BigDecimal performDeposit;

    /**
     * 预付款(元)
     */
    private BigDecimal advanceAmount;

    /**
     * 月约产量
     */
    private BigDecimal monthProduction;

    /**
     * 计量单位
     */
    private String meteringUnit;

    /**
     * 起拍价(元)
     */
    private BigDecimal startBidPrice;

    /**
     * 梯次价(元)
     */
    private BigDecimal echelonBidPrice;


}
