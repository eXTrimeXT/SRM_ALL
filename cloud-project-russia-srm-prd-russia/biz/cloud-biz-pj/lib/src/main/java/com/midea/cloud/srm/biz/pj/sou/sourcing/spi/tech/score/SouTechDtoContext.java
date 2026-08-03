package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.tech.score;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2022/09/26
 */
@Data
@Builder
public class SouTechDtoContext {

    @ApiModelProperty("寻源单")
    private SouProject souProject;

    @ApiModelProperty("评委")
    private SouGroup group;

    @ApiModelProperty("报价单")
    private SouOrder order;

    @ApiModelProperty("评分规则")
    private SouScoreRule scoreRule;

    @ApiModelProperty("技术维度评分规则明细")
    private List<SouScoreRuleLine> techScoreRuleLineList;

    private static final ThreadLocal<SouTechDtoContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setContextHolder(SouTechDtoContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static SouTechDtoContext getContextHolder() {
        return CONTEXT_HOLDER.get();
    }

    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
