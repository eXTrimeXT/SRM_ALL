package com.midea.cloud.srm.model.sou.fixprice.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceQueryDTO extends BasePage {

    /** @see ExtFixPriceHead#getFixPriceNo */
    @ApiModelProperty("定价单号")
    private String fixPriceNo;

    /** @see ExtFixPriceHead#getFixPriceStatus */
    @ApiModelProperty("定价单状态")
    private String fixPriceStatus;

    /** @see ExtFixPriceHead#getOrgDepName */
    @ApiModelProperty("采购部门")
    private String orgDepName;

    /** @see ExtFixPriceHead#getCreationDate */
    @ApiModelProperty("创建时间范围")
    private Date creationDateFrom;
    private Date creationDateTo;
    private Date creationDate;
    /** @see ExtFixPriceLine#getItemCode */
    @ApiModelProperty("物料编码")
    private String itemCode;

    /** @see ExtFixPriceLine#getItemDesc */
    @ApiModelProperty("物料名称")
    private String itemDesc;

    /** @see ExtFixPriceHead#getCreatedBy */
    @ApiModelProperty("创建人账号")
    private String createdBy;

    @ApiModelProperty("创建人账号")
    private List<Long> fixPriceIds;

    @ApiModelProperty("供应商名字")
    private String vendorName;

    @ApiModelProperty("规格型号")
    private String extMaterialModel;

    /**
     * 入参格式化
     */
    public void formatParams() {
        fixPriceNo = StringUtils.trimToNull(fixPriceNo);
        fixPriceStatus = StringUtils.trimToNull(fixPriceStatus);
        orgDepName = StringUtils.trimToNull(orgDepName);
        if (creationDateFrom != null) {
            creationDateFrom = getStartTimeOfDate(creationDateFrom);
        }
        if (creationDateTo != null) {
            creationDateTo = getEndTimeOfDay(creationDateTo);
        }
        itemCode = StringUtils.trimToNull(itemCode);
        itemDesc = StringUtils.trimToNull(itemDesc);
        createdBy = StringUtils.trimToNull(createdBy);
        vendorName = StringUtils.trimToNull(vendorName);
        extMaterialModel= StringUtils.trimToNull(extMaterialModel);
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
