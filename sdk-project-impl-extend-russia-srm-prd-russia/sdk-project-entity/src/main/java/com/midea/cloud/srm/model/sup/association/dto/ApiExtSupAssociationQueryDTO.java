package com.midea.cloud.srm.model.sup.association.dto;

import com.midea.cloud.srm.model.common.BasePage;
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
@ApiModel(description = "关联供应商信息查询")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSupAssociationQueryDTO extends BasePage {

    @ApiModelProperty("关联供应商ID")
    private Long associationId;

    @ApiModelProperty("A供应商ID")
    private Long vendorIdA;

    @ApiModelProperty("A供应商编码")
    private String vendorCodeA;

    @ApiModelProperty("A供应商名称")
    private String vendorNameA;

    @ApiModelProperty("B供应商ID")
    private Long vendorIdB;

    @ApiModelProperty("B供应商CODE")
    private String vendorCodeB;

    @ApiModelProperty("B供应商NAME")
    private String vendorNameB;

    @ApiModelProperty("类型")
    private String associationType;

    @ApiModelProperty("创建人ID")
    private Long createdId;

    @ApiModelProperty("创建人名字")
    private String createdFullName;

    @ApiModelProperty("创建日期从")
    private Date creationDateFrom;

    @ApiModelProperty("创建日期到")
    private Date creationDateTo;

    /**
     * 入参格式化
     */
    public void formatParams() {

        // 供应商A编码
        vendorCodeA = StringUtils.trimToNull(vendorCodeA);
        // 供应商B编码
        vendorCodeB = StringUtils.trimToNull(vendorCodeB);
        // 类型
        associationType = StringUtils.trimToNull(associationType);

        // 创建时间从
        if (creationDateFrom != null) {
            creationDateFrom = getStartTimeOfDate(creationDateFrom);
        }
        // 创建时间到
        if (creationDateTo != null) {
            creationDateTo = getEndTimeOfDay(creationDateTo);
        }
    }

    /**
     * 获取一天的开始时刻
     */
    public static Date getStartTimeOfDate(Date time) {
        return Date.from(time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取一天的结束时刻
     */
    public static Date getEndTimeOfDay(Date day) {
        return Date.from(day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atTime(23, 59, 59, 999999999)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

}
