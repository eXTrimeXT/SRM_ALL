package com.midea.cloud.srm.model.pj.sou.car.point.notice.entity;

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
 *  定点通知-基础信息
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/13 10:56
 *  修改内容:
 * </pre>
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_point_notice")
@ApiModel(description = "定点通知-基础信息")
public class PointNotice extends BaseEntity<PointNotice> {
    /**
     * 定点通知ID
     */
    @ApiModelProperty("定点通知ID")
    @TableId("NOTICE_ID")
    private Long noticeId;

    /**
     * 会签ID
     */
    @ApiModelProperty("会签ID")
    @TableField("SIGN_ID")
    private Long signId;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    @TableField("NOTICE_TITLE")
    private String noticeTitle;

    /**
     * 定点通知单号
     */
    @ApiModelProperty("定点通知单号")
    @TableField("NOTICE_NO")
    private String noticeNo;

    /**
     * 发布状态（字典：SOU_POINT_NOTICE_PUBLISH_STATUS）
     */
    @ApiModelProperty("发布状态（字典：SOU_POINT_NOTICE_PUBLISH_STATUS）")
    @TableField("PUBLISH_STATUS")
    private String publishStatus;

    /**
     * 审批状态（字典：SOU_POINT_NOTICE_AUDIT_STATUS）
     */
    @ApiModelProperty("审批状态（字典：SOU_POINT_NOTICE_AUDIT_STATUS）")
    @TableField("audit_status")
    private String auditStatus;

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
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    /**
     * 供应商编码
     */
    @ApiModelProperty("供应商编码")
    @TableField("VENDOR_CODE")
    private String vendorCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    @TableField("VENDOR_NAME")
    private String vendorName;


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
     * 定点通知书附件ID
     */
    @ApiModelProperty("定点通知书附件ID")
    @TableField("FILE_ID")
    private String fileId;

    /**
     * 定点通知书附件名称
     */
    @ApiModelProperty("定点通知书附件名称")
    @TableField("FILE_NAME")
    private String fileName;

}
