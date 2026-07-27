package com.midea.cloud.srm.model.pj.supplier.rev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author huangbf3
 * <pre>
 *  资质审查单据 模型
 * </pre>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_sup_auth_review_form")
@ApiModel(description = "资质审查")
@QlMatchType("ReviewForm")
public class ReviewForm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId("REVIEW_FORM_ID")
    private Long reviewFormId;

    @ApiModelProperty(value = "资质审查单号")
    @TableField("REVIEW_FORM_NUMBER")
    private String reviewFormNumber;

    @ApiModelProperty(value = "资质审查类型,参考字典码QUA_REVIEW_TYPE")
    @TableField("QUA_REVIEW_TYPE")
    private String quaReviewType;

    @ApiModelProperty(value = "资质审查类型名称")
    @TableField("QUA_REVIEW_TYPE_NAME")
    private String quaReviewTypeName;

    @ApiModelProperty(value = "供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty(value = "是否研发部门发起(Y是 N否),参考字典码YES_OR_NO")
    @TableField("IF_DEVELOP")
    private String ifDevelop;

    @ApiModelProperty(value = "是否现场审查(Y是 N否),参考字典码YES_OR_NO")
    @TableField("IF_SITE_FORM")
    private String ifSiteForm;

    @ApiModelProperty(value = "审查单据说明")
    @TableField("REVIEW_EXPLAIN")
    private String reviewExplain;

    @ApiModelProperty(value = "审批状态(DRAFT拟定、SUBMITTED已提交、REJECTED已驳回、APPROVED已审批,参考字典码APPROVE_STATUS_TYPE," +
            "枚举类：ApproveStatusType")
    @TableField("APPROVE_STATUS")
    private String approveStatus;

    @ApiModelProperty(value = "部门ID")
    @TableField("CEEA_DEPT_ID")
    private Long ceeaDeptId;

    @ApiModelProperty(value = "部门名称")
    @TableField("CEEA_DEPT_NAME")
    private String ceeaDeptName;

    @ApiModelProperty(value = "是否供应商认证")
    @TableField("CEEA_IF_VENDOR_AUTH")
    private String ceeaIfVendorAuth;

    @ApiModelProperty(value = "需求分析")
    @TableField("CEEA_DEMAND_ANALYSIS")
    private String ceeaDemandAnalysis;

    @ApiModelProperty(value = "市场供应分析")
    @TableField("CEEA_SUP_ANALYSIS")
    private String ceeaSupAnalysis;

    @ApiModelProperty(value = "品类本期采购策略")
    @TableField("CEEA_CATEGORY_STRATEGY")
    private String ceeaCategoryStrategy;

    @ApiModelProperty(value = "供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;

    @ApiModelProperty(value = "供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;

    @ApiModelProperty(value = "附件上传")
    @TableField(exist = false)
    List<Fileupload> fileUploads;
}
