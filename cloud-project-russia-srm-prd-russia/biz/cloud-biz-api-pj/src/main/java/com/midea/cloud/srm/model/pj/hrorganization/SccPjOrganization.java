package com.midea.cloud.srm.model.pj.hrorganization;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
@TableName("scc_pj_organization")
public class SccPjOrganization extends BaseEntity {

    @TableId
    private Long rowId;
    private Long organizationId;
    private Long parentOrganizationId;
    private Long iamParentId;
    private Long id;
    private String organizationCode;
    private String organizationTypeCode;
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
    private String orgIdPath;
    private String orgNamePath;
    private String organizationPath;
    private String orgStatusPath;
    private String showOrder;
    private String grade;
}
