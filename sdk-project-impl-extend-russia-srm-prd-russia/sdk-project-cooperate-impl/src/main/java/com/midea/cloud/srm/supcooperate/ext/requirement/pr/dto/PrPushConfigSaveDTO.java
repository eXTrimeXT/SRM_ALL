package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zenghx2
 */
@Data
public class PrPushConfigSaveDTO extends PrPushConfig {

    private List<PrPushNotify> notifyList;
}
