package com.midea.cloud.srm.model.pj.hrorganizationtemp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_pj_organization_temp")
public class SccPjOrganizationTemp extends BaseEntity {

    @TableId
    private Long organizationTempId;
    private Long id;
    private String groupName;
    private String groupNameEn;
    private Long parentId;
    private Integer level;
    private String directLeader;
    private String directLeaderName;
    private String groupTel;
    private String unitId;
    private String unitName;
    private String departmentId;
    private String departmentName;
    private String teamId;
    private String teamName;
    private String deleteFlag;
    private String parentGroupName;
    private String processSerialNum;
    private String processStatus;
    private String processMessage;
    private Date processDate;
    private Long processGroupId;
    private String showOrder;
    private String grade;
    private Long preOrganizationId;

    @TableField(exist = false)
    private String organizationCode;
    @TableField(exist = false)
    private String organizationTypeCode;
    @TableField(exist = false)
    private Long parentOrganizationId;
    @TableField(exist = false)
    private String orgIdPath;
    @TableField(exist = false)
    private String orgNamePath;
    @TableField(exist = false)
    private String organizationPath;
    @TableField(exist = false)
    private String orgStatusPath;
    @TableField(exist = false)
    private Long iamParentId;
}
