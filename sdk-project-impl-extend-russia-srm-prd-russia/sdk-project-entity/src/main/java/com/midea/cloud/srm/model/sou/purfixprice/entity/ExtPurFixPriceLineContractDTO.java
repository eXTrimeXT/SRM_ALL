package com.midea.cloud.srm.model.sou.purfixprice.entity;

import com.mideacloud.common.util.BeanUtil;
import lombok.Data;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class ExtPurFixPriceLineContractDTO extends ExtPurFixPriceLine {

    private Long createUserOrgOuId;

    private String createUserOrgCode;

    private String createUserOrgName;

    public static ExtPurFixPriceLineContractDTO makeExtPurFixPriceLineContractDTO(ExtPurFixPriceLine extPurFixPriceLine,ExtPurFixPriceHead extPurFixPriceHead){
        ExtPurFixPriceLineContractDTO extPurFixPriceLineContractDTO = BeanUtil.copyProperties(extPurFixPriceLine,ExtPurFixPriceLineContractDTO.class);
        extPurFixPriceLineContractDTO.setCreateUserOrgOuId(extPurFixPriceHead.getCreateUserOrgOuId());
        extPurFixPriceLineContractDTO.setCreateUserOrgCode(extPurFixPriceHead.getCreateUserOrgOuCode());
        extPurFixPriceLineContractDTO.setCreateUserOrgName(extPurFixPriceHead.getCreateUserOrgOuName());
        return extPurFixPriceLineContractDTO;
    }
}
