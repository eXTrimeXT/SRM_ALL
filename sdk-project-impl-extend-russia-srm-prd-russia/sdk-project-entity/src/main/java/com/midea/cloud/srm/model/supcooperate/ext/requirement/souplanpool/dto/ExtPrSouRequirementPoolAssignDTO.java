package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * MQL - 招标计划池分配/转办
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementPoolAssignDTO extends BaseObjectX {

    /**
     * @see ExtPrSouRequirementGroup#getGroupType
     * @see ExtPrSouRequirementGroup#getUserId
     */
    private Map<String, Long> groupUsers;

    /** @see ExtPrSouRequirementHead#getRequirementHeadId */
    @ApiModelProperty("计划池行ID集合")
    private Set<Long> requirementHeadIds;

    @ApiModelProperty("应用场景")
    private String sceneType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (groupUsers == null || groupUsers.isEmpty()) {
            throw new IllegalArgumentException("请至少选择一个负责人用于分配");
        } else {
            Set<String> emptyGroupTypes = new HashSet<>();
            groupUsers.forEach((groupType, userId) -> {
                if (userId == null) { emptyGroupTypes.add(groupType); }
            });
            for (String groupType : emptyGroupTypes) {
                groupUsers.remove(groupType);
            }
            if (groupUsers.isEmpty()) {
                throw new IllegalArgumentException("请至少选择一个负责人用于分配");
            }
        }
        if (requirementHeadIds != null) {
            requirementHeadIds.removeIf(Objects::isNull);
        }
        if (CollectionUtils.isEmpty(requirementHeadIds)) {
            throw new IllegalArgumentException("缺少requirementHeadIds数据");
        }
    }

}
