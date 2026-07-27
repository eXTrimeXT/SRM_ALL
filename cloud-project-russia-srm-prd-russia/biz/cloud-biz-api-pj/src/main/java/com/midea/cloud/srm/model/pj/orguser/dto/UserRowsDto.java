package com.midea.cloud.srm.model.pj.orguser.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ex_liuxy46
 */
@Data
public class UserRowsDto {

    private Long organizationIds;
    private Date createTime;
    private String chineseName;
    private String company;
    private String remark;
    private Date updateTime;
    private Long id;
    private Integer personType;
    private String personnelNo;
}
