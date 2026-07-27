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
 *  定点通知-联系人信息
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/13 11:10
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_point_notice_link")
@ApiModel(description = "定点通知-联系人信息")
public class PointNoticeLink extends BaseEntity<PointNoticeLink> {
    /**
     * 联系人ID
     */
    @ApiModelProperty("联系人ID")
    @TableId("LINK_ID")
    private Long linkId;

    /**
     * 定点通知ID
     */
    @ApiModelProperty("定点通知ID")
    @TableField("NOTICE_ID")
    private Long noticeId;

    /**
     * 序号
     */
    @ApiModelProperty("序号")
    @TableField("NUM")
    private Integer num;

    /**
     * 联络人类型
     */
    @ApiModelProperty("联络人类型")
    @TableField("TYPE")
    private String type;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    @TableField("NAME")
    private String name;

    /**
     * 电话
     */
    @ApiModelProperty("电话")
    @TableField("PHONE")
    private String phone;

    /**
     * 传真
     */
    @ApiModelProperty("传真")
    @TableField("FAX")
    private String fax;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @TableField("EMAIL")
    private String email;
}
