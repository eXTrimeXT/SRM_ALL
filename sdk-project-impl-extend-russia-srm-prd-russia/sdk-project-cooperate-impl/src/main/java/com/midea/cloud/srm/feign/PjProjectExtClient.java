package com.midea.cloud.srm.feign;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.pricetax.entity.PriceRate;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 备注
 * @author huangbf3
 */
@FeignClient(
        value = "cloud-biz-pj", contextId = "pjProjectExt", path = "/api-pj"
)
public interface PjProjectExtClient {
    /**
     * 备注
     * @param username 参数
     * @return 返回
     */
    @GetMapping("/pj-anon/user/getHrUserOrgnizationByUsername")
    HrUserOrgnizationDto getHrUserOrgnizationByUsername(@RequestParam String username);

    /**
     * 根据用户账号获取用户信息
     * @param personnelNo 参数
     * @return 返回
     */
    @ApiOperation(value = "根据用户账号获取用户信息")
    @GetMapping("/pj-anon/user/getSccUserByPersonnelNo")
    SccPjUser getSccUserByPersonnelNo(@RequestParam("personnelNo") String personnelNo);

    /**
     * 备注
     * @param tradingCurrency
     * @param currenyCode
     * @param date
     * @return
     */
    @GetMapping("/pj-anon/exchangeRate/queryExchangeRate")
    List<PriceRate> queryExchangeRate(@RequestParam String tradingCurrency, @RequestParam String currenyCode, @RequestParam String date);

    /**
     * 备注
     * @param data
     */
    @PostMapping("/easPurchase/pushInvoiceNotice")
    void pushInvoiceNotice(@RequestBody Object data);

    /**
     * 备注
     * @param data
     */
    @PostMapping("/easPurchase/pushDeliveryNote")
    void pushDeliveryNote(@RequestBody Object data);

    /**
     * 备注
     * @param data
     * @return
     */
    @PostMapping("/easPurchase/getActualStock")
    List<Map<String,Object>> getActualStock(@RequestBody Object data);

    /**
     * 通过板块ID查询寻源模板
     * @param buCode
     * @return
     */
    @GetMapping("/pj-anon/source/pubconfig/queryByBuCode")
    Optional<SccPjSourcePubconfig> queryByBuCode(@RequestParam("buCode") String buCode);

    /**
     * 发票报销接口
     * @param params
     * @return
     */
    @PostMapping("/invoiceApi/reimburse")
    Object reimburse(@RequestBody List<Map<String, Object>> params);


    /**
     * 根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录
     * @param flowInstanceRecord 参数
     * @return 返回
     */
    @ApiOperation(value = "根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录")
    @PostMapping("/external/bpm/getLastFlowInstanceRecord")
    FlowInstanceRecord getLastFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord);

    /**
     * 保存BPM新审批流标志
     * @param bpmNewFlag 参数
     * @return 返回
     */
    @ApiOperation(value = "获取BPM新审批流标志")
    @PostMapping("/bpmFlow/bpmNewFlag/saveOrUpdate")
    void saveOrUpdateBpmNewFlag(@RequestBody BpmNewFlag bpmNewFlag);

    /**
     * 获取BPM新审批流标志
     * @param bpmNewFlag 参数
     * @return 返回
     */
    @ApiOperation(value = "获取BPM新审批流标志")
    @PostMapping("/bpmFlow/bpmNewFlag/get")
    BpmNewFlag getBpmNewFlag(@RequestBody BpmNewFlag bpmNewFlag);

    /**
     * 发送短信
     * @param content 参数
     * @param phones 参数
     * @return 返回
     */
    @ApiOperation(value = "发送短信")
    @PostMapping("/external/SMS/message")
    JSONObject message(@RequestParam("content") String content, @RequestParam("phones") String phones);

    /**
     * 推送采购订单到EDM
     * @param pa 参数
     * @return 返回
     */
    @ApiOperation(value = "推送采购订单到EDM")
    @PostMapping("/external/edm/pur/order/push")
    JSONObject pushPurOrderToEdm(@RequestBody String pa);

    /**
     * 推送采购订单到EDM
     * @param pa 参数
     * @return 返回
     */
    @ApiOperation(value = "推送采购订单到EDM")
    @PostMapping("/external/edm/edmDraftOrderBackHaul")
    JSONObject edmDraftOrderBackHaul(@RequestBody String pa);

    /**
     * 根据组织ID获取组织
     * @param organizationId
     * @return
     */
    @ApiOperation(value = "根据组织ID获取组织")
    @GetMapping("/organization/organization/getOrganization")
    OrganizationEditDto findList(@RequestParam("organizationId") Long organizationId);

}
