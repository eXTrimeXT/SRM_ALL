package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 招标资料递交头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_bid_data_submit")
public class BidDataSubmit extends BaseEntity {
    /**
     * 主键
     */
    @TableId
    private Long dataSubmitId;

    /**
     * 招标资料递交单号
     */
    private String dataSubmitNo;

    /**
     * 板块ID
     */
    private Long orgBuId;

    /**
     * 板块编码
     */
    private String orgBuCode;

    /**
     * 板块名称
     */
    private String orgBuName;

    /**
     * 公司ID(对应产品的业务实体id)
     */
    private Long orgId;

    /**
     * 公司编码(对应产品的业务实体编码)
     */
    private String orgCode;

    /**
     * 公司名称(对应产品的业务实体名称)
     */
    private String orgName;

    /**
     * 部门ID
     */
    private String ceeaDepartmentId;

    /**
     * 部门编码
     */
    private String ceeaDepartmentCode;

    /**
     * 部门名称
     */
    private String ceeaDepartmentName;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 需求人ID
     */
    private Long reqUserId;

    /**
     * 需求人名称
     */
    private String reqUserName;

    /**
     * 招标负责人ID
     */
    private Long souPersonId;

    /**
     * 招标负责人名称
     */
    private String souPersonName;

    /**
     * 申请单号
     */
    private String requirementHeadNum;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 需求来源
     */
    private String sourceFrom;

    /**
     * 预算总金额
     */
    private BigDecimal totalBudget;

    /**
     * 品类ID
     */
    private Long categoryId;

    /**
     * 品类编码
     */
    private String categoryCode;

    /**
     * 品类
     */
    private String categoryName;

    /**
     * 规模数量
     */
    private String requireQuantity;

    /**
     * 意向金缴纳金额(元)
     */
    private BigDecimal depositAmount;

    /**
     * 投资编号
     */
    private String investNo;

    /**
     * 招标流程
     */
    private String bidFlow;

    /**
     * 评标组长用户ID
     */
    private Long bidEvalLeaderId;

    /**
     * 评标组长名称
     */
    private String bidEvalLeaderName;

    /**
     * 评标副组长用户ID
     */
    private Long bidEvalDeputyLeaderId;

    /**
     * 评标副组长名称
     */
    private String bidEvalDeputyLeaderName;

    /**
     * 技术负责人
     */
    private String techPrincipal;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 工作年限
     */
    private BigDecimal workYears;

    /**
     * 竞争性谈判签批附件ID
     */
    private Long competeFileId;

    /**
     * 竞争性谈判签批附件名称
     */
    private String competeFileName;

    /**
     * 合同签订单位
     */
    private String contractSignUnit;

    /**
     * 合同签订单位ID集合,多个用逗号分割
     */
    private String contractSignUnitId;

    /**
     * 合同签订单位编码集合,多个用逗号分割
     */
    private String contractSignUnitCode;

    /**
     * 合同签订单位统一社会信用代码集合,多个用逗号分割
     */
    private String contractSignUnitCredit;

    /**
     * 未提报月度计划原因
     */
    private String notMonthlyPlanReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否指定评标人(Y是，N否)
     */
    private String isAppointEvaluator;

    /**
     * 评标总人数
     */
    private Integer bidEvaluatorNum;

    /**
     * 要求高级专家人数
     */
    private Integer askSeniorExpertNum;

    /**
     * 指定评标人原因
     */
    private String appointEvaluatorReason;

    /**
     * 发布日期
     */
    private Date publishTime;
    @TableField(exist = false)
    private List<SceneFile> fileUploads;

    /**
     * 审批通过时间
     */
    @ApiModelProperty("审批通过时间")
    private Date approvePassTime;

    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;

    @ApiModelProperty("废弃原因")
    private String reasonDesc;
}
