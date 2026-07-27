package com.midea.cloud.srm.sou.expert.spi.event.editapply;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertApplyDTO;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 寻源 - 专家申请编辑上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertEditApplyContext extends SdkPluginContext {

    @ApiModelProperty("入参: 编辑信息")
    private ExtSouExpertApplyDTO param;

    @Nullable
    @ApiModelProperty("现有的专家申请(IExtSouExpertEditApplyPlugin#judgeEditApplyAuth环节填补)")
    private ExtSouExpertApply existExpertApply;
    @ApiModelProperty("现有的专家申请适用品类(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private Map<Long, ExtSouExpertCategoryRelation> existExpertCategoryRelationMap = Collections.emptyMap();
    @ApiModelProperty("现有的专家申请学历(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private Map<Long, ExtSouExpertEducation> existExpertEducationMap = Collections.emptyMap();
    @ApiModelProperty("现有的专家申请适用组织(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private Map<Long, ExtSouExpertOrgRelation> existExpertOrgRelationMap = Collections.emptyMap();
    @ApiModelProperty("现有的专家申请工作经历(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private Map<Long, ExtSouExpertWork> existExpertWorkMap = Collections.emptyMap();
    @ApiModelProperty("现有的专家申请亲属工作经历(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private Map<Long, ExtSouExpertWorkRelation> existExpertWorkRelationMap = Collections.emptyMap();
    @ApiModelProperty("用户已申请通过的专家申请信息(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private List<ExtSouExpertApply> hasPassedExpertApplyList = Collections.emptyList();
    @ApiModelProperty("品类信息(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private Map<String, PurchaseCategory> categoryMap = Collections.emptyMap();
    @ApiModelProperty("组织信息(IExtSouExpertEditApplyPlugin#prepareEditApply环节填补)")
    private Map<String, Organization> orgMap = Collections.emptyMap();

    @ApiModelProperty("专家申请实体(IExtSouExpertEditApplyValidatePlugin环节填补)")
    private ExtSouExpertApply expertApplyEntity;
    @ApiModelProperty("专家申请附件实体(IExtSouExpertEditApplyValidatePlugin环节填补)")
    private List<SceneFile> expertApplyAttachFileEntityList = Collections.emptyList();
    @ApiModelProperty("专家申请适用品类实体(IExtSouExpertEditApplyValidatePlugin环节填补)")
    private List<ExtSouExpertCategoryRelation> expertCategoryRelationEntityList = Collections.emptyList();
    @ApiModelProperty("专家申请学历实体(IExtSouExpertEditApplyValidatePlugin环节填补)")
    private List<ExtSouExpertEducation> expertEducationEntityList = Collections.emptyList();
    @ApiModelProperty("专家申请适用组织实体(IExtSouExpertEditApplyValidatePlugin环节填补)")
    private List<ExtSouExpertOrgRelation> expertOrgRelationEntityList = Collections.emptyList();
    @ApiModelProperty("专家申请工作经历实体(IExtSouExpertEditApplyValidatePlugin环节填补)")
    private List<ExtSouExpertWork> expertWorkEntityList = Collections.emptyList();
    @ApiModelProperty("专家申请亲属工作经历实体(IExtSouExpertEditApplyValidatePlugin环节填补)")
    private List<ExtSouExpertWorkRelation> expertWorkRelationEntityList = Collections.emptyList();

    public ExtSouExpertEditApplyContext(ExtSouExpertApplyDTO param) {
        this.param = param;
    }

}
