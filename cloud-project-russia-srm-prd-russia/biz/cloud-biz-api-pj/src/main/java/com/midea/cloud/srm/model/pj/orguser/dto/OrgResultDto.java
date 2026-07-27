package com.midea.cloud.srm.model.pj.orguser.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Data
public class OrgResultDto {

    private Long id;
    private String groupName;
    private String groupNameEn;
    private Long parentId;
    private String showOrder;
    private String businessType;
    private String createUserCode;
    private String updateUserCode;
    private Date createTime;
    private Date updateTime;
    private List<OrgResultDto> childrenList;
}
