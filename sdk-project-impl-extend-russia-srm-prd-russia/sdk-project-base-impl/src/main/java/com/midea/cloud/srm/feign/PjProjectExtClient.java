package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.base.organization.entity.ErpBranchBank;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialParam;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialResultDto;
import com.midea.cloud.srm.model.pj.changchengapi.yangguan.ResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * PJ Feign
 * @author huangbf3
 */
@FeignClient(value = "cloud-biz-pj", contextId = "baseProjectExt", path = "/api-pj")
public interface PjProjectExtClient {

    /**
     * materialPage
     * @param materialParam
     * @return
     */
    @PostMapping("/external/material/page")
    ResultDTO<MaterialResultDto> materialPage(@RequestBody MaterialParam materialParam);

    /**
     * 同步银行信息
     * @return
     */
    @PostMapping("/branch-Bank/bankSync")
    List<ErpBranchBank> bankSync();


}
