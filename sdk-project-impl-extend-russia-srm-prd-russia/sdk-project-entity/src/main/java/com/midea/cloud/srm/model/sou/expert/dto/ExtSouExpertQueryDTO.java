package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * 寻源 - 专家库 - 列表查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertQueryDTO extends BasePage {

    /** @see ExtSouExpertApply#getExpertApplyNo */
    @ApiModelProperty("申请编号(模糊查询)")
    private String expertApplyNo;

    /** @see ExtSouExpertApply#getApplyTime */
    @ApiModelProperty("申请时间范围")
    private LocalDateTime applyTimeFrom;
    private LocalDateTime applyTimeTo;

    /**
     * @see ExtSouExpertApply#getApplyBy
     * @see ExtSouExpertApply#getApplyByNickname
     */
    @ApiModelProperty("申请人账号/昵称(模糊查询)")
    private String applyBy;

    /** @see ExtSouExpertApply#getApplyStatus */
    @ApiModelProperty("申请状态(等值查询)")
    private String applyStatus;

    /** @see ExtSouExpert#getHasQuite */
    @ApiModelProperty("是否退出(等值查询)")
    private String hasQuite;

    /**
     * 入参格式化
     */
    public void formatParams() {
        expertApplyNo = StringUtils.trimToNull(expertApplyNo);
        if (applyTimeFrom != null) {
            applyTimeFrom = applyTimeFrom.toLocalDate().atStartOfDay();
        }
        if (applyTimeTo != null) {
            applyTimeTo = applyTimeTo.withHour(23).withMinute(59).withSecond(59);
        }
        applyBy = StringUtils.trimToNull(applyBy);
        applyStatus = StringUtils.trimToNull(applyStatus);
        hasQuite = StringUtils.trimToNull(hasQuite);
    }

}
