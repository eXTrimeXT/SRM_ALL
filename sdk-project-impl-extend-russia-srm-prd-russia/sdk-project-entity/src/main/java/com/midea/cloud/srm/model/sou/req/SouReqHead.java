package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 寻源需求单头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@ApiModel(description = "寻源需求单头表")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("scc_npm_sou_req_head")
public class SouReqHead extends BaseEntity {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long reqHeadId;

    /**
     * 寻源单单号
     */
    @ApiModelProperty("寻源单单号")
    private String reqHeadNo;

    /**
     * 板块ID
     */
    @ApiModelProperty("板块ID")
    private Long orgBuId;

    /**
     * 板块编码
     */
    @ApiModelProperty("板块编码")
    private String orgBuCode;

    /**
     * 板块名称
     */
    @ApiModelProperty("板块名称")
    private String orgBuName;

    /**
     * 公司ID(对应产品的业务实体id)
     */
    @ApiModelProperty("公司ID(对应产品的业务实体id)")
    private Long orgId;

    /**
     * 公司编码(对应产品的业务实体编码)
     */
    @ApiModelProperty("公司编码(对应产品的业务实体编码)")
    private String orgCode;

    /**
     * 公司名称(对应产品的业务实体名称)
     */
    @ApiModelProperty("公司名称(对应产品的业务实体名称)")
    private String orgName;
    @ApiModelProperty("公示寻源模板主键")
    private Long pubconfigId;
    @ApiModelProperty("公示寻源模板名称")
    private String pubconfigName;
    /**
     * 需求部门
     */
    @ApiModelProperty("需求部门")
    private String reqDepartment;

    /**
     * 需求人ID
     */
    @ApiModelProperty("需求人ID")
    private Long reqUserId;

    /**
     * 需求人名称
     */
    @ApiModelProperty("需求人名称")
    private String reqUserName;

    /**
     * 供应商负责人ID
     */
    @ApiModelProperty("供应商负责人ID")
    private Long responsibilityUserId;

    /**
     * 供应商负责人名称
     */
    @ApiModelProperty("供应商负责人名称")
    private String responsibilityUserName;

    /**
     * 招标负责人ID
     */
    @ApiModelProperty("招标负责人ID")
    private Long souPersonUserId;

    /**
     * 招标负责人名称
     */
    @ApiModelProperty("招标负责人名称")
    private String souPersonUserName;

    @ApiModelProperty("技术负责人ID")
    private Long technicalUserId;
    @ApiModelProperty("技术负责人名称")
    private String technicalUserName;

    /**
     * 是否前置交流（Y是，N否）
     */
    @ApiModelProperty("是否前置交流（Y是，N否）")
    private String isPreComm;

    /**
     * 是否公示寻源（Y是，N否）
     */
    @ApiModelProperty("是否公示寻源（Y是，N否）")
    private String isPublic;
    /**
     * 关闭公示原因
     */
    @ApiModelProperty("关闭公示原因")
    private String closePublicReason;
    /**
     * 是否已经供应商推荐(Y是，N否)
     */
    @ApiModelProperty("是否已经供应商推荐(Y是，N否)")
    private String isRecommend;

    /**
     * 单据状态
     */
    @ApiModelProperty("单据状态")
    private String status;

    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    private String projectName;

    /**
     * 公示截止时间
     */
    @ApiModelProperty("公示截止时间")
    private Date publicEndTime;

    /**
     * 发布时间
     */
    @ApiModelProperty("发布时间")
    private Date releaseDate;

    /**
     * 所属品类ID
     */
    @ApiModelProperty("所属品类ID")
    private Long categoryId;

    /**
     * 所属品类编码
     */
    @ApiModelProperty("所属品类编码")
    private String categoryCode;

    /**
     * 所属品类名称
     */
    @ApiModelProperty("所属品类名称")
    private String categoryName;

    /**
     * 概算金额(万元)
     */
    @ApiModelProperty("概算金额(万元)")
    private BigDecimal totalAmountByTenKilo;

    /**
     * 规模数量
     */
    @ApiModelProperty("规模数量")
    private String requireQuantity;

    /**
     * 已邀请供应商数量
     */
    @ApiModelProperty("已邀请供应商数量")
    private Integer inviteQuantity;

    /**
     * 招标计划申请单ID
     */
    @ApiModelProperty("招标计划申请单ID")
    private Long requirementHeadId;
    /**
     * 合并申请单号
     */
    @ApiModelProperty("合并申请单ID")
    private String requirementHeadIdList;

    /**
     * 招标计划申请单号
     */
    @ApiModelProperty("招标计划申请单号")
    private String requirementHeadNo;

    /**
     * 合并申请单号
     */
    @ApiModelProperty("合并申请单号")
    private String requirementHeadNoList;
    /**
     * 是否部分取消
     */
    @ApiModelProperty("是否部分取消")
    private String partCancle;

    /**
     * 需求来源
     */
    @ApiModelProperty("需求来源")
    private String requireFrom;

    /**
     * 项目概况与招标范围
     */
    @ApiModelProperty("项目概况与招标范围")
    private String projectScope;

    /**
     * 供应商资质要求
     */
    @ApiModelProperty("供应商资质要求")
    private String vendorQualReq;

    /**
     * 技术要求
     */
    @ApiModelProperty("技术要求")
    private String technicalReq;

    /**
     * 业绩要求
     */
    @ApiModelProperty("业绩要求")
    private String performanceReq;

    /**
     * 项目所在地
     */
    @ApiModelProperty("项目所在地")
    private String projectAddress;

    /**
     * 联系人
     */
    @ApiModelProperty("联系人")
    private String contactName;

    /**
     * 电话
     */
    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("技术负责人电话")
    private String techPhone;
    /**
     * 办公电话
     */
    @ApiModelProperty("办公电话")
    private String officePhone;

    /**
     * 是否需缴纳意向金（Y是，N否）
     */
    @ApiModelProperty("是否需缴纳意向金（Y是，N否）")
    private String isNeedDeposit;

    /**
     * 意向金缴纳金额(元)
     */
    @ApiModelProperty("意向金缴纳金额(元)")
    private BigDecimal depositAmount;

    /**
     * 开户银行
     */
    @ApiModelProperty("开户银行")
    private String bankName;

    /**
     * 开户行号
     */
    @ApiModelProperty("开户行号")
    private String bankNumber;

    /**
     * 开户账号
     */
    @ApiModelProperty("开户账号")
    private String bankAccount;

    /**
     * 开户户名
     */
    @ApiModelProperty("开户户名")
    private String bankAccountName;

    @ApiModelProperty(value = "适用板块组织ID")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;

    @ApiModelProperty(value = "适用板块组织编码")
    @TableField("ORGANIZATION_CODE")
    private String organizationCode;

    @ApiModelProperty(value = "适用板块组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;
    /**
     * 规模数量
     */
    @ApiModelProperty("项目已阅数量")
    private Integer projectViewsCount;
    @TableField(exist = false)
    private List<SceneFile> fileUploads;

    @ApiModelProperty("废弃原因")
    private String reasonDesc;
}
