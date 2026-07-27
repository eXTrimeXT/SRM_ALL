package com.midea.cloud.srm.model.pj.sou.technicalexchange.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * 功能名称
 * </pre>
 *
 * @author ex_nongtb@partner.midea.com
 * @version 1.00.00
 *
 * <pre>
 * 修改记录
 * 修改后版本:
 * 修改人: ex_nongtb
 * 修改日期: 2022/5/5 9:16
 * 修改内容:
 * </pre>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SouTechnicalExchangeParam {

    private Long technicalExchangeId;
    /**
     * 交流单号
     */
    private String technicalExchangeFormCode;
    /**
     * 交流标题
     */
    private String technicalExchangeTitle;
    /**
     * 业务实体ID
     */
    private Long orgOuId;
    /**
     * 业务实体编码
     */
    private String orgOuCode;
    /**
     * 业务实体名称
     */
    private String orgOuName;
    /**
     * 交流类型
     */
    private String technicalExchangeType;
    /**
     * 单据状态
     */
    private String technicalExchangeFormStatus;

    /**
     * 单据状态-不等于
     */
    private String tecExcFormStatusNe;
    /**
     * 反馈状态
     */
    private String feedbackStatus;
    /**
     * 供应商编码
     */
    private String vendorCode;

    /**
     * 创建人
     */
    private String createdBy;
}
