package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFileConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileConfigTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 寻源openAPI - 评选附件
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSelectFileDTO extends BaseObjectX {

    @ApiModelProperty("竞价ID")
    protected Long projectId;

    @ApiModelProperty("审批流")
    private String approvalProcess;

    @ApiModelProperty("评选附件表ID")
    private Long selectFileId;

    @ApiModelProperty("评选附件ID")
    private Long selectDocId;

    @ApiModelProperty("评选附件名称")
    private String selectFileName;

    @ApiModelProperty("评选附件备注")
    private String selectRemark;

    @ApiModelProperty("附件类型")
    private SouFileTypeEnum fileType;


}
