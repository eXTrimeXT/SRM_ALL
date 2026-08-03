package com.midea.cloud.srm.model.pj.sou.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreDimension;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 供应商评分管理
 * @author: hesl41
 * @Date: 2022/10/17 10:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_supplier_score_manage")
@ApiModel("供应商评分管理")
public class SouSupplierScoreManage extends BaseEntity<SouSupplierScoreManage> {

    @ApiModelProperty("ID")
    @TableId("SUPPLIER_SCORE_MANAGE_ID")
    private Long supplierScoreManageId;

    @ApiModelProperty("评分管理ID")
    @TableField("SCORE_MANAGE_ID")
    private Long scoreManageId;

    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;

    @ApiModelProperty(value = "联系人名称")
    @TableField("LINKMAN_NAME")
    private String linkmanName;

    @ApiModelProperty(value = "电话")
    @TableField("PHONE")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @TableField("EMAIL")
    private String email;

    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("报价单ID")
    @TableField("ORDER_HEAD_ID")
    private Long orderHeadId;

    @ApiModelProperty("寻源单编号")
    @TableField("SOU_NO")
    private String souNo;

    @ApiModelProperty("寻源单名称")
    @TableField("SOU_NAME")
    private String souName;

    @ApiModelProperty("寻源单类型")
    @TableField("SOU_TYPE")
    private String souType;
    /**
     * @see SouScoreDimension#getScoreDimensionId()
     */
    @ApiModelProperty("评分维度表ID")
    @TableField("SCORE_DIMENSION_ID")
    private Long scoreDimensionId;

    @ApiModelProperty("评分维度编码")
    @TableField("DIMENSION_CODE")
    private String dimensionCode;

    @ApiModelProperty("轮次")
    @TableField("ROUND")
    private Integer round;

    @ApiModelProperty("评分状态:已完成,未完成")
    @TableField("SUPPLIER_SCORE_MANAGE_STATUS")
    private String supplierScoreManageStatus;

    @ApiModelProperty("评分意见")
    @TableField("COMMENTS")
    private String comments;

    @ApiModelProperty("评分维度总分")
    @TableField("DIMENSION_TOTAL_SCORE")
    private BigDecimal dimensionTotalScore;

    @ApiModelProperty("评分结果")
    @TableField("RESULT")
    private String result;

    /**
     * @see SouScoreDimension#getScoreInstructions()
     */
    @ApiModelProperty("评分维度的评分说明")
    @TableField(exist = false)
    private String scoreInstructions;

}
