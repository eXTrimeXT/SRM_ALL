package com.midea.cloud.srm.model.sou.designplans.dto;

import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandSup;
import lombok.Data;

import java.util.List;

/**
 * @author ex_liuxy46
 */
@Data
public class SupDto {

    private Long designId;

    private List<SccSouChDemandSup> list;
}
