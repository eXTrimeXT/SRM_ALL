package com.midea.cloud.srm.model.sou.purfixprice.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.enums.ExtPurFixPriceStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurFixPriceQueryDTO extends BasePage {

    /** @see ExtPurFixPriceHead#getDesignProjectCode */
    @ApiModelProperty("项目编号")
    private String designProjectCode;

    /** @see ExtPurFixPriceHead#getDesignProjectName */
    @ApiModelProperty("项目名称")
    private String designProjectName;

    /** @see ExtPurFixPriceHead#getFixPriceStatus */
    @ApiModelProperty("审核状态")
    private ExtPurFixPriceStatusEnum fixPriceStatus;

    /** @see ExtPurFixPriceHead#getCreatedBy */
    @ApiModelProperty("创建人")
    private String createdBy;

    /** @see ExtPurFixPriceHead#getCreationDate */
    @ApiModelProperty("创建时间范围")
    private Date creationDateFrom;
    private Date creationDateTo;

    /** 入参格式化 */
    public void formatParams() {
        designProjectCode = StringUtils.trimToNull(designProjectCode);
        designProjectName = StringUtils.trimToNull(designProjectName);
        createdBy = StringUtils.trimToNull(createdBy);
        if (creationDateFrom != null) {
            creationDateFrom = getStartTimeOfDate(creationDateFrom);
        }
        if (creationDateTo != null) {
            creationDateTo = getEndTimeOfDay(creationDateTo);
        }
    }

    /** 获取一天的开始时刻 */
    public static Date getStartTimeOfDate(Date time) {
        return Date.from(time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    /** 获取一天的结束时刻 */
    public static Date getEndTimeOfDay(Date day) {
        return Date.from(day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atTime(23, 59, 59, 999999999)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

}
