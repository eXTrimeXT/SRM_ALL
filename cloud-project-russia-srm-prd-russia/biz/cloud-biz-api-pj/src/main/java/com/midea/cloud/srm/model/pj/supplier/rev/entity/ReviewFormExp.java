package com.midea.cloud.srm.model.pj.supplier.rev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <pre>
 *  资质审查原因描述 模型
 * </pre>
 *
 * @author chensl26@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-03-10 16:49:31
 *  修改内容:
 * </pre>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_sup_auth_review_form_exp")
@ApiModel(description = "资质审查原因描述")
@QlMatchType("ReviewFormExp")
public class ReviewFormExp extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资质审查原因描述ID")
    @TableId("REVIEW_FORM_EXP_ID")
    private Long reviewFormExpId;

    @ApiModelProperty(value = "资质审查单据ID")
    @TableField("REVIEW_FORM_ID")
    private Long reviewFormId;

    @ApiModelProperty(value = "供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty(value = "审查原因,参考字典码REVIEW_REASON_TYPE")
    @TableField("REVIEW_REASON")
    private String reviewReason;

    @ApiModelProperty(value = "原因描述")
    @TableField("REASON_EXPLAIN")
    private String reasonExplain;


}
