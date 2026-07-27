package com.midea.cloud.srm.feign;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.InternalSupplierQuery;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.sup.black.dto.BlackCompanyInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/07 17:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "cloud-biz-pj", contextId = "pjProjectExt", path = "/api-pj")
public interface PjProjectExtClient extends FlowBusinessCallbackClient {
    /**
     * 查询是否黑名单
     * @param taxCode
     * @return
     */
    @ApiOperation(value = "查询是否黑名单")
    @GetMapping("/external/blackCompany/public/mdm/sun/blackcompany/info")
    BlackCompanyInfo blackcompanyInfo(@RequestParam(value = "taxCode") String taxCode);

    /**
     * 筛查请求
     * @param companyId
     * @return
     */
    @ApiOperation(value = "筛查请求")
    @PostMapping("/external/authentication/importScreening")
    String importScreening(@RequestParam Long companyId);

    /**
     * 钉钉发送消息
     * @param content
     * @param userList
     * @return
     */
    @ApiOperation(value = "钉钉发送消息")
    @PostMapping("/external/dingding/workNotices")
    JSONObject workNotices(@RequestParam String content , @RequestBody List<String> userList);

    /**
     * 按流程分组发起流程
     * @param requestJsn
     * @param dataId
     * @param businessType
     * @return
     */
    @ApiOperation(value = "按流程分组发起流程")
    @PostMapping("/external/bpm/public/flow/native/createProcessByCategory")
    BpmResultDTO<BpmCreateResult> createProcessByCategory(@RequestBody JSONObject requestJsn, @RequestParam("dataId") String dataId, @RequestParam("businessType") String businessType);

    /**
     * 根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录
     * @param flowInstanceRecord
     * @return
     */
    @ApiOperation(value = "根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录")
    @PostMapping("/external/bpm/getLastFlowInstanceRecord")
    public FlowInstanceRecord getLastFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord);

    /**
     * 获取MDM编码
     * @param companyInfo
     * @return
     */
    @ApiOperation(value = "获取MDM编码,并更新到供应商主信息")
    @PostMapping("/mdmcompany/sendCompanyInfoToMdm")
    MdmResponseDto sendCompanyInfoToMdm(@RequestBody CompanyInfo companyInfo);

    /**
     * 获取账户组信息
     * @param creditCode
     * @return
     */
    @ApiOperation(value = "内部供应商查询接口")
    @PostMapping("/pj-anon/supplier/open/platform/getInternalSupplierData")
    Map<String, List<InternalSupplierQuery>> getInternalSupplierData(@RequestBody List<String> creditCode);

    /**
     * 通过组织id获取hr组织信息
     * @param organizationId
     * @return
     */
    @ApiOperation(value = "通过组织id获取hr组织信息")
    @GetMapping("/hrOrganization/getHrOrganizationInfoByOrganizationId")
    SccPjOrganization getHrOrganizationInfo(@RequestParam("organizationId") Long organizationId);

    /**
     * 根据用户账号获取用户信息
     * @param personnelNo
     * @return
     */
    @ApiOperation(value = "根据用户账号获取用户信息")
    @GetMapping("/pj-anon/user/getSccUserByPersonnelNo")
    SccPjUser getSccUserByPersonnelNo(@RequestParam("personnelNo") String personnelNo);

    /**
     * 发送短信
     * @param content
     * @param phones
     * @return
     */
    @ApiOperation(value = "发送短信")
    @PostMapping("/external/SMS/message")
    JSONObject message(@RequestParam String content, @RequestParam String phones);

    /**
     * 发起流程
     * @param businessId
     * @param businessType
     * @param bpmParam
     * @return
     */
    @ApiOperation(value = "发起流程")
    @PostMapping("/bpmFlow/startProcessByCategoty")
    BpmResultDTO<BpmCreateResult> startProcessByCategoty(@RequestParam("businessId") Long businessId
            ,@RequestParam("businessType") String businessType,@RequestBody BpmStartProcessParam bpmParam);

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
}
