package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 寻源openAPI - 报名确认/驳回
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/03
 */
@Data
@ApiModel(description = "报名确认/驳回")
@EqualsAndHashCode(callSuper = true)
public class ApiSouSignUpConfirmDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("供应商ID")
    protected Long vendorId;
    @SuppressWarnings({"AlibabaPojoNoDefaultValue", "AlibabaPojoMustUsePrimitiveField"})
    @ApiModelProperty("true-通过/false-驳回")
    protected boolean toPass = true;
    @ApiModelProperty("驳回原因")
    protected String rejectReason;
    @ApiModelProperty("报价权限信息")
    protected List<ApiSouVendorAuthEditDTO> authList;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
        if (toPass) {
            rejectReason = null;
        } else {
            rejectReason = StringUtils.trimToNull(rejectReason);
            int length = 300;
            if (rejectReason != null && rejectReason.length() > length) {
                throw new IllegalArgumentException("驳回原因的输入长度不能超过300");
            }
        }
        /*if (CollectionUtils.isNotEmpty(authList)) {
            for (ApiSouVendorAuthEditDTO auth : authList) {
                auth.setVendorId(vendorId);
                if (auth.getSouItemId() == null) {
                    throw new IllegalArgumentException("authList.souItemId不能为空");
                }
            }
        }*/
    }

}
