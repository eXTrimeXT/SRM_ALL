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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <pre>
 *  品类日志表 模型
 * </pre>
 *
 * @author chensl26@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-08-08 11:11:26
 *  修改内容:
 * </pre>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ceea_sup_auth_cate_journal")
@ApiModel(description = "品类日志")
@QlMatchType("CateJournal")
public class CateJournal extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID,品类日志ID")
    @TableId("CATEGORY_JOURNAL_ID")
    private Long categoryJournalId;

    @ApiModelProperty(value = "品类日志单据类型")
    @TableField("FORM_TYPE")
    private String formType;

    @ApiModelProperty(value = "品类日志单据ID")
    @TableField("FORM_ID")
    private Long formId;

    @ApiModelProperty(value = "供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty(value = "品类名称")
    @TableField("CATEGORY_NAME")
    private String categoryName;

    @ApiModelProperty(value = "品类编码")
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    @ApiModelProperty(value = "品类ID")
    @TableField("CATEGORY_ID")
    private Long categoryId;

    @ApiModelProperty(value = "品类服务状态")
    @TableField("CATEGORY_SERVICE_STATUS")
    private String categoryServiceStatus;

    @ApiModelProperty(value = "品类全路径ID")
    @TableField("CATEGORY_FULL_ID")
    private String categoryFullId;

    @ApiModelProperty(value = "品类全路径名称")
    @TableField("CATEGORY_FULL_NAME")
    private String categoryFullName;

    @ApiModelProperty(value = "品类本年度采购金额")
    @TableField("THIS_YEAR_AMOUNT")
    private BigDecimal thisYearAmount;

    @ApiModelProperty(value = "生效时间")
    @TableField("START_DATE")
    private LocalDate startDate;

    @ApiModelProperty(value = "失效时间")
    @TableField("END_DATE")
    private LocalDate endDate;


    @ApiModelProperty(value = "是否强控品类供应商上限（Y：是，N：否，默认N）")
    @TableField("SUPPLIER_COUNT_LIMIT_FLAG")
    private String supplierCountLimitFlag;

    @ApiModelProperty(value = "品类供应商上限")
    @TableField(value = "SUPPLIER_COUNT_LIMIT")
    private Integer supplierCountLimit;

    @ApiModelProperty(value = "存在的绿牌供应商数量")
    @TableField(value = "EXIST_COUNT_OF_COMPANY")
    private Integer existCountOfCompany;

    @ApiModelProperty("寻源单ID")
    @TableField("REQ_HEAD_ID")
    private Long reqHeadId;

}
