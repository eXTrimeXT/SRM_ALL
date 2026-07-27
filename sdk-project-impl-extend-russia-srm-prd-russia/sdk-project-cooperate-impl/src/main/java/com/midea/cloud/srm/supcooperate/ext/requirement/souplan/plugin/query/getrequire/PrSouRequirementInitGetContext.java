package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.query.getrequire;

import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.query.getrequire.RequirementInitGetContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招标计划 - 列表查询上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrSouRequirementInitGetContext extends RequirementInitGetContext {

    /** 返回值 result 指代 {@link ExtPrSouRequirementHeadVO} */

}
