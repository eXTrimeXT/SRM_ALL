package com.midea.cloud.srm.model.pj.sou.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 供应商评分管理记录
 * @author: hesl41
 * @Date: 2022/10/17 10:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_supplier_score_manage_record")
@ApiModel("供应商评分管理记录")
public class SouSupplierScoreManageRecord extends BaseEntity<SouSupplierScoreManageRecord> {

    @ApiModelProperty("ID")
    @TableId("SUPPLIER_SCORE_MANAGE_RECORD_ID")
    private Long supplierScoreManageRecordId;

    @ApiModelProperty("ID")
    @TableField("SUPPLIER_SCORE_MANAGE_ID")
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

    @ApiModelProperty("评分维度表ID")
    @TableField("SCORE_DIMENSION_ID")
    private Long scoreDimensionId;

    @ApiModelProperty("评分维度编码")
    @TableField("DIMENSION_CODE")
    private String dimensionCode;

    @ApiModelProperty("评分项目")
    @TableField("SCORE_ITEM")
    private String scoreItem;

    @ApiModelProperty("评分标准")
    @TableField("DIMENSION_STANDARD")
    private String dimensionStandard;

    @ApiModelProperty("轮次")
    @TableField("ROUND")
    private Integer round;

    @ApiModelProperty("评分状态:已完成,未完成")
    @TableField("SUPPLIER_SCORE_MANAGE_RECORD_STATUS")
    private String supplierScoreManageRecordStatus;

    /**
     * @see SouScoreDimensionDetails#getScoreDimensionDeatailsId()
     */
    @ApiModelProperty("ID")
    @TableField("SCORE_DIMENSION_DETAILS_ID")
    private Long scoreDimensionDeatailsId;

    @ApiModelProperty("权重%")
    @TableField("WEIGHT")
    private BigDecimal weight;

    @ApiModelProperty("满分值")
    @TableField("FULL_SCORE")
    private BigDecimal fullScore;

    @ApiModelProperty("评分值")
    @TableField("SCORE")
    private BigDecimal score;
    /** OK , NG */
    @ApiModelProperty("评分结果")
    @TableField("RESULT")
    private String result;

    @ApiModelProperty("评分说明")
    @TableField("INSTRUCTIONS")
    private String instructions;




}
