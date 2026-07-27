package com.midea.cloud.srm.feign;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.aihelper.BidReviewResDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author xiaym13
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/17 11:36:28
 *  修改内容:
 * </pre>
 */
@FeignClient(value = "${cloud.scc.feign-name-mapping.cloud-biz-pj:cloud-biz-pj}", path = "${cloud.scc.feign-name-mapping.cloud-biz-pj-path:/api-pj}", contextId = "pjSouClient")
public interface PjSouClient extends FlowBusinessCallbackClient {
    /**
     * 发送短信
     * @param content 参数
     * @param phones 参数
     * @return 返回
     */
    @ApiOperation(value = "发送短信")
    @PostMapping("/external/SMS/message")
    JSONObject message(@RequestParam String content, @RequestParam String phones);
    /**
     * 多文件创建合同文档-创建合同-获取签署页面-接口整合
     * @param jsonObject 参数
     * @return 返回
     */
    @ApiOperation("多文件创建合同文档-创建合同-获取签署页面-接口整合")
    @PostMapping("/external/ContractLock/contractSigning")
    public String contractSigningByUrl(@RequestBody JSONObject jsonObject);



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
     * @param flowInstanceRecord 参数
     * @return 返回
     */
    @ApiOperation(value = "根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录")
    @PostMapping("/external/bpm/getLastFlowInstanceRecord")
    public FlowInstanceRecord getLastFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord);

    /**
     * 寻源公示模板详情查询
     * @param pubconfigId 参数
     * @return 返回
     */
    @ApiOperation(value = "寻源公示模板详情查询")
    @GetMapping("/source/pubconfig/queryPubconfig")
    SccPjSourcePubconfig queryPubconfig(@RequestParam("pubconfigId") Long pubconfigId);

    /**
     * 根据用户账号获取上级领导
     * @param username 参数
     * @return 返回
     */
    @ApiOperation("根据用户账号获取上级领导")
    @GetMapping("/organizationRole/getParentUserByUsername")
    public SccPjOrganizationRoleUser getParentUserByUsername(@RequestParam("username") String username);

    /**
     * 筛查请求
     * @param companyId 参数
     * @return 返回
     */
    @ApiOperation(value = "筛查请求")
    @PostMapping("/external/authentication/importScreening")
    String importScreening(@RequestParam Long companyId);

    /**
     * 通过供应商名称去爱企查查询数据
     * @param companyNames 参数
     * @return 返回
     */
    @ApiOperation(value = "通过供应商名称去爱企查查询数据")
    @PostMapping("/external/companyBigData/findAQCByNames")
    public List<CompanyAQCApiDTO> findAqcByNames(@RequestBody List<String> companyNames);

    /**
     * 通过组织id获取hr组织信息
     * @param organizationId 参数
     * @return 返回
     */
    @ApiOperation(value = "通过组织id获取hr组织信息")
    @GetMapping("/hrOrganization/getHrOrganizationInfoByOrganizationId")
    SccPjOrganization getHrOrganizationInfo(@RequestParam("organizationId") Long organizationId);

    /**
     * 根据组织ID获取组织
     * @param organizationId 参数
     * @return 返回
     */
    @ApiOperation(value = "根据组织ID获取组织")
    @GetMapping("/organization/organization/getOrganizationById")
    Organization getOrganizationById(@RequestParam("organizationId") Long organizationId);

    /**
     * 根据是否有开票信息获取组织
     * @return 返回
     */
    @ApiOperation(value = "根据是否有开票信息获取组织")
    @GetMapping("/organization/organization/findListFilterInvoiceInfo")
    List<Organization> findListFilterInvoiceInfo();

    /**
     * 根据用户账号获取用户信息
     * @param personnelNo 参数
     * @return 返回
     */
    @ApiOperation(value = "根据用户账号获取用户信息")
    @GetMapping("/pj-anon/user/getSccUserByPersonnelNo")
    SccPjUser getSccUserByPersonnelNo(@RequestParam("personnelNo") String personnelNo);

    /**
     * 范围SRM嵌套页面回调地址
     * @param funName 参数
     * @param formId 参数
     * @param formNo 参数
     * @return 返回
     */
    @ApiOperation(value = "范围SRM嵌套页面回调地址")
    @GetMapping("/external/bpm/getViewSrmRollBackUrl")
    public String getViewSrmRollBackUrl(@RequestParam("funName") String funName,@RequestParam("formId") Long formId
            ,@RequestParam("formNo") String formNo);

    /**
     * 定标审批推送中标范围
     * @param zbfw
     * @param zbfwcode
     * @param caNo
     * @return
     */
    @ApiOperation(value = "定标审批推送中标范围")
    @RequestMapping("/organizationRole/push_ZBFW_Bpm")
    void pushZbfwToBpm(@RequestParam("zbfw") String zbfw, @RequestParam("zbfwcode") String zbfwcode,  @RequestParam("caNo") String caNo);

    /**
     * getHrUserOrgnizationByUsername
     * @param username
     * @return
     **/
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

    /**
     * workNotices
     * @param content
     * @param userList
     * @return
     */
    @ApiOperation(value = "钉钉发送消息")
    @PostMapping("/external/dingding/workNotices")
    JSONObject workNotices(@RequestParam String content , @RequestBody List<String> userList);

    /**
     * typeStr
     * @param financeParam 参数
     * @param typeStr 类型
     * @return 字符串
     */
    @ApiOperation(value = "c")
    @PostMapping("/finance/coin/sendFinance")
    String sendFinance(@RequestParam("financeParam") String financeParam, @RequestParam("typeStr") String typeStr);

    /**
     * 智能评标获取评审项结果接口
     * @param projectId 参数
     * @return BidReviewResDto
     */
    @ApiOperation(value = "智能评标获取评审项结果接口")
    @PostMapping("/external/ai/bidReview/itemsQuotation")
    BidReviewResDto itemsQuotation(@RequestParam Long projectId) ;

    /**
     * 智能评标获取评审项结果接口
     * @param projectId 参数
     * @return List<Long>
     */
    @ApiOperation(value = "智能评标数据扫描件接口")
    @PostMapping("/external/ai/bidReview/scanFileList")
    List<Long> scanFileList(@RequestParam Long projectId);

    /**
     * 智能评标获取评审项结果接口
     * @param fileId 参数
     * @param fileName 参数
     * @return Fileupload
     */
    @ApiOperation(value = "智能化文件word转pdf上传")
    @PostMapping("/files-anon/file/wordTransPdf")
    Fileupload wordTransPdf(@RequestParam Long fileId, @RequestParam String fileName);
}
