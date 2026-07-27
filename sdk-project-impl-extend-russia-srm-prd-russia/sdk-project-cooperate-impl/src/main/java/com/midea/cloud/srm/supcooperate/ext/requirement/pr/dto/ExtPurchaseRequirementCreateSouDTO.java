package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 采购申请 - 非招创建寻源
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/11/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurchaseRequirementCreateSouDTO extends BaseObjectX {

    /** @see SouProject#getSouType */
    @ApiModelProperty("寻源类型")
    private String souType;

    /** @see RequirementHead#getRequirementHeadId */
    @ApiModelProperty("采购申请明细ID集合")
    private Set<Long> requirementLineIds;

    /**
     * 入参格式化
     */
    public void formatParam() {
        if (souType == null) {
            throw new IllegalArgumentException("缺少souType参数");
        }
        if (CollectionUtils.isEmpty(requirementLineIds)) {
            requirementLineIds = requirementLineIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(requirementLineIds)) {
            throw new IllegalArgumentException("缺少requirementLineIds参数");
        }
    }

}
