package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.tech;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreHead;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTechScoreStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouTechProgressReviewVendorDetailVO extends BaseObjectX {

    /** @see SouVendor#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see SouVendor#getLinkmanName */
    @ApiModelProperty("联系人")
    private String linkManName;

    /** @see SouVendor#getPhone */
    @ApiModelProperty("电话")
    private String phone;

    /** @see SouVendor#getEmail */
    @ApiModelProperty("邮箱")
    private String email;

    /** @see SouTechScoreHead#getScoreStatus */
    @ApiModelProperty("评分状态")
    private SouTechScoreStatusEnum scoreStatus;

}
