package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 供应商报名附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSignUpFileDTO extends BaseObjectX {

    @ApiModelProperty("ID")
    private Long signUpFileId;

    /** @see SouFile#getSouFileId */
    @ApiModelProperty("关联报名附件要求ID")
    private Long souFileId;

    @ApiModelProperty("文件ID")
    private Long signUpDocId;

    @ApiModelProperty("文件名称")
    private String signUpFileName;

    @ApiModelProperty("备注")
    private String signUpRemark;

}
