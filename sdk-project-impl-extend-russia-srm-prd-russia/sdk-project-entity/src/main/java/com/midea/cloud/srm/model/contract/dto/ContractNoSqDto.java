package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ContractNoSqDto {

    /**
     * id
     */
    private Long contractNoSeqId;


    private Long contractHeadId;

    /**
     * 原合同号
     */
    private String contractNo;

    /**
     *  序列
     */
    private Long contractExtMaxSeq;



}
