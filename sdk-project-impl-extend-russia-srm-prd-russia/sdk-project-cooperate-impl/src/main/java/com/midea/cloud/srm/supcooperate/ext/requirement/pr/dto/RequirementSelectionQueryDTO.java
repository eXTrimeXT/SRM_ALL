package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import com.mideacloud.common.dto.request.PageRequest;
import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class RequirementSelectionQueryDTO extends PageRequest {

        private Long orgId;
        private List<Long> orgIds;
        private String requirementHeadNum;
        private String materialCode;
        private String extUseDepartmentName;
        private Long ceeaPerformUserId;
        private String vendorName;
        private String materialName;
        private List<Long> requirementLineIds;

}
