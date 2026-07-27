package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.editrequire;

import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.RequirementInitEditContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 招标计划 - 立项编辑上下文
 * PS: 参数 param 指代 {@link ExtPrSouRequirementHeadDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrSouRequirementInitEditContext extends RequirementInitEditContext {

    @ApiModelProperty("现有的招标计划(IRequirementInitEditPlugin#prepareEditRequirement环节填补)")
    private ExtPrSouRequirementHead existPrSouHead;
    /** requirementGroupId */
    @ApiModelProperty("现有的招标计划工作小组(IRequirementInitEditPlugin#prepareEditRequirement环节填补)")
    private Map<Long, ExtPrSouRequirementGroup> existPrSouGroupMap = Collections.emptyMap();
    /** requirementVendorId */
    @ApiModelProperty("现有的招标计划推荐供应商(IRequirementInitEditPlugin#prepareEditRequirement环节填补)")
    private Map<Long, ExtPrSouRequirementVendor> existPrSouVendorMap = Collections.emptyMap();
    /** requirementAttachId */
    @ApiModelProperty("现有的招标计划附件(IRequirementInitEditPlugin#prepareEditRequirement环节填补)")
    private Map<Long, ExtPrSouRequirementAttach> existPrSouAttachMap = Collections.emptyMap();
    @Nullable
    @ApiModelProperty("所选择的项目计划(IRequirementInitEditPlugin#prepareEditRequirement环节填补)")
    private ExtPrSouProjectPlan projectPlan;
    /** username */
    @ApiModelProperty("可用成员信息集合(IRequirementInitEditPlugin#prepareEditRequirement环节填补)")
    private Map<String, User> userMap = Collections.emptyMap();

    @ApiModelProperty("招标计划实体")
    private ExtPrSouRequirementHead souReqHeadEntity;
    @ApiModelProperty("招标计划工作小组实体")
    private List<ExtPrSouRequirementGroup> souGroupEntityList;
    @ApiModelProperty("招标计划推荐供应商实体")
    private List<ExtPrSouRequirementVendor> souVendorEntityList;
    @ApiModelProperty("招标计划附件实体")
    private List<ExtPrSouRequirementAttach> souAttachEntityList;

}
