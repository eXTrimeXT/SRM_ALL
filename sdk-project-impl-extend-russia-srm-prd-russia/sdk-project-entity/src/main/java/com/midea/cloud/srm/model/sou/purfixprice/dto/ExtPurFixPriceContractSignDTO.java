package com.midea.cloud.srm.model.sou.purfixprice.dto;

import com.midea.cloud.srm.model.common.enums.Enable;
import lombok.Data;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ExtPurFixPriceContractSignDTO {

    public ExtPurFixPriceContractSignDTO(Long purFixPriceLineId, Enable signContractFlag) {
        this.purFixPriceLineId = purFixPriceLineId;
        this.signContractFlag = signContractFlag;
    }

    private Long purFixPriceLineId;

    private Enable signContractFlag;
}
