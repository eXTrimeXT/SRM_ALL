package com.midea.cloud.srm.sou.meiql.inspect.dto;

import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class InspectSaveDTO extends Inspect {

    private List<InspectVendor> vendorList;
    private List<InspectAttach> attachList;
}
