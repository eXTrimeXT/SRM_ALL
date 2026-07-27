package com.midea.cloud.srm.model.sou.designplans.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class PullQueDto implements Serializable {

    private Long infoId;
    private String infoCode;
    private String infoName;
    List<PullQueDto> twoLevelList;
}
