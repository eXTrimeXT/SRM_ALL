package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.copy;

import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.copyrequire.RequirementInitCopyContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招标计划 - 复制上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrSouRequirementInitCopyContext extends RequirementInitCopyContext {

    /** 参数 editDTO 指代 {@link ExtPrSouRequirementHeadDTO} */

}
