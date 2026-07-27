package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.repo;

import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementPoolQueryVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolQueryDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.service.PrSouRequirementPoolQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@RestController
public class PrSouRequirementController {

    @Autowired
    private PrSouRequirementPoolQueryService prSouRequirementPoolQueryService;

    public List<ExtPrSouRequirementPoolQueryVO> querySouPrPool(ExtPrSouRequirementPoolQueryDTO queryParam) {
        return prSouRequirementPoolQueryService.querySouPrPool(queryParam);
    }

}
