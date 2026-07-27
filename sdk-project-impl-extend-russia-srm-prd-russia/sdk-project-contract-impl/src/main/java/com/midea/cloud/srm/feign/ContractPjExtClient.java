package com.midea.cloud.srm.feign;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 100014336 ganyh19
 */
@FeignClient(
        value = "${cloud.scc.feign-name-mapping.cloud-biz-pj:cloud-biz-pj}", path = "${cloud.scc.feign-name-mapping.cloud-biz-pj-path:/api-pj}",
        contextId = "cloud-biz-pj-contract-ext"
)
public interface ContractPjExtClient {

    /**
     * 根据组织ID获取组织
     * @param organizationId
     * @return
     */
    @ApiOperation(value = "根据组织ID获取组织")
    @GetMapping("/organization/organization/getOrganization")
    OrganizationEditDto findList(@RequestParam("organizationId") Long organizationId);

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
     * 根据用户账号获取用户信息
     * @param personnelNo
     * @return
     **/
    @ApiOperation(value = "根据用户账号获取用户信息")
    @GetMapping("/pj-anon/user/getSccUserByPersonnelNo")
    SccPjUser getSccUserByPersonnelNo(@RequestParam("personnelNo") String personnelNo);

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
     * 获取所属单位信息
     * @param username
     * @return
     */
    @GetMapping("/pj-anon/user/getHrUserOrgnizationByUsername")
    HrUserOrgnizationDto getHrUserOrgnizationByUsername(@RequestParam String username);

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
     * 根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录
     * @param flowInstanceRecord 参数
     * @return 返回
     */
    @ApiOperation(value = "根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录")
    @PostMapping("/external/bpm/getLastFlowInstanceRecord")
    FlowInstanceRecord getLastFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord);

    /**
     * 获取BPM新审批流标志
     * @param bpmNewFlag 参数
     * @return 返回
     */
    @ApiOperation(value = "获取BPM新审批流标志")
    @PostMapping("/bpmFlow/bpmNewFlag/get")
    BpmNewFlag getBpmNewFlag(@RequestBody BpmNewFlag bpmNewFlag);
    /**
     * 保存BPM新审批流标志
     * @param bpmNewFlag 参数
     * @return 返回
     */
    @ApiOperation(value = "获取BPM新审批流标志")
    @PostMapping("/bpmFlow/bpmNewFlag/saveOrUpdate")
    void saveOrUpdateBpmNewFlag(@RequestBody BpmNewFlag bpmNewFlag);
}
