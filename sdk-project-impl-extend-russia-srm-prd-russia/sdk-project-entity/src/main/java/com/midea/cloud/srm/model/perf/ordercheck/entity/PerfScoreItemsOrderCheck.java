package com.midea.cloud.srm.model.perf.scoreproject.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * <pre>
 *  绩效评分项目供应商表 模型
 * </pre>
 *
 * @author luxc18@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-06-06 15:10:37
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_npm_score_item_order_check")
@ApiModel(description = "订单化绩效复核")
public class PerfScoreItemsOrderCheck extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("ORDER_CHECK_ID")
    private Long orderCheckId;

    @ApiModelProperty(value = "关联绩效评分项目主表ID")
    @TableField("SCORE_ITEMS_ID")
    private Long scoreItemsId;

    @ApiModelProperty(value = "项目名称")
    @TableField("PROJECT_NAME")
    private String projectName;

    @ApiModelProperty(value = "采购组织ID")
    @TableField("ORGANIZATION_ID")
    private Long organizationId;

    @ApiModelProperty(value = "采购组织名称")
    @TableField("ORGANIZATION_NAME")
    private String organizationName;

    @ApiModelProperty(value = "供应商ID")
    @TableField("COMPANY_ID")
    private Long companyId;

    @ApiModelProperty(value = "供应商编码")
    @TableField("COMPANY_CODE")
    private String companyCode;

    @ApiModelProperty(value = "供应商名称")
    @TableField("COMPANY_NAME")
    private String companyName;

    @ApiModelProperty(value = "绩效开始月份(2020-01)")
    @TableField(value = "PER_START_MONTH")
    private LocalDate perStartMonth;

    @ApiModelProperty(value = "绩效结束月份(2020-02)")
    @TableField(value = "PER_END_MONTH")
    private LocalDate perEndMonth;

    @ApiModelProperty(value = "复核状态")
    @TableField("STATUS")
    private String status;

}
