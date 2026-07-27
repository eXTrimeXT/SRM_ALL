package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zenghx2
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_pr_requirement_head")
public class PurchaseRequirementHeadDTO extends RequirementHead {

    @ApiModelProperty("板块id")
    private String extOrgBuId;
    @ApiModelProperty("板块编码")
    private String extOrgBuCode;
    @ApiModelProperty("板块名称")
    private String extOrgBuName;

    @ApiModelProperty("是否领单，Y/N")
    private String extInPool;

    @ApiModelProperty("审批时间")
    private LocalDateTime extApproveTime;

    @ApiModelProperty("是否招标，Y/N")
    private String extBidFlag;

    private String applyByNickname;
    private Long applyById;
    /** 来源 */
    private String edmSource;
    /** 外部单号 */
    private String edmExNo;

    private Long buId;
    private String buCode;
    private String buName;
    private Long depId;
    private String depCode;
    private String depName;
    private Long comId;
    private String comCode;
    private String comName;

    private BigDecimal extExpectTotalAmount;
}
