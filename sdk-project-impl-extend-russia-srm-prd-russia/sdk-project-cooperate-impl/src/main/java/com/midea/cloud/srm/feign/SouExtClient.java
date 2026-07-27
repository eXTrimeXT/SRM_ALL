package com.midea.cloud.srm.feign;

import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementDTO;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementQueryDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.swagger.init.ApiSouInitSwaggerDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@FeignClient(value = "cloud-biz-sou", contextId = "SouExtClient", path = "/api-sou")
public interface SouExtClient {
    /**
     * 获取集采协议管理列表
     * @param dto 参数
     * @return 返回
     */
    @ApiOperation(value = "按区域+物料查询有效价格协议", notes = "获取集采协议管理列表", httpMethod = "POST")
    @PostMapping("/jcAgreement/getValidPriceList")
    List<PriceAgreementDTO> getValidPriceList(@RequestBody PriceAgreementQueryDTO dto);

    /**
     * editInitInfo
     * @param param
     * @param souType
     * @return
     */
    @PostMapping({"/api/v1/buyer/{souType}/init/ext/editInitInfo"})
    @ApiOperation("采购商端-寻源立项-保存寻源立项信息(整体)")
    ApiSouInitSwaggerDTO editInitInfo(@RequestBody ApiSouInitSwaggerDTO param, @PathVariable("souType") @ApiParam("寻源类型(inq/bid/brg/comp/...)") String souType);

    /**
     * 取消招标单任务、待办
     * @param projectIdList
     */
    @PostMapping("/ext/buyer/bid/init/cancleBid")
    void cancleBid(@RequestBody List<Long> projectIdList);
}
