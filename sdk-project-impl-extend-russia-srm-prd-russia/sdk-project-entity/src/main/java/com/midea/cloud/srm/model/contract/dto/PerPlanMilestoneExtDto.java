package com.midea.cloud.srm.model.contract.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlanMilestone;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 100014336 ganyh19
 */
@Data
public class PerPlanMilestoneExtDto  {

    private static final long serialVersionUID = 921993L;

    @ApiModelProperty("合同履约里程碑ID")
    private Long perPlanMilestoneId;

    @ApiModelProperty("合同履约计划主键")
    private Long perPlanId;

    @ApiModelProperty("状态")
    private String planStatus;

    @ApiModelProperty("里程碑类型")
    private String milestoneType;

    @ApiModelProperty("节点负责人ID")
    private Long nodePersonId;

    @ApiModelProperty("节点负责人编码")
    private String nodePersonBy;

    @ApiModelProperty("节点负责人名称")
    private String nodePersonName;

    @ApiModelProperty("计划开始日期")
    private Date planStartDate;

    @ApiModelProperty("计划结束日期")
    private Date planEndDate;

    @ApiModelProperty("实际完成实际")
    private Date actualCompleteDate;

    @ApiModelProperty("节点交付数量")
    private Integer nodePlanNum;

    @ApiModelProperty("实际结束日期")
    private Date practicallyEndDate;

    @ApiModelProperty("驳回原因")
    private String rejectionReason;

    @ApiModelProperty("特殊备注")
    private String remarks;

    @ApiModelProperty("附件ID")
    private Long fileId;

    @ApiModelProperty("附件名称")
    private String fileName;

    @ApiModelProperty("履约模板配置行ID")
    private Long performTemplLineId;

    @ApiModelProperty("序号")
    private Integer serialNumber;

    @ApiModelProperty("附件数量")
    private Integer filenum;

    @ApiModelProperty("交付按钮：SHOW显示，NOT_SHOW不显示")
    private String deliveryButton;

    /**
     * 是否已创建评分项目
     */
    @ApiModelProperty("是否已创建评分项目")
    private String extCreatePerformFlag;
}
