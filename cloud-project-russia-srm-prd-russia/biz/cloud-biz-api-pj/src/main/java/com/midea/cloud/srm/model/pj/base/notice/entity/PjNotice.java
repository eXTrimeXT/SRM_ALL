package com.midea.cloud.srm.model.pj.base.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <pre>
 *  公告表 模型
 * </pre>
 *
 * @author huangbf3
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020/3/27 14:45
 *  修改内容:
 * </pre>
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_pj_notice")
@ApiModel(description = "公告")
public class PjNotice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("NOTICE_ID")
    private Long noticeId;

    @ApiModelProperty(value = "公告分类")
    @TableField("NOTICE_TYPE")
    private String noticeType;

    @ApiModelProperty(value = "状态")
    @TableField("NOTICE_STATUS")
    private String noticeStatus;

    @ApiModelProperty(value = "标题")
    @TableField("TITLE")
    private String title;

    @ApiModelProperty(value = "正文")
    @TableField("DETAIL")
    private String detail;

    @ApiModelProperty(value = "附件ID")
    @TableField("FILE_RELATION_ID")
    private Long fileRelationId;

    @ApiModelProperty(value = "附件名称")
    @TableField("FILE_NAME")
    private String fileName;

    @ApiModelProperty(value = "置顶公告,Y：是,N：否")
    @TableField("IS_TOP")
    private String isTop;

    @ApiModelProperty(value = "通知来源(说明是根据其他单据来创建的)：BIDING(招标单通知)")
    @TableField("NOTICE_SOURCE")
    private String noticeSource;

    @ApiModelProperty(value = "来源单据ID")
    @TableField("SOURCE_FORM_ID")
    private Long sourceFormId;

    @ApiModelProperty(value = "发布人ID")
    @TableField("PUBLISHER_ID")
    private Long publisherId;

    @ApiModelProperty(value = "发布人")
    @TableField("PUBLISH_BY")
    private String publishBy;

    @ApiModelProperty(value = "发布时间")
    @TableField("PUBLISH_TIME")
    private Date publishTime;

}
