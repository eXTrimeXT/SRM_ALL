package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class OppositeCollectDTO {
    /** 文件列表 */
    private List<ContractFileDTO> contractFiles;
    /** 是否涉及美国成分分析 */
    private Integer isAmericaIngredient;
    /** 是否签订阳光协议 */
    private Integer isJusticeAgreement;
    /** 是否签订保密协议 */
    private Integer isSecretAgreement;
    /** 相对方名称 */
    private String oppositeName;
    /** 签章类型 */
    private Integer sealType;
    /** 签署序号 */
    private Integer serial;
    /** 是否签署完成 */
    private Integer signState;
    /** 相对方编码 */
    private String supplierCode;
    /** 相对方社会信用代码 */
    private String taxCode;
    /** 单位类型 */
    private String unitType;
    /** 相对方id */
    private Long oppositeId;
    /** 阳光诚信状态 */
    private Integer creditState;
    /** 不签订阳光协议申请书 */
    private String notJusticeFile;
    /** 签章系统印章id */
    private Long sealId;
    /** 签章系统印章名称 */
    private String sealName;
}