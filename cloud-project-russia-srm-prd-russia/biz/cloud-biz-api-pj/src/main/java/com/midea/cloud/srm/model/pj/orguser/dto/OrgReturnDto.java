package com.midea.cloud.srm.model.pj.orguser.dto;

import lombok.Data;

import java.util.List;

/**
 * @author ex_liuxy46
 */
@Data
public class OrgReturnDto {

    private List<OrgResultDto> result;
    private Integer code;
    private String message;
}
