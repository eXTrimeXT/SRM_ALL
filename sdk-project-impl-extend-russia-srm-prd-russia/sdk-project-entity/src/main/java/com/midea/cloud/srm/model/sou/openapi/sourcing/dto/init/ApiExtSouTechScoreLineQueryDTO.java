package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.mideacloud.common.enums.YesOrNo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "招标评分明细查询条件")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSouTechScoreLineQueryDTO extends BasePage {


    /**
     * 寻源单ID
     */
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    /**
     * 评分规则明细ID
     */
    @ApiModelProperty("评分规则明细ID")
    private Long scoreRuleLineId;
    /**
     * 技术评分头ID
     */
    @ApiModelProperty("技术评分头ID")
    private Long techScoreHeadId;
    /**
     * 报价单ID
     */
    @ApiModelProperty("报价单ID")
    private Long orderId;
    /**
     * 工作小组成员ID
     */
    @ApiModelProperty("工作小组成员ID")
    private Long groupId;
    /**
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("供应商技术已投标供应商ID范围")
    List<Long> techVendorIdList;

    @ApiModelProperty("脱敏供应商")
    Map<Long, String> desensitizeVendorMap;

    @ApiModelProperty("历史评分日期 yyyy-MM-dd HH:mm:ss")
    private String submiteDate;

    @ApiModelProperty("是否扩充废标供应商")
    private String extendAbandon;

    @ApiModelProperty("是否智能评标")
    private String extendReview;
    }
