package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 报价附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderFileDTO extends BaseObjectX {

    /** @see SouOrderFile#getOrderFileId */
    @ApiModelProperty("供应商报价附件表ID")
    private Long orderFileId;

    /** @see SouOrderFile#getSouFileConfigId */
    @ApiModelProperty("供方必须上传附件ID")
    private Long souFileConfigId;

    /** @see SouOrderFile#getOrderDocId */
    @ApiModelProperty("供应商报价附件ID")
    private Long orderDocId;

    /** @see SouOrderFile#getOrderFileName */
    @ApiModelProperty("供应商报价附件名称")
    private String orderFileName;

    /** @see SouOrderFile#getOrderRemark */
    @ApiModelProperty("供应商报价附件备注")
    private String orderRemark;

    /** @see SouFileConfig#getFileType */
    @ApiModelProperty("附件类型")
    private SouFileConfigTypeEnum fileType;

}
