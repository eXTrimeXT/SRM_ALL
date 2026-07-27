package com.midea.cloud.srm.model.contract.dto;

import com.midea.cloud.srm.model.contract.dto.FileDTO;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ContractSignInfoDTO {
    /**
     * 是否流程签署
     */
    private Integer isProcessSign;
    /**
     *  是否需要用印
     */
    private Integer isSign;
    /**
     *  是否已经完成用印
     */
    private Integer isSignComplete;
    /**
     *  签章类型
     */
    private String sealType;
    /**
     *  用印次数
     */
    private Integer signCount;
    /**
     *  已签署文件
     */
    private String signFiles;
    /**
     *  签署完成时间
     */
    private String signTime;
    /**
     *  签章方式
     */
    private Integer signType;

}