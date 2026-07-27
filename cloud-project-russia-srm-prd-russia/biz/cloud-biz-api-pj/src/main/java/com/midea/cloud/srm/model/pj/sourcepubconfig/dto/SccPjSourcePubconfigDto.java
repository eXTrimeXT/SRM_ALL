package com.midea.cloud.srm.model.pj.sourcepubconfig.dto;

import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class SccPjSourcePubconfigDto {
    private String type;
    private SccPjSourcePubconfig sourcePubconfig;
}
