package com.midea.cloud.srm.feign;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.contract.dto.CreateContractReturnDTO;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.pj.sup.company.entity.PurveyorRootDTO;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.InternalSupplierQuery;
import com.midea.cloud.srm.model.sup.black.dto.BlackCompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
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
 *  修改日期: 2023/11/27 17:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "cloud-biz-pj", contextId = "pjProjectExt", path = "/api-pj")
public interface PjProjectExtClient extends FlowBusinessCallbackClient {

    /**
     * 二开-长城开放平台_创建已签署合同接口
     * @param requestJson
     * @return
     */
    @PostMapping("/external/contract/createContract")
    @ApiOperation(value = "长城开放平台_创建已签署合同接口")
    CreateContractReturnDTO createContract(@RequestBody JSONObject requestJson);

    /**
     * 多文件创建合同文档接口,fileIdList为附件id,title为合成的文件名
     * @param fileIdList
     * @param title
     * @return
     */
    @ApiOperation(value = "多文件创建合同文档接口,fileIdList为附件id,title为合成的文件名")
    @PostMapping("/external/ContractLock/createbyfiles")
    Long createbyfiles(@RequestBody List<Long> fileIdList,@RequestParam String title );

    /**
     * 契约锁创建合同,documents为合成的文件的id,subject为合同名称
     * @param documents
     * @param subject
     * @return
     */
    @ApiOperation(value = "契约锁创建合同,documents为合成的文件的id,subject为合同名称")
    @PostMapping("/external/ContractLock/createContractByCategory")
    Long createContractByCategory(@RequestBody List<Long> documents,@RequestParam String subject);

    /**
     * 预签署页面接口
     * @param contractId
     * @return
     */
    @ApiOperation(value = "预签署页面接口")
    @PostMapping("/external/ContractLock/preSignUrl")
    String preSignUrl(@RequestParam Long contractId);


    /**
     * 发起合同
     * @param contractId
     */
    @ApiOperation(value = "发起合同")
    @PostMapping("/external/ContractLock/send")
    void send(@RequestParam Long contractId);

    /**
     * 根据合同id创建合同
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "契约锁创建合同,documents为合成的文件的id,subject为合同名称(合同模块用)")
    @PostMapping("/external/ContractLock/createContractByCategoryForContract")
    Long createContractByCategoryForContract(@RequestBody JSONObject jsonObject);

    /**
     * 保存BPM新审批流标志
     * @param bpmNewFlag 参数
     * @return 返回
     */
    @ApiOperation(value = "获取BPM新审批流标志")
    @PostMapping("/bpmFlow/bpmNewFlag/saveOrUpdate")
    void saveOrUpdateBpmNewFlag(@RequestBody BpmNewFlag bpmNewFlag);

    /**
     * 印章管理者员工信息
     * /external/ContractLock/sealDetailEmployees
     * @param sealId
     * @return
     */
    @ApiOperation("印章管理者员工信息")
    @GetMapping("/external/ContractLock/sealDetailEmployees")
    public String sealDetailEmployees(@RequestParam(value = "sealId") Long sealId);


    /**
     * 根据社会信用代码查询mdm供应商信息
     * @param taxCode 社会信用代码
     *  region默认CHN
     * @return
     */
    @ApiOperation(value = "根据社会信用代码查询mdm供应商信息")
    @PostMapping("/external/supplier/searchListByTaxCodes")
    PurveyorRootDTO searchListByTaxCodes(@RequestBody List<String> taxCode, @RequestParam("region") String region);
}
