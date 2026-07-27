package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.signup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQL - 供应商报名附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouSignUpFileDTO extends BaseObjectX {

    @ApiModelProperty("ID")
    private Long signUpFileId;

    /** @see SouSignUpFile#getSouFileId */
    @ApiModelProperty("关联报名附件要求ID")
    private Long souFileId;

    /** @see SouSignUpFile#getSignUpDocId */
    @ApiModelProperty("文件ID")
    private Long signUpDocId;

    /** @see SouSignUpFile#getSignUpFileName */
    @ApiModelProperty("文件名称")
    private String signUpFileName;

    /** @see SouSignUpFile#getSignUpRemark */
    @ApiModelProperty("备注")
    private String signUpRemark;

}
