package com.midea.cloud.srm.model.sup.info.dto;

import com.midea.cloud.srm.model.supplier.info.entity.ManagementAttach;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ExtManagementAttachDTO extends ManagementAttach {

    /**
     * 证件类型
     */
    private String documentType;
}
