package com.midea.cloud.srm.feign;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.feign.workflow.FlowBusinessCallbackClient;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCallback;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouProjectInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
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
 * @author yipeng
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
@FeignClient(value = "${cloud.scc.feign-name-mapping.cloud-biz-pj:cloud-biz-pj}", path = "${cloud.scc.feign-name-mapping.cloud-biz-pj-path:/api-pj}", contextId = "pjBidInfoExt")
public interface PjProjectBidExtClient extends FlowBusinessCallbackClient {

    /**
     * 项目信息【编辑/提交】
     * @param param 参数
     * @return 返回
     */
    @ApiOperation(value = "询价立项 - 项目信息【编辑/提交】")
    @PostMapping("/buyer/comp/init/editProjectInfo")
    Long editProjectInfo(@RequestBody ApiCompSouProjectInfoDTO param);

    /**
     * 暂存/提交项目需求
     * @param param 参数
     * @return 返回
     */
    @ApiOperation(value = "暂存/提交项目需求")
    @PostMapping("/buyer/comp/init/editRequireInfo")
    List<ApiCompSouItemVO> editRequireInfo(@RequestBody ApiCompSouRequireInfoDTO param);

    /**
     * 需求池拟定生成竞价单
     * @param params 参数
     * @return 返回
     */
    @ApiOperation(value = "需求池拟定生成竞价单")
    @PostMapping("/buyer/comp/init/editSouBidInfo")
    SouProject editSouBidInfo(@RequestBody Map<String, Object> params);

    /**
     * 钉钉发送消息
     * @param content 参数
     * @param userList 参数
     * @return 返回
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
     * SRM审批流最新记录
     * @param flowInstanceRecord 参数
     * @return 返回
     */
    @ApiOperation(value = "根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录")
    @PostMapping("/external/bpm/getLastFlowInstanceRecord")
    FlowInstanceRecord getLastFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord);

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
     * callback
     * @param bpmCallback
     * @throws Exception
     */
    @ApiOperation(value = "审批流状态回调")
    @PostMapping("/external/bpm/callback")
    public void callback(@RequestBody BpmCallback bpmCallback) throws Exception;
}
