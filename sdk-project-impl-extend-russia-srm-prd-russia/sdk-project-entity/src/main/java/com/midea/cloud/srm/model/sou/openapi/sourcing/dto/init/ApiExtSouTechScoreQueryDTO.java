package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "招标评分查询条件")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSouTechScoreQueryDTO extends BasePage {

    /** @see ExtSouProject#getSouNo */
    @ApiModelProperty("寻源单号(模糊查询)")
    private String souNo;

    /** @see ExtSouProject#getSouName */
    @ApiModelProperty("寻源单名称(模糊查询)")
    private String souName;

    /** @see ExtSouProject#getProjectStatus */
    @ApiModelProperty("项目状态")
    private String projectStatus;

    @ApiModelProperty("评标人")
    private String fullName;

    /** @see ExtSouProject#getCreationDate */
    @ApiModelProperty("创建时间从")
    private Date creationDateFrom;

    /** @see ExtSouProject#getCreationDate */
    @ApiModelProperty("创建时间到")
    private Date creationDateTo;

    /**
     * 招标项目编号
     */
    @ApiModelProperty("招标项目编号")
    private String extProjectNo;

    /**
     * 入参格式化
     */
    public void formatParams() {
        // 寻源单号
        souNo = StringUtils.trimToNull(souNo);
        // 寻源单名称
        souName = StringUtils.trimToNull(souName);
        // 创建人账号
        fullName = StringUtils.trimToNull(fullName);
        /**
         * 招标项目编号
         */
        extProjectNo = StringUtils.trimToNull(extProjectNo);

        // 创建时间从
        if (creationDateFrom != null) {
            creationDateFrom = getStartTimeOfDate(creationDateFrom);
        }
        // 创建时间到
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
