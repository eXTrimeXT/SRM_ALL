package com.midea.cloud.srm.biz.pj.purveyor;

import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorRootDTO;
import io.swagger.annotations.ApiOperation;
import java.util.List;

/**
 * @author huangbf3
 */
public interface PurveyorService {
    /**
     * 备注
     * @param taxCode
     * @param region
     * @return
     */
    @ApiOperation(value = "根据社会信用代码查询供应商信息接口")
    PurveyorRootDTO searchListByTaxCodes(List<String>taxCode, String region);
}
