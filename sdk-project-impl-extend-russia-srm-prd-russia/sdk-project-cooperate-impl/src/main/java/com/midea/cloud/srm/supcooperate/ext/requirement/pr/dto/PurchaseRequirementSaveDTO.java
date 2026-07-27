package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementAttach;
import lombok.Data;

import java.util.List;

/**
 * @author zenghx2
 */
@Data
public class PurchaseRequirementSaveDTO extends PurchaseRequirementHeadDTO {

    private List<PurchaseRequirementLineDTO> reqLineList;

    private List<RequirementAttach> reqAttachList;
}
