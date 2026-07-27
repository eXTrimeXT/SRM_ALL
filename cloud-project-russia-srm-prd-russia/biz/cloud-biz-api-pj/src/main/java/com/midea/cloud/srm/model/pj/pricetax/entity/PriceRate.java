package com.midea.cloud.srm.model.pj.pricetax.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author huangbf3
 */
@Data
public class PriceRate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 汇率
     */
    private BigDecimal rate;

    /**
     * 对应货币
     */
    private String correspondingCurrency;

    /**
     * 基本货币
     */
    private String tradingCurrency;

    /**
     * 货币对
     */
    private String currencyPair;

    /**
     * 国家地区编码
     */
    private String countryRegionCode;

    /**
     * 国家地区名称
     */
    private String countryRegionName;

    /**
     * 汇率日期
     */
    private Date rateDate;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 顺序号
     */
    private String sortNo;

    /**
     * 创建人工号
     */
    private String createUserCode;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新人工号
     */
    private String updateUserCode;

    /**
     * 更信任姓名
     */
    private String updateUserName;

    /**
     * 删除标识
     */
    private String deleteFlag;

    /**
     * 启用/禁用标识
     */
    private Boolean activeFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本
     */
    private String version;
}
