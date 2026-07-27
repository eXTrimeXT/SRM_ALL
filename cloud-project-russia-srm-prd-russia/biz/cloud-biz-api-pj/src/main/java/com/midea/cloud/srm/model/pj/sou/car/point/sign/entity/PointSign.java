package com.midea.cloud.srm.model.pj.sou.car.point.sign.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 *  寻源-定点会签头表
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/10 16:13
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_point_sign")
@ApiModel(description = "寻源-定点会签头表")
public class PointSign extends BaseEntity<PointSign> {
    /**
     * 定点会签ID
     */
    @ApiModelProperty("定点会签ID")
    @TableId("SIGN_ID")
    private Long signId;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    @TableField("SIGN_TITLE")
    private String signTitle;

    /**
     * 定点会签单号
     */
    @ApiModelProperty("定点会签单号")
    @TableField("SIGN_NO")
    private String signNo;

    /**
     * 单据状态(字典:SOU_POINT_SIGN_STATUS)
     * DRAFT:拟定;SUBMITTED:已提交;APPROVED:已审批;REJECTED:已驳回;CANCEL:已作废
     */
    @ApiModelProperty("单据状态(字典:SOU_POINT_SIGN_STATUS [DRAFT:拟定;SUBMITTED:已提交;APPROVED:已审批;REJECTED:已驳回;CANCEL:已作废])")
    @TableField("STATUS")
    private String status;

    /**
     * 来源 (字典:SOU_POINT_SIGN_SOURCE [SOU:寻源结果;MANUAL:手工创建])
     */
    @ApiModelProperty("来源 (字典:SOU_POINT_SIGN_SOURCE [SOU:寻源结果;MANUAL:手工创建])")
    @TableField("SOURCE")
    private String source;

    /**
     * 询价ID
     */
    @ApiModelProperty("询价ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /**
     * 询价单号
     */
    @ApiModelProperty("询价单号")
    @TableField("SOU_NO")
    private String souNo;

    /**
     * 询价标题
     */
    @ApiModelProperty("询价标题")
    @TableField("SOU_NAME")
    private String souName;

    /**
     * 公司ID
     */
    @ApiModelProperty("公司ID")
    @TableField("OU_ID")
    private Long ouId;

    /**
     * 公司编码
     */
    @ApiModelProperty("公司编码")
    @TableField("OU_CODE")
    private String ouCode;

    /**
     * 公司名称
     */
    @ApiModelProperty("公司名称")
    @TableField("OU_NAME")
    private String ouName;

    /**
     * 单据类型(字典:SOU_POINT_SIGN_TYPE SIGN_PARTS:零部件定点会签;SIGN_CONTINUS_PARTS:零部件沿用会签;SIGN_RD:研发非生产会签;SIGN_CONTINUS_RD:研发非生产沿用会签;SIGN_RD_END:研发非生产会签终止)
     */
    @ApiModelProperty("单据类型(字典:SOU_POINT_SIGN_TYPE SIGN_PARTS:零部件定点会签;SIGN_CONTINUS_PARTS:零部件沿用会签;SIGN_RD:研发非生产会签;SIGN_CONTINUS_RD:研发非生产沿用会签;SIGN_RD_END:研发非生产会签终止)")
    @TableField("TYPE")
    private String type;

    /**
     * 定点说明
     */
    @ApiModelProperty("定点说明")
    @TableField("MEMO")
    private String memo;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("REMARK")
    private String remark;

    /**
     * 寻源类型
     */
    @ApiModelProperty("寻源类型")
    @TableField("SOU_TYPE")
    private String souType;

    /**
     * 是否售后精品
     */
    @ApiModelProperty("是否售后精品")
    @TableField("IF_BOUTIQUE")
    private String ifBoutique;

    /**
     * 是否媒体框招
     */
    @ApiModelProperty("是否媒体框招")
    @TableField("IF_MEDIA")
    private String ifMedia;

    /**
     * 被终止定点会签ID
     */
    @ApiModelProperty("被终止定点会签ID")
    @TableField("END_SIGN_ID")
    private Long endSignId;

    /**
     * 会签终止申请说明
     */
    @ApiModelProperty("会签终止申请说明")
    @TableField("END_MEMO")
    private String endMemo;

    /**
     * 工厂ID
     */
    @ApiModelProperty("工厂ID")
    @TableField("INV_ID")
    private Long invId;

    /**
     * 工厂编码
     */
    @ApiModelProperty("工厂编码")
    @TableField("INV_CODE")
    private String invCode;

    /**
     * 工厂名称
     */
    @ApiModelProperty("工厂名称")
    @TableField("INV_NAME")
    private String invName;

}
