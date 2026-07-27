package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.select.ApiSouSelectChangePricingResultDTO;
import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceContractSignDTO;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLineContractDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 100014336 ganyh19
 */
@FeignClient(
        value = "${cloud.scc.feign-name-mapping.cloud-biz-sou:cloud-biz-sou}",
        path = "${cloud.scc.feign-name-mapping.cloud-biz-sou-path:/api-sou}",
        contextId = "cloud-biz-sou-contract-ext"
)
public interface ContractSouExtClient {

    /**
     * 更新合同相关信息
     * @param extFixPriceContractSignDTO
     */
    @PostMapping({"/npm/fix-price/contract/updateContractSign"})
    @ApiOperation("更新合同相关信息")
    void updateContractSign(@RequestBody ExtFixPriceContractSignDTO extFixPriceContractSignDTO);

    /**
     * 更新临采定价是否签名
     * @param extFixPriceContractSignDTOs
     */
    @PostMapping("/npm/fix-price/contract/updateTempProcureContractSigns")
    void updateTempPurchaseContractSigns(@RequestBody List<ExtFixPriceContractSignDTO> extFixPriceContractSignDTOs);

    /**
     * 通过id查找priceLine
     * @param fixPriceLineIds
     * @return
     */
    @ApiOperation("通过id查找priceLine")
    @PostMapping("/npm/fix-price/contract/fixPriceLineList")
    List<ExtFixPriceLine> fixPriceLineList(@RequestBody List<String> fixPriceLineIds);

    /**
     * 更新合同相关信息
     * @param fixPriceLineIds
     * @return
     */
    @PostMapping("/npm/pur-fix-price/contract/purFixPriceLineList")
    @ApiOperation("通过id查找priceLine")
   List<ExtPurFixPriceLineContractDTO> purFixPriceLineList(@RequestBody List<String> fixPriceLineIds);

    /**
     * 添加或更新集采管理
     * @param sccSouJcAgreement
     * @return
     */
    @ApiOperation(value = "添加或更新集采管理", notes = "添加或更新集采管理", httpMethod = "POST")
    @PostMapping("/jcAgreement/contract/saveOrUpdateJcAgreement")
    SccSouJcAgreement saveOrUpdateJcAgreement(@RequestBody SccSouJcAgreement sccSouJcAgreement);

    /**
     * 更新集采合同相关信息
     * @param extPurFixPriceContractSignDTO
     */
    @PostMapping("/npm/pur-fix-price/contract/updateContractSign")
    @ApiOperation("更新合同相关信息")
    void updateContractSign(@RequestBody ExtPurFixPriceContractSignDTO extPurFixPriceContractSignDTO);

    /**
     * 更新集采合同相关信息
     * @param extPurFixPriceContractSignDTOS
     */
    @PostMapping("/npm/pur-fix-price/contract/updateCentPurchaseContractSigns")
    void updateCentPurchaseContractSigns(@RequestBody List<ExtPurFixPriceContractSignDTO> extPurFixPriceContractSignDTOS);
    /**
     * 根据项目编号查询项目名称
     * @param extSouProject 参数
     * @return 返回
     */
    @ApiOperation(value = "根据项目编号获取项目名称")
    @PostMapping("/sourcing/init/queryByProjectNo")
    List<ExtSouProject> queryByProjectNo(@RequestBody ExtSouProject extSouProject);
}
