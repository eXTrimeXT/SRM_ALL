package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementLine;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 寻源核心 MQL - 需求池转寻源参数
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouCreateFromSupCoopPoolDTO extends BaseObjectX {
/**    requirementHeadId */
    @ApiModelProperty("采购申请头信息")
    private Map<Long, PrRequirementHead> requirementHeadMap;
    @ApiModelProperty("需求池行数据")
    private List<PrRequirementLine> requirementLineList;

    @ApiModelProperty("单据序列号生成规则")
    private String sequenceCode;

    private String souType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (CollectionUtils.isEmpty(requirementLineList)) {
            throw new IllegalArgumentException("缺少需求池行数据");
        } else {
            requirementLineList.forEach(reqLine -> {
                PrRequirementHead reqHead = requirementHeadMap.get(reqLine.getRequirementHeadId());
                if (reqHead == null) {
                    throw new IllegalArgumentException("缺少requirementHeadMap数据");
                }
            });
        }
        sequenceCode = StringUtils.trimToNull(sequenceCode);
    }

}
