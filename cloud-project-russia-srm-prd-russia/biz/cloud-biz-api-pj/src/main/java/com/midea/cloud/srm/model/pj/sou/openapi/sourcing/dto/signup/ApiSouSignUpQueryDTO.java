package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 寻源openAPI - 报名查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Data
@ApiModel(description = "报名查询条件")
@EqualsAndHashCode(callSuper = true)
public class ApiSouSignUpQueryDTO extends BasePage {

    /** @see SouVendor#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouVendor#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see SouVendor#getSignUpStatus */
    @ApiModelProperty("报名状态")
    private SouSignUpStatusEnum signUpStatus;

    @ApiModelProperty("供应商加入的轮次")
    private Integer joinRound;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        vendorCode = StringUtils.trimToNull(vendorCode);
        vendorName = StringUtils.trimToNull(vendorName);
    }

}
