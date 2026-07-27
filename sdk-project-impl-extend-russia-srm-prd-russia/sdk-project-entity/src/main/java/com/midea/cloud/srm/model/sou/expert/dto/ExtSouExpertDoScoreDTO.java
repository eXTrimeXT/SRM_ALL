package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 寻源 - 专家库 - 专家评分
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertDoScoreDTO extends BaseObjectX {

    /** @see ExtSouExpertScoreLine#getExpertScoreLineId */
    @ApiModelProperty("评分明细ID")
    private Long expertScoreLineId;

    /** @see ExtSouExpertScoreLine#getScore */
    @ApiModelProperty("评分")
    private BigDecimal score;

    /** 不为空时用于和 {@link ExtSouExpertScoreLine#getUserId} 进行对比校验权限 */
    @Nullable
    @ApiModelProperty("当前用户ID")
    private Long currentUserId;

    /**
     * 入参格式化
     */
    public static void formatParams(@Nullable List<ExtSouExpertDoScoreDTO> params) {
        if (CollectionUtils.isEmpty(params)) {
            throw new IllegalArgumentException("缺少数据");
        }
        for (ExtSouExpertDoScoreDTO param : params) {
            if (param.getExpertScoreLineId() == null) {
                throw new IllegalArgumentException("缺少expertScoreLineId参数");
            }
            if (param.getScore() == null) {
                throw new IllegalArgumentException("缺少score参数");
            }
        }
    }

}
