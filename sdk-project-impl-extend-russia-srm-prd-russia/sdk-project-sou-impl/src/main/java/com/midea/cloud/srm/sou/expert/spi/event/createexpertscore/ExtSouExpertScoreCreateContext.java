package com.midea.cloud.srm.sou.expert.spi.event.createexpertscore;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreCreateDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 寻源 - 专家库 - 专家审批创建上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertScoreCreateContext extends SdkPluginContext {

    @ApiModelProperty("入参: 专家审批创建信息")
    private List<ExtSouExpertScoreCreateDTO> params;

    @ApiModelProperty("专家信息(IExtSouExpertScoreCreatePlugin#prepareCreateExpertScore环节填补)")
    /** userId */
    private Map<Long, ExtSouExpert> expertMap = Collections.emptyMap();
    @ApiModelProperty("用户信息(IExtSouExpertScoreCreatePlugin#prepareCreateExpertScore环节填补)")
    /** username */
    private Map<String, User> userMap = Collections.emptyMap();
    @ApiModelProperty("现有的指定寻源单相关的评审信息(IExtSouExpertScoreCreatePlugin#prepareCreateExpertScore环节填补)")
    /** souProjectId */
    private Map<Long, List<ExtSouExpertScore>> existScoreMap;

    @ApiModelProperty("专家评审实体集合(IExtSouExpertScoreCreateValidatePlugin环节填补)")
    private List<ExtSouExpertScore> expertScoreEntityList;
    @ApiModelProperty("专家评审实体集合(IExtSouExpertScoreCreateValidatePlugin环节填补)")
    private List<ExtSouExpertScoreLine> expertScoreLineEntityList;

    public ExtSouExpertScoreCreateContext(List<ExtSouExpertScoreCreateDTO> params) {
        this.params = params;
    }

}
