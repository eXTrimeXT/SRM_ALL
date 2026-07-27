package com.midea.cloud.srm.biz.pj.changchengapi.sign.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISignService;
import com.midea.cloud.srm.biz.pj.common.PjInterfaceLogUtils;
import com.midea.cloud.srm.feign.pj.contract.ContractBpmClient;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.changchengapi.sign.dto.NodeCallbackRequestParamDto;
import com.midea.cloud.srm.model.pj.changchengapi.sign.vo.ContractPartnerVo;
import com.midea.cloud.srm.model.pj.sign.dto.SignCallback;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;


/**
 * @author huangbf3
 * bpm=>srm接口
 */
@Slf4j
@RestController
@RequestMapping("/external/sign")
public class SignController {
    @Autowired
    private ISignService iSignService;
    @Autowired
    ContractBpmClient contractBpmClient;
    @ApiOperation(value = "契约锁状态回调")
    @RequestMapping(value="/statusCallback",method= RequestMethod.POST)
    public void statusCallback(@RequestParam("contractId") String contractId  ,@RequestParam("status") String status) throws Exception {

        SignCallback signCallback = new SignCallback();
        signCallback.setStatus(status);
        signCallback.setContractId(contractId);
        log.info("signCallback:" + JSONObject.toJSONString(signCallback));
        iSignService.statusCallback(signCallback);
    }
    //CONTRACT_HEAD_ID
    /**
     * 合同 id contractId
     *  签署方编号： tenantId
     *  签署方名称： tenantName
     *  文件自定义编号：  sn
     *  文件状态：  status
     *  回调类型： type
     *  联系方式：contact
     *  操作人姓名：        operatorName
     *  操作人手机号：        operatorMobile
     *  操作人员工编号：        operatorNumber
     *  经办人员工编号：        receiverNumber
     * @param param
     * @throws Exception
     */
    @ApiOperation(value = "契约锁节点回调")
    @RequestMapping(value="/nodeCallback",method= RequestMethod.POST)
    public void nodeCallback(@RequestParam Map<String, Object> param) throws Exception {
        String msg = ProcessStatusEnum.COMPLETED.getName();
        try {
            NodeCallbackRequestParamDto paramDto = JSON.parseObject(JSON.toJSONString(param), NodeCallbackRequestParamDto.class);
            log.info(MessageFormat.format("nodeCallback request params: {0}", JSON.toJSONString(paramDto)));
            //更新状态为已签署
            contractBpmClient.updateStampState(Long.valueOf(paramDto.getContractId()),paramDto.getTenantName());
            //根据合同ID查询出甲方和乙方的签署情况
            List<ContractPartnerVo> list =contractBpmClient.getById(Long.valueOf(paramDto.getContractId()));
            ContractPartnerVo my = new ContractPartnerVo();
            ContractPartnerVo you = new ContractPartnerVo();
            String partnerType = "甲方";
            String stampStatus = "STAMP";
            for(int i=0;i<list.size();i++){
                if(list.get(i).getPartnerName().equals(paramDto.getTenantName())){
                    my=list.get(i);
                }
                else{
                    you=list.get(i);
                }
            }

            //如果是甲方 直接返回
            if(partnerType.equals(my.getPartnerType()))
            {
                return ;
            }
            //如果是乙方，并且甲方已经签署，返回
            if(stampStatus.equals(you.getExtStampStatus())){
                return ;
            }
            if(StringUtils.isNotBlank(you.getExtEmployeeNumber())) {
                //发送钉钉通知
                contractBpmClient.sendDingDing(Long.valueOf(paramDto.getContractId()), you.getExtEmployeeNumber());
            }
        } catch (Exception e) {
            log.error("nodeCallback Exception", e);
            msg = "接口异常" + e.getMessage();
            throw new BaseException(e.getMessage());
        } finally {
            PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.SIGN_NODE_CALLBACK, JSON.toJSONString(param), msg);
        }
    }
}