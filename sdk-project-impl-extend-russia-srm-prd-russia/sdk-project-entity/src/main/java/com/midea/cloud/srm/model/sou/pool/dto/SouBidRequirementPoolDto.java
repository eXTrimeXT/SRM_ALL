package com.midea.cloud.srm.model.sou.pool.dto;

import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
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
@ApiModel("招标计划池DTO")
public class SouBidRequirementPoolDto extends BaseObjectX {

    @ApiModelProperty("寻源需求单")
    private List<SouReqHead> souReqHeadList;

    @ApiModelProperty("招标单据")
    private List<ExtSouProject> projectList;

    @ApiModelProperty("供应商推荐")
    private List<RecommvendorProjectDto> recommvendorProjectList;

    @ApiModelProperty("招标单据或推荐单关联的申请单号")
    List<ExtSouDemand> souDemandList;

    /**
     * 申请单号合集
     */
    List<String> applicantNoList;
}
