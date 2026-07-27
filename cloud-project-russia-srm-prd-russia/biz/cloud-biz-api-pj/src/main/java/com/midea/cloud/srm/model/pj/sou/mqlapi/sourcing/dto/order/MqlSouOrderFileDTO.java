package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.order;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQL - 供应商报价附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderFileDTO extends SouOrderFile {

    /** @see SouFileConfig#getRequireDocId */
    @ApiModelProperty("文件ID")
    private Long requireDocId;

    /** @see SouFileConfig#getRequireFileName */
    @ApiModelProperty("文件名")
    private String requireFileName;

    /** @see SouFileConfig#getFileRequire */
    @ApiModelProperty("资料要求")
    private String fileRequire;

    /** @see SouFileConfig#getRequireRemark */
    @ApiModelProperty("备注")
    private String requireRemark;

}
