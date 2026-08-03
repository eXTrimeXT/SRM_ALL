package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.vendorsignup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源openAPI - 供应商报名实体存储
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SouVendorSignUpPO extends BaseObjectX {

    @ApiModelProperty("供应商信息")
    private SouVendor vendor;
    @ApiModelProperty("报名附件")
    private List<SouSignUpFile> signUpFileList;
    @ApiModelProperty("保证金缴纳附件")
    private List<SouFile> bondPayList;

}
