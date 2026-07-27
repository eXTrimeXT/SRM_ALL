package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.answer.dto.ExtReplayFileDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreFile;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("招标评分明细")
public class ApiExtSouTechEvaDetailDto extends BaseObjectX {

    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("技术评分头ID")
    private Long techScoreHeadId;

    @ApiModelProperty("评委ID")
    private Long groupId;

    @ApiModelProperty("技术文件")
    private List<ExtSouOrderFileDto> techFileList;

    @ApiModelProperty("打分明细")
    List<ApiExtScoreRuleDto> scoreRuleList;

    @ApiModelProperty("合并招标标识")
    private Boolean mergeFlag;

    @ApiModelProperty("附件列表")
    private List<ExtSouTechScoreFile> fileList;
    @ApiModelProperty("附件列表")
    private List<ExtReplayFileDTO> replayFileList;

}
