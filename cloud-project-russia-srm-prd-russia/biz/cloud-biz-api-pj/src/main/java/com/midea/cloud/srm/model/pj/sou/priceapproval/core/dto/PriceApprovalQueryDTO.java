package com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApproval;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 价格审批单 - 列表查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalQueryDTO extends BasePage {

    /** @see PriceApproval#getApprovalNo */
    @ApiModelProperty("价格审批单号(模糊查询)")
    private String approvalNo;

    /** @see PriceApproval#getApprovalName */
    @ApiModelProperty("价格审批单标题(模糊查询)")
    private String approvalName;

    /** @see PriceApproval#getApprovalStatus */
    @ApiModelProperty("价格审批单状态(等值查询)")
    private String approvalStatus;

    /** @see PriceApproval#getSourceFromNo */
    @ApiModelProperty("寻源单号(来源单号)(模糊查询)")
    private String sourceFromNo;

    /** @see PriceApproval#getCreationDate */
    @ApiModelProperty("创建时间(范围查询)")
    private Date creationDateFrom;
    private Date creationDateTo;

    /**
     * @see PriceApproval#getCreatedBy
     * @see PriceApproval#getCreatedFullName
     */
    @ApiModelProperty("创建人(昵称/账号)(模糊查询)")
    private String createdBy;

    /** @see PriceApprovalItem#getItemId */
    @ApiModelProperty("物料ID(等值查询)")
    private Long itemId;

    /** @see PriceApprovalItem#getVendorId */
    @ApiModelProperty("供应商ID(等值查询)")
    private Long vendorId;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    private String sceneType = "default";

    /**
     * 入参格式化
     */
    public void formatParams() {
        // 1: 价格审批单号
        approvalNo = StringUtils.trimToNull(approvalNo);
        // 2: 价格审批单标题
        approvalName = StringUtils.trimToNull(approvalName);
        // 3: 价格审批单状态
        approvalStatus = StringUtils.trimToNull(approvalStatus);
        // 4: 寻源单号
        sourceFromNo = StringUtils.trimToNull(sourceFromNo);
        // 5: 创建时间范围
        if (creationDateFrom != null) {
            creationDateFrom = ApiSouProjectQueryDTO.getStartTimeOfDate(creationDateFrom);
        }
        if (creationDateTo != null) {
            creationDateTo = ApiSouProjectQueryDTO.getEndTimeOfDay(creationDateTo);
        }
        // 6: 创建人
        createdBy = StringUtils.trimToNull(createdBy);
    }

}
