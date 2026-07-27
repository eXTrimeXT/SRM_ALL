package com.midea.cloud.srm.sou.expert.spi.query.getlatestapplyinfo;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertLatestApplyQueryDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertApplyVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 专家库 - 查询用户的最新专家申请详情上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertGetLatestApplyInfoContext extends SdkPluginContext {

    @ApiModelProperty("入参: 用户ID")
    private ExtSouExpertLatestApplyQueryDTO param;

    @Nullable
    @ApiModelProperty("专家申请信息(IExtSouExpertGetApplyInfoPlugin#judgeGetApplyInfoAuth环节填补)")
    private ExtSouExpertApply expertApply;

    @Nullable
    @ApiModelProperty("最新未审批通过的申请ID")
    private Long noApprovalPassExpertApplyId;

    @Nullable
    @ApiModelProperty("查询结果")
    private ExtSouExpertApplyVO result;

    public ExtSouExpertGetLatestApplyInfoContext(ExtSouExpertLatestApplyQueryDTO param) {
        this.param = param;
    }

}
