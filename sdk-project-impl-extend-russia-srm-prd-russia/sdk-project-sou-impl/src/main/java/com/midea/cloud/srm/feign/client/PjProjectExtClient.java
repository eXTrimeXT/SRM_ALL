package com.midea.cloud.srm.feign.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.aihelper.BidReviewResDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentRequestDto;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentResponseDto;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.ApiSettleAcountingRequestDto;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.ApiSettleAcountingResponseDto;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.sou.pj.SccPjHrUserInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 备注
 * @author huangbf3
 */
@FeignClient(value = "cloud-biz-pj", contextId = "pjProjectExt", path = "/api-pj")
public interface PjProjectExtClient {
    /**
     * 备注
     * @param username 参数
     * @return 返回
     */
    @GetMapping("/pj-anon/user/getHrUserOrgnizationByUsername")
    HrUserOrgnizationDto getHrUserOrgnizationByUsername(@RequestParam String username);

    /**
     * 备注
     * @param organizationCode 参数
     * @return 返回
     */
    @GetMapping("/pj-anon/user/getBuOrganizationByOuOrgCode")
    Organization getBuOrganizationByOuOrgCode(@RequestParam String organizationCode);

    /**
     * 根据用户账号获取用户信息
     * @param personnelNo 参数
     * @return 返回
     */
    @ApiOperation(value = "根据用户账号获取用户信息")
    @GetMapping("/pj-anon/user/getSccUserByPersonnelNo")
    SccPjUser getSccUserByPersonnelNo(@RequestParam("personnelNo") String personnelNo);

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
     * 根据员工工号查询长城hr的员工信息
     * @param personnelNo 参数
     * @return 返回
     */
    @ApiOperation("根据员工工号查询长城hr的员工信息")
    @GetMapping("/pj-anon/hrUser/getHrUserInfo")
    SccPjHrUserInfo getHrUserInfo(@RequestParam("personnelNo") String personnelNo);

    /**
     * 根据员工工号批量查询长城hr的员工信息
     * @param personnelNos 参数
     * @return 返回
     */
    @ApiOperation("根据员工工号批量查询长城hr的员工信息")
    @PostMapping("/pj-anon/hrUser/listHrUserInfos")
    Map<String/* 员工工号 */, SccPjHrUserInfo> listHrUserInfos(@RequestBody Set<String> personnelNos);

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

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 发票开具创建
     * @param param 发票开具创建请求参数
     * @return 发票开具创建 返回结果
     */
    @ApiOperation(value = "发票开具创建")
    @PostMapping("/pj-anon/saleSettle/createInvoice")
    JSONObject createInvoice(@RequestBody JSONObject param) ;

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 结算结果查询
     * @param param 结算结果查询请求参数
     * @return 结算结果查询 返回结果
     */
    @ApiOperation(value = "结算结果查询")
    @PostMapping("/pj-anon/saleSettle/settleResult")
    public JSONObject settleResult(@RequestBody JSONObject param);

    /**
     * 发送待办
     * @param businessId 单据ID
     * @param businessType 单据类型
     * @param processTitle 待办标题
     * @param todoUsername 待办接收账号
     * @param extUrlParm url扩展参数，非必填，例如 id=1&code=2
     * @throws Exception
     */
    @ApiOperation(value = "发送待办")
    @GetMapping("/bpmFlow/srmbpmTodo")
    public void srmbpmTodo(@RequestParam("businessId") Long businessId,@RequestParam("businessType") String businessType
            ,@RequestParam("processTitle")String processTitle,@RequestParam("todoUsername")String todoUsername, @RequestParam(value = "extUrlParam", required = false) String extUrlParm) throws Exception;


    /**
     * srmbpmHavedone
     * @param businessId
     * @param businessType
     * @param todoUsername
     * @throws Exception
     */
    @ApiOperation(value = "待办转已办")
    @GetMapping("/bpmFlow/srmbpmHavedone")
    public void srmbpmHavedone(@RequestParam("businessId") Long businessId,@RequestParam("businessType") String businessType
            ,@RequestParam("todoUsername")String todoUsername) throws Exception;


    /**
     * saveOutSourceOneVo
     * @param apiPaymentRequestDto
     * @return
     */
    @ApiOperation(value = "批量付款及自动提交审批")
    @PostMapping("/ccApi/payment/saveOutSourceOneVo")
    ApiPaymentResponseDto saveOutSourceOneVo(@RequestBody ApiPaymentRequestDto apiPaymentRequestDto);

    /**
     * accounting
     * @param requestDto
     * @return
     */
    @ApiOperation(value = "结算记账 销售结算记账接口，完成会计记账  封装版")
    @PostMapping("/pj-anon/saleSettle/accounting")
    ApiSettleAcountingResponseDto accounting(@RequestBody ApiSettleAcountingRequestDto requestDto);


    /**
     * 围串标识别结果接口
     * @param projectId 参数
     * @param compareWordSizes 参数
     * @return 返回
     */
    @ApiOperation(value = "围串标识别结果")
    @PostMapping("/external/ai/project/fileCompare")
    JSONArray getFileCompare(@RequestParam Long projectId, @RequestParam String compareWordSizes);


    /**
     * 智能评标获取评审项结果接口
     * @param projectId 参数
     * @return 返回
     */
    @ApiOperation(value = "智能评标获取评审项结果接口")
    @PostMapping("/external/ai/bidReview/itemsQuotation")
    BidReviewResDto itemsQuotation(@RequestParam Long projectId);

}
