package com.midea.cloud.srm.base.category.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class PullQueryDto implements Serializable {

    private Long infoId;
    private String infoCode;
    private String infoName;
    List<PullQueryDto> twoLevelList;
}
