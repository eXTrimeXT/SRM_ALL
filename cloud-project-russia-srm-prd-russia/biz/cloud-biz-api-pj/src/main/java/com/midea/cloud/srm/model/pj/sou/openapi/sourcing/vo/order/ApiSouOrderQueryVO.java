package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 寻源openAPI - 供应商报价列表查询数据
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouOrderQueryVO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouProject#getProcessConfigId */
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("寻源标题")
    private String souName;

    /** @see SouProject#getCurrentRound */
    @ApiModelProperty("当前轮次")
    private Integer currentRound;

    /** @see SouProject#getPublishScope */
    @ApiModelProperty("发布范围")
    private SouPublishScopeEnum publishScope;

    /** @see SouOrder#getOrderStatus */
    @ApiModelProperty("寻源单状态")
    private SouProjectStatusEnum projectStatus;

    /** @see SouOrder#getOrderId */
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("报价单号")
    private String orderNo;

    /** @see SouOrder#getOrderStatus */
    @ApiModelProperty("报价单状态")
    private SouOrderStatusEnum orderStatus;

    /** @see SouProject#getSignUpEndTime */
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;

    /** @see SouProject#getOrderStartTime */
    @ApiModelProperty("本轮报价开始时间")
    private Date orderStartTime;

    /** @see SouProject#getOrderEndTime */
    @ApiModelProperty("本轮报价截止时间")
    private Date orderEndTime;

    /** @see SouProject#getPublishTime */
    @ApiModelProperty("发布时间")
    private String publishTime;

    /** @see SouProject#getAllowWithdraw */
    @ApiModelProperty("是否允许供应商撤回报价")
    private Enable allowWithdraw;

    /** @see SouVendor#getJoinRound */
    @ApiModelProperty("供应商加入的轮次")
    private Integer joinRound;

    /** @see SouProject#getCreatedBy */
    @ApiModelProperty("创建人账号")
    private String createdBy;
    /** @see SouProject#getCreatedFullName */
    @ApiModelProperty("创建人昵称")
    private String createdFullName;

    @ApiModelProperty("供应商在该轮次是否有报价权限")
    private Enable canOrder;

    @ApiModelProperty("询价单是否有报名节点")
    private Enable hasSignUpNode;

    @ApiModelProperty("报名状态")
    private SouSignUpStatusEnum signUpStatus;

    @ApiModelProperty("招标编号")
    private String  extProjectNo;

}
