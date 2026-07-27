package com.midea.cloud.srm.model.perf.ordercheck.entity;

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
@TableName("scc_npm_score_item_order_check_detail")
@ApiModel(description = "订单化绩效复核-明细")
public class PerfScoreItemsOrderCheckDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId("ORDER_CHECK_DETAIL_ID")
    private Long orderCheckDetailId;

    @ApiModelProperty(value = "复核头id")
    @TableField("ORDER_CHECK_ID")
    private Long orderCheckId;

    @ApiModelProperty(value = "评分人账号ID")
    @TableField("SCORE_USER_ID")
    private Long scoreUserId;

    @ApiModelProperty(value = "评分人账号")
    @TableField("SCORE_USER_NAME")
    private String scoreUserName;

    @ApiModelProperty(value = "评分人名称")
    @TableField("SCORE_NICK_NAME")
    private String scoreNickName;

    @ApiModelProperty(value = "品类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty(value = "品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    @ApiModelProperty(value = "品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

    @ApiModelProperty(value = "复核状态")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "关联绩效评分项目主表ID")
    @TableField(exist = false)
    private Long scoreItemsId;

    @ApiModelProperty(value = "供应商ID")
    @TableField(exist = false)
    private Long companyId;

}
