package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.remverequire;

import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.remverequire.RequirementInitRemoveContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招标计划 - 立项编辑上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrSouRequirementInitRemoveContext extends RequirementInitRemoveContext {

    /** 返回值 result 指代 {@link ExtPrSouRequirementHeadVO} */

}
