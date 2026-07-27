package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * MQL - 报名确认/驳回
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@ApiModel(description = "报名确认/驳回")
@EqualsAndHashCode(callSuper = true)
public class MqlSouSignUpConfirmDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
/**    vendorId  rejectReason */
    @ApiModelProperty("供应商ID集合")
    protected Map<Long, String> vendorMap;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-通过/false-驳回")
    protected Boolean toPass = true;
/**    vendorId */
    @ApiModelProperty("报价权限信息")
    protected Map<Long, List<SouVendorAuth>> authMap;
    @ApiModelProperty("寻源类型")
    private String souType;

    /**
     * 入参格式化
     */
    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorMap == null || vendorMap.isEmpty()) {
            throw new IllegalArgumentException("缺少vendorMap参数");
        }
        if (authMap != null && !authMap.isEmpty()) {
            authMap.forEach((vendorId, authList) -> authList.forEach(auth -> {
                auth.setVendorId(vendorId);
                if (auth.getSouItemId() == null) {
                    throw new IllegalArgumentException("authMap.souItemId不能为空");
                }
            }));
        }
    }

}
