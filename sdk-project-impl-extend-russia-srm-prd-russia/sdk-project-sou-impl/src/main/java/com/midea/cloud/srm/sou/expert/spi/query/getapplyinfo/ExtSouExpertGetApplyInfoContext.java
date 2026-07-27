package com.midea.cloud.srm.sou.expert.spi.query.getapplyinfo;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 专家库 - 查询专家申请详情上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertGetApplyInfoContext extends SdkPluginContext {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("入参: 专家申请ID")
    private Long expertApplyId;

    @Nullable
    @ApiModelProperty("专家申请信息(IExtSouExpertGetApplyInfoPlugin#judgeGetApplyInfoAuth环节填补)")
    private ExtSouExpertApply expertApply;

    @Nullable
    @ApiModelProperty("查询结果")
    private ExtSouExpertApplyVO result;

    public ExtSouExpertGetApplyInfoContext(Long expertApplyId) {
        this.expertApplyId = expertApplyId;
    }

}
