package com.midea.cloud.srm.sou.expert.spi.event.removeapply;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 专家库 - 删除专家申请上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertRemoveApplyContext extends SdkPluginContext {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("入参: 专家申请ID")
    private Long expertApplyId;

    @ApiModelProperty("专家申请(IExtSouExpertRemoveApplyPlugin#judgeRemoveApplyAuth环节填补)")
    private ExtSouExpertApply expertApply;

    @ApiModelProperty("被删除的数据")
    private ExtSouExpertApplyVO result;

    public ExtSouExpertRemoveApplyContext(long expertApplyId) {
        this.expertApplyId = expertApplyId;
    }

}
