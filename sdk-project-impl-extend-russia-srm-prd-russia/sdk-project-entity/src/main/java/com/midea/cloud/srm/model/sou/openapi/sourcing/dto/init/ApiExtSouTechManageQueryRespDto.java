package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
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
@ApiModel("技术标管理查询接口整合")
public class ApiExtSouTechManageQueryRespDto extends BaseObjectX {

    @ApiModelProperty("评标总人数")
    private Integer extBidEvaluatorNum;

    @ApiModelProperty("要求高级专家人数")
    private Integer extAskSeniorExpertNum;

    @ApiModelProperty("专家抽取范围，字典：SOU_EXPERT_RANGE")
    private String extExpertRange;

    @ApiModelProperty("高级专家人数")
    private Integer expertNum;

    @ApiModelProperty("普通专家人数")
    private Integer commonNum;

    @ApiModelProperty("抽取风险数量")
    private Integer extractRiskNum;

    @ApiModelProperty("评标小组")
    private List<ExtSouGroup> evaGroupList;

    @ApiModelProperty("评标进度跟踪")
    private List<ExtSouTechScoreHeadDto> evaTechScoreList;

    @ApiModelProperty("投标供应商")
    private List<ExtSouVendorDto> tenderVendorList;

    @ApiModelProperty("专家自身及亲友履历")
    private List<ExtSouExpertRiskDto> expertRiskList;


    @ApiModelProperty("是否已确认")
    private String extConfirmFlag;

    @ApiModelProperty("抽取风险数量")
    private Integer riskNum;

    @ApiModelProperty("开标人员记录表")
    private List<ExtNpmSouOpenBidRecordDto> openUserList;
}
