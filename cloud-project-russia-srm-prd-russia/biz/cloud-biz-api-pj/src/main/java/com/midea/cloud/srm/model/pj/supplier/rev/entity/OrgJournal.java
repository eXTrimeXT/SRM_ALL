package com.midea.cloud.srm.model.pj.supplier.rev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.supplier.info.entity.OrgJournalPayPlan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 *  合作ou日志表 模型
 * </pre>
 *
 * @author chensl26@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-08-08 11:08:06
 *  修改内容:
 * </pre>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("ceea_sup_auth_org_journal")
@ApiModel(description = "合作ou日志")
@QlMatchType("OrgJournal")
public class OrgJournal extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID,合作ou日志ID")
    @TableId("ORG_JOURNAL_ID")
    private Long orgJournalId;

    @ApiModelProperty(value = "合作ou日志单据类型")
    @TableField("FORM_TYPE")
    private String formType;

    @ApiModelProperty(value = "合作ou日志单据ID")
    @TableField("FORM_ID")
    private Long formId;

    @ApiModelProperty(value = "供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty(value = "合作ou名称")
    @TableField("ORG_NAME")
    private String orgName;

    @ApiModelProperty(value = "合作ou编码")
    @TableField("ORG_CODE")
    private String orgCode;

    @ApiModelProperty(value = "合作ouID")
    @TableField("ORG_ID")
    private Long orgId;

    @ApiModelProperty(value = "事业部ID")
    @TableField("DIVISION_ID")
    private String divisionId;

    @ApiModelProperty(value = "事业部名称")
    @TableField("DIVISION")
    private String division;

    @ApiModelProperty(value = "ou服务状态")
    @TableField("ORG_SERVICE_STATUS")
    private String orgServiceStatus;

    @ApiModelProperty(value = "生效时间")
    @TableField("START_DATE")
    private LocalDate startDate;

    @ApiModelProperty(value = "失效时间")
    @TableField("END_DATE")
    private LocalDate endDate;

    @ApiModelProperty("寻源单ID")
    @TableField("REQ_HEAD_ID")
    private Long reqHeadId;

    @TableField(exist = false)
    private List<OrgJournalPayPlan> orgJournalPayPlanList;

}
