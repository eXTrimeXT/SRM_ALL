package com.midea.cloud.srm.model.pj.mdm.dto;

import lombok.Data;

import java.util.List;

/**
 * @author fu
 */
@Data
public class MdmTaxCodeResultDto {
/**    社会信用代码 */
    private String taxCode;
    /**    社会信用代码 */
    private List<MdmCompanyDto> supplierInfoList;
}
