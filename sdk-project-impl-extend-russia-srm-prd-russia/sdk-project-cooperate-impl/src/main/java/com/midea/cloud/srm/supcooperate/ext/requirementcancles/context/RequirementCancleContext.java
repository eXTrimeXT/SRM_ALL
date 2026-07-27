package com.midea.cloud.srm.supcooperate.ext.requirementcancles.context;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class RequirementCancleContext extends SdkPluginContext {

    private List<Long> requirementHeadIdList;
    private Map<Long, String> requirementHeadNumMap;
    private HashMap<String, Object> localCache;
}
