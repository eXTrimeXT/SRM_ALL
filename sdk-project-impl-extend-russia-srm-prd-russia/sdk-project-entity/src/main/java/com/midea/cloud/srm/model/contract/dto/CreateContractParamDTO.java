package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class CreateContractParamDTO {
    /**
     * 合同分类全路径
     */
    private String allContractType;
    /**
     * 业务唯一标识
     */
    private String businessId;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 合同种类
     */
    private Integer contractKind;
    /**
     * 合同名称
     */
    private String contractName;
    /**
     * 相对方
     */
    private List<OppositeCollectDTO> contractOppositeCollects;
    /**
     * 用印信息
     */
    private ContractSignInfoDTO contractSignInfo;
    /**
     * 财务信息
     */
    private ContractFinanceDTO contractFinance;
    /**
     * 经办人
     */
    private String createAccount;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 是否审核完成
     */
    private Integer isCreateComplete;
    /**
     * 是否为正式合同
     */
    private Integer officialFileFlag;
    /**
     * 我方签约主体
     */
    private String signCompanyName;
    /**
     * 我方签署主体社会信用代码
     */
    private String signTaxCode;
    /**
     * 合同来源
     */
    private String source;
    /**
     * 合同状态
     */
    private String state;
    /**
     * 合同分类
     */
    private String tailContractType;
    /**
     * 末级分类ID
     */
    private Integer tailTypeId;
    /**
     * 期限类型
     */
    private Integer timeLimit;
    /**
     * 最后修改人
     */
    private String updateAccount;
    /**
     * 更新时间
     */
    private String updateTime;
    /**
     * 是否法务审核
     */
    private Integer lawyerFlag;
    /**
     * 契约锁合同id
     */
    private String signContractId;
    /**
     * 特殊类型合同
     */
    private Integer speciaType;
    /**
     * 期限开始时间
     */
    private String timeStart;
    /**
     * 期限截止时间
     */
    private String timeEnd;
    /**
     * 无固定期限说明
     */
    private String limitDesc;
    /**
     * 是否免审
     */
    private Integer auditProcess;
    /**
     * 原合同id
     */
    private Long relateContractId;

}







