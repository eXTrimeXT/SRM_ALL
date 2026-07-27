package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementCancelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 招标计划 - 计划取消编辑上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementCancelEditContext extends SdkPluginContext {

    @ApiModelProperty("入参: 计划取消信息")
    private ExtPrSouRequirementCancelDTO param;

    @ApiModelProperty("已存在的计划取消单(IPrSouRequirementCancelEditPlugin#judgeEditReqCancelAuth环节填补)")
    private ExtPrSouRequirementCancel existReqCancel;
    /** cancelLineId */
    @ApiModelProperty("已存在的计划取消明细(IPrSouRequirementCancelEditPlugin#judgeEditReqCancelAuth环节填补)")
    private Map<Long, ExtPrSouRequirementCancelLine> existReqCancelLineMap = Collections.emptyMap();
    /** cancelAttachId */
    @ApiModelProperty("已存在的计划取消附件(IPrSouRequirementCancelEditPlugin#judgeEditReqCancelAuth环节填补)")
    private Map<Long, ExtPrSouRequirementCancelAttach> existReqCancelAttachMap = Collections.emptyMap();

    @ApiModelProperty("计划取消实体(IPrSouRequirementCancelEditValidatePlugin环节填补)")
    private ExtPrSouRequirementCancel reqCancelEntity;
    @ApiModelProperty("计划取消明细实体(IPrSouRequirementCancelEditValidatePlugin环节填补)")
    private List<ExtPrSouRequirementCancelLine> reqCancelLineEntityList;
    @ApiModelProperty("计划取消附件实体(IPrSouRequirementCancelEditValidatePlugin环节填补)")
    private List<ExtPrSouRequirementCancelAttach> reqCancelAttachEntityList;

    public PrSouRequirementCancelEditContext(ExtPrSouRequirementCancelDTO param) {
        this.param = param;
    }
    @Override
    @Nullable
    public String getSceneType() {
        return ISdkPlugin.DEFAULT_SCENE;
    }

}
