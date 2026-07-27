package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileEditDTO;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 寻源openAPI - 供应商报名信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@SuppressWarnings("ALL")
@Data
@ApiModel(description = "供应商报名信息")
@EqualsAndHashCode(callSuper = true)
public class ApiSouSignUpVendorDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("供应商ID")
    protected Long vendorId;
    @ApiModelProperty("报名附件")
    protected List<ApiSouSignUpFileDTO> signUpFileList;
    @ApiModelProperty("保证金缴纳时间")
    private Date depositPayTime;
    @ApiModelProperty("保证金附件")
    private List<ApiSouFileEditDTO> bondFileList;
    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (vendorId == null) {
            throw new IllegalArgumentException("缺少vendorId参数");
        }
    }

}
