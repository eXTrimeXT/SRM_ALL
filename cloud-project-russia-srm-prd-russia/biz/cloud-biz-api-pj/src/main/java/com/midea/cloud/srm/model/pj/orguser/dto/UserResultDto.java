package com.midea.cloud.srm.model.pj.orguser.dto;

import lombok.Data;

import java.util.List;

/**
 * @author ex_liuxy46
 */
@Data
public class UserResultDto {

    private Integer total;
    private Integer pageCount;
    private Integer size;
    private Integer page;
    private List<UserRowsDto> rows;
}
