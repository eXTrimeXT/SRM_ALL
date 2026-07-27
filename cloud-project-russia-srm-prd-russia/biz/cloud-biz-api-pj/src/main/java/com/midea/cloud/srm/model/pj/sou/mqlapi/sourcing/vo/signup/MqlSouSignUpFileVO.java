package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.signup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangbf3
 * MQL - 供应商报名附件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouSignUpFileVO extends SouSignUpFile {

    @ApiModelProperty("立项外部附件")
    private SouFile souFile;

}
