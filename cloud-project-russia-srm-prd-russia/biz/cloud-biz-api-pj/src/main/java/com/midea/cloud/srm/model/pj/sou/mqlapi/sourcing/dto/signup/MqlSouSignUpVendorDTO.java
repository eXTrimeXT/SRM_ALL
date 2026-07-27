package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * MQL - 供应商报名信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@ApiModel(description = "供应商报名信息")
@EqualsAndHashCode(callSuper = true)
public class MqlSouSignUpVendorDTO extends SouVendor {

    @ApiModelProperty("报名附件")
    protected List<SouSignUpFile> signUpFileList;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave = true;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (getProjectId() == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (getVendorId() == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
    }

}
