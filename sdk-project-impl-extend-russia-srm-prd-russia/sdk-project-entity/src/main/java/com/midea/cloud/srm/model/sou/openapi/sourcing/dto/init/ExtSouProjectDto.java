package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.openapi.bid.vo.process.ApiBidSouProcessConfigVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiExtSouProcessConfigVo;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectExtendDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("寻源项目基本信息")
public class ExtSouProjectDto extends ExtSouProject {

    /**
     * 合并申请单号
     */
    private String applicantNo;

    /**
     * 合并申请单ID
     */
    private String applicantId;

    @ApiModelProperty("合并招标标识")
    private Boolean mergeFlag;

    @ApiModelProperty("审批人ID")
    private Long approveUserId;

    @ApiModelProperty("审批人账号")
    private String approveUserName;

    @ApiModelProperty("审批人名字")
    private String approveFullName;

    @ApiModelProperty("驳回说明")
    private String approveRejectDesc;

    @ApiModelProperty("流程配置信息")
    private ApiExtSouProcessConfigVo processConfig;
    @ApiModelProperty("流程节点信息")
    private List<ApiSouProcessNodeVO> processNodeList;

    @ApiModelProperty(value = "定/废标申请单ID", example = "1", required = true)
    private Long caId;

    @ApiModelProperty(value = "定/废标申请单单号", example = "CA20220101001", required = true)
    private String caNo;

    @ApiModelProperty(value = "中落标通知ID", example = "1")
    private Long bidNoticeId;

    @ApiModelProperty(value = "中/落标通知单号", example = "202109210001")
    private String bidNoticeNo;

    @ApiModelProperty("是否已确认")
    private String extConfirmFlag;

    @ApiModelProperty("推荐单ID")
    private String extRecommendId;

    @ApiModelProperty("合并申请单号列表")
    List<ExtSouDemand> demandList;

    /**
     * 是否部分取消
     */
    private String partCancle;
}
