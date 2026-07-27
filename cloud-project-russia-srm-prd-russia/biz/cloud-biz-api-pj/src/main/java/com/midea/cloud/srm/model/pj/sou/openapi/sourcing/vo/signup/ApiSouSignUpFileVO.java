package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangbf3
 * 寻源openAPI - 供应商报名附件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSignUpFileVO extends SouSignUpFile {

    /** @see SouFile#getSouDocId */
    @ApiModelProperty("文件ID")
    private Long souDocId;

    /** @see SouFile#getSouFileName */
    @ApiModelProperty("文件名")
    private String souFileName;

    /** @see SouFile#getFileType */
    @ApiModelProperty("附件类型[字典:SOU_FILE_TYPE]")
    private SouFileTypeEnum fileType;

    /** @see SouFile#getSouRemark */
    @ApiModelProperty("说明")
    private String souRemark;

    /** @see SouFile#getSortIndex */
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
