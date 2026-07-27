package com.midea.cloud.srm.sup.dto;

import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupReportFormsDto extends BaseEntity<SupReportFormsDto> implements Serializable {

    private List<Long> supIdList;
    private Long supId;
    private String supName;
    private String supCode;
    private String contactName;
    private String contactTel;
    private String contactEmail;
    /**
     * 注册资金
     */
    private String registeredCapital;
    /**
     * 币种编码
     */
    private String bzCode;
    private String zzLevel;
    /**
     * 公司成立年限
     */
    private String comUp;
    private BigDecimal comUpNum;
    /**
     * 平台注册日期
     */
    private Date regDate;
    private String address;
    private String province;
    private String city;
    private BigDecimal cy;
    private BigDecimal zb;
    private Integer hgNum;
    private Integer bhgNum;

    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 品类
     */
    private String pl;
    private String projectCode;
    /**
     * 参与率
     */
    private BigDecimal cylStart;
    private BigDecimal cylEnd;
    /**
     * 中标率
     */
    private BigDecimal zblStart;
    private BigDecimal zblEnd;
    /**
     * 注册资金
     */
    private BigDecimal zcFundStart;
    private BigDecimal zcFundEnd;
    /**
     * 成立年限
     */
    private String clYearStart;
    private String clYearEnd;

    /**
     * 品类id
     *
     */
    private Long categoryId;

}
