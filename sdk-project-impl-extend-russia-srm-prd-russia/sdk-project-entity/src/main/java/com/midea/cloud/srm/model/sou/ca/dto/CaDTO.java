package com.midea.cloud.srm.model.sou.ca.dto;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/5 10:49:18
 *  修改内容:
 * </pre>
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.BaseDTO;
import com.midea.cloud.srm.model.sou.bidprices.dto.BidPriceDto;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "定标申请DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CaDTO extends BaseDTO {

    @ApiModelProperty(value = "定/废标申请单ID", example = "1", required = true)
    private Long caId;

    @ApiModelProperty(value = "定/废标申请单单号", example = "CA20220101001", required = true)
    private String caNo;

    @ApiModelProperty(value = "类型(枚举类:CaTypeEnum)", example = "1")
    private String type;

    @ApiModelProperty(value = "原定标申请ID", example = "1")
    private Long originalCaId;

    @ApiModelProperty(value = "原定标申请单号", example = "CA20210101001")
    private String originalCaNo;

    @ApiModelProperty(value = "板块ID", example = "1")
    private Long extOrgBuId;

    @ApiModelProperty(value = "板块编码", example = "BU001")
    private String extOrgBuCode;

    @ApiModelProperty(value = "板块名称", example = "板块1")
    private String extOrgBuName;

    @ApiModelProperty(value = "公司ID", example = "1", required = true)
    private Long extOrgOuId;

    @ApiModelProperty(value = "公司编码", example = "OU001", required = true)
    private String extOrgOuCode;

    @ApiModelProperty(value = "公司名称", example = "公司1", required = true)
    private String extOrgOuName;

    @ApiModelProperty(value = "项目ID", example = "1", required = true)
    private Long projectId;

    @ApiModelProperty(value = "寻源名称", example = "寻源1", required = true)
    private String souName;

    @ApiModelProperty(value = "寻源单号", example = "SO20220101001", required = true)
    private String souNo;

    @ApiModelProperty(value = "寻源类型", example = "1", required = true)
    private String souType;

    @ApiModelProperty(value = "招标项目编号", example = "P20220101001")
    private String extProjectNo;

    @ApiModelProperty(value = "单据状态（枚举类:CaStatusEnum）", example = "1")
    private String status;

    @ApiModelProperty(value = "废弃说明", example = "废弃原因")
    private String discardDescription;

    @ApiModelProperty(value = "需求部门ID", example = "1")
    private String demandDepartmentId;

    @ApiModelProperty(value = "需求部门编码", example = "DEPT001")
    private String demandDepartmentCode;

    @ApiModelProperty(value = "需求部门名称", example = "部门1")
    private String demandDepartmentName;

    @ApiModelProperty(value = "需求人id(可多选)", example = "1,2,3")
    private String demandUserId;

    @ApiModelProperty(value = "需求人工号", example = "1001")
    private String demandUserName;

    @ApiModelProperty(value = "需求人名称", example = "张三")
    private String demandUserNickname;

    @ApiModelProperty(value = "质保期", example = "1年", required = true)
    private String warrantyPeriod;

    @ApiModelProperty(value = "预算", example = "1000.00")
    private BigDecimal extBudget;

    @ApiModelProperty(value = "工期/交货期要求", example = "2022-01-01")
    private String timeLimit;

    @ApiModelProperty(value = "发标时间", example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    @ApiModelProperty(value = "收标时间", example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date busEndTime;

    @ApiModelProperty(value = "评标结束", example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date techEvaluationTime;

    @ApiModelProperty(value = "开价格标时间", example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date priceOpeningTime;

    @ApiModelProperty(value = "项目概况与招标范围", example = "项目概况与招标范围")
    private String projectOverviewAndBidScope;

    @ApiModelProperty(value = "付款要求", example = "付款要求")
    private String paymentRequirements;

    @ApiModelProperty(value = "备注", example = "备注")
    private String remark;

    @ApiModelProperty(value = "投标供应商", example = "投标供应商")
    private List<CaOrderDTO> caOrders;

    @ApiModelProperty(value = "供应商选定结果DTO", example = "供应商选定结果DTO")
    private List<CaSelectionResultDTO> caSelectionResults;

    @ApiModelProperty(value = "供应商总体情况DTO", example = "供应商总体情况DTO")
    private List<CaSupplierDTO> caSuppliers;

    @ApiModelProperty(value = "投标时间DTO", example = "投标时间DTO")
    private List<CaTenderTimeDto> caTenderTimes;

    @ApiModelProperty(value = "供应商谈判DTO", example = "供应商谈判DTO")
    private List<CaNegotiateDto> caNegotiates;

    @ApiModelProperty(value = "定/废标申请附件", example = "定/废标申请附件")
    private List<SceneFile> sceneFiles;

    @ApiModelProperty(value = "原定标申请附件", example = "原定标申请附件")
    private List<SceneFile> originalSceneFiles;

    @ApiModelProperty(value = "废弃定标说明", example = "废弃定标说明")
    private String abandonDesc;

    @ApiModelProperty(value = "合并申请号", example = "PR2023092300101,PR2023092300102,PR2023092300103")
    private String applicantNo;

    @ApiModelProperty(value = "合并申请ID", example = "1,2,3")
    private String applicantId;

    /**
     * 品类ID
     */
    @ApiModelProperty(value = "品类ID", example = "品类ID")
    private Long extCategoryId;
    /**
     * 品类编码
     */
    @ApiModelProperty(value = "品类编码", example = "品类编码")
    private String extCategoryCode;
    /**
     * 品类
     */
    @ApiModelProperty(value = "品类", example = "品类")
    private String extCategoryName;
    /**
     * 是否下发
     */
    @ApiModelProperty(value = "是否下发", example = "是否下发")
    private String ifAuth;

    /**
     * 是否下发
     */
    @ApiModelProperty(value = "是否填写", example = "是否填写")
    private String ifWrite;
    /**
     * 技术权重
     */
    @ApiModelProperty(value = "技术权重", example = "技术权重")
    private BigDecimal techWeight;

    /**
     * 合同经办人ID
     */
    @ApiModelProperty(value = "合同经办人ID", example = "合同经办人ID")
    private Long contractOperatorUserId;

    /**
     * 合同经办人账号
     */
    @ApiModelProperty(value = "合同经办人账号", example = "合同经办人账号")
    private String contractOperatorUsername;

    /**
     * 合同经办人名称
     */
    @ApiModelProperty(value = "合同经办人名称", example = "合同经办人名称")
    private String contractOperatorNickname;

    /**
     * 评分规则
     */
    @ApiModelProperty(value = "评分规则", example = "评分规则")
    private String extScoreRule;

    /**
     * 招标流程
     */
    @ApiModelProperty(value = "招标流程", example = "招标流程")
    private String extSouProcess;

    /**
     * 第一层级审批人ID
     */
    @ApiModelProperty(value = "第一层级审批人ID")
    private Long approvalUserId;

    /**
     * 第一层级审批人账号
     */
    @ApiModelProperty(value = "第一层级审批人账号")
    private String approvalUserName;

    /**
     * 第一层级审批人姓名
     */
    @ApiModelProperty(value = "第一层级审批人姓名")
    private String approvalNickname;

    /**
     * bpm发起人账号
     */
    @ApiModelProperty(value = "bpm发起人账号")
    private String startBpmUsername;

    /**
     * bpm发起人名称
     */
    @ApiModelProperty(value = "bpm发起人名称")
    private String startBpmNickname;

    /**
     * 废弃 bpm发起人账号
     */
    @ApiModelProperty(value = "废弃 bpm发起人账号")
    private String startBpmDiscardUsername;

    /**
     * 废弃 bpm发起人名称
     */
    @ApiModelProperty(value = "废弃 bpm发起人名称")
    private String startBpmDiscardNickname;

    @ApiModelProperty("轮次，字典 NPM_CA_PRICE_ROUND")
    private String caRound;
    @ApiModelProperty(value = "供应商资质要求", example = "供应商资质要求")
    private String vendorFlairAdjure;
    @ApiModelProperty(value = "供应商业绩要求", example = "供应商业绩要求")
    private String vendorBizAdjure;
    @ApiModelProperty(value = "预算与报价差异分析", example = "预算与报价差异分析")
    private String budgetPriceDiff;
    @ApiModelProperty(value = "前期采购情况", example = "前期采购情况")
    private String previousPurchase;
    @ApiModelProperty(value = "厂家报价差异分析", example = "厂家报价差异分析")
    private String manufacturerAnalysis;

    @ApiModelProperty("供应商谈判内容")
    private CaNegotiateExtendDto caNegotiateExtend;

    @ApiModelProperty(value = "定标申请价格历史关联表")
    private List<CaPriceDto> caPrices;

    @ApiModelProperty(value = "定标申请价格历史列表")
    private List<BidPriceDto> historyPriceList;

}

