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
 * 定点通知-中标明细表
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/13 11:05
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_point_notice_result")
@ApiModel(description = "定点通知-中标明细表")
public class PointNoticeResult extends BaseEntity<PointNoticeResult> {
    /**
     * 中标明细ID
     */
    @ApiModelProperty("中标明细ID")
    @TableId("result_id")
    private Long resultId;

    /**
     * 定点通知ID
     */
    @ApiModelProperty("定点通知ID")
    @TableField("NOTICE_ID")
    private Long noticeId;

    /**
     * 车型
     */
    @ApiModelProperty("车型")
    @TableField("CAR_TYPE")
    private String carType;

    /**
     * 物料ID
     */
    @ApiModelProperty("物料ID")
    @TableField("MATERIAL_ID")
    private Long materialId;

    /**
     * 物料编码
     */
    @ApiModelProperty("物料编码")
    @TableField("MATERIAL_CODE")
    private String materialCode;

    /**
     * 物料描述
     */
    @ApiModelProperty("物料描述")
    @TableField("MATERIAL_NAME")
    private String materialName;

    /**
     * 备用字段1
     */
    @ApiModelProperty("备用字段1")
    @TableField("PROPERTIES1")
    private String properties1;

    /**
     * 备用字段2
     */
    @ApiModelProperty("备用字段2")
    @TableField("PROPERTIES2")
    private String properties2;

    /**
     * 备用字段3
     */
    @ApiModelProperty("备用字段3")
    @TableField("PROPERTIES3")
    private String properties3;

    /**
     * 备用字段4
     */
    @ApiModelProperty("备用字段4")
    @TableField("PROPERTIES4")
    private String properties4;
}
