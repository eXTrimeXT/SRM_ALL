package com.midea.cloud.srm.cm.contract.controller;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.cm.common.constant.DingTalkConstant;
import com.midea.cloud.srm.cm.common.dingtalks.DingTalkClient;
import com.midea.cloud.srm.cm.contract.service.IContractInterfceService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.contract.vo.ContractHeadVo;
import com.midea.cloud.srm.model.contract.vo.ContractPartnerVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author luxc18
 */
@RestController
@RequestMapping("contractInterface/ext/")
@Slf4j
public class ContractInterfaceController {
    @Autowired
    private IContractInterfceService contractInterfceService;
    @Autowired
    public BaseClient baseClient;
    @Resource
    private PjProjectExtClient pjProjectExtClient;
    List<String> extContractHandlerUsers = new ArrayList<>();
    List<Record> contractHeads = new ArrayList<>();
    @ApiOperation("合作伙伴对应的盖章状态改为已签署")
    @PostMapping("/updateStampState")
    public void updateStampState(@RequestParam String contractId,
                                 @RequestParam String tenantName) {
        contractInterfceService.updateStampState(contractId,tenantName);
    }

    @ApiOperation("发送钉钉通知乙方签署合同")
    @PostMapping("sendDingDing")
    public void sendDingDing(@RequestParam("contractId") Long contractId,@RequestParam("extEmployeeNumber") String extEmployeeNumber){
        DingTalkClient dingTalkClient = DingTalkClient.newInstance(baseClient, pjProjectExtClient);
        //根据订单号查询相关信息
        ContractHeadVo contractHeadVo=contractInterfceService.selectById(contractId);
        Map<String, String> var = new HashMap<>(16);
        var.put("${Contract.Name}",contractHeadVo.getContractName());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String strDate1 = sdf1.format(contractHeadVo.getCreationDate());
        var.put("${Contract.Date}", strDate1);
        var.put("${Contract.Handler}", contractHeadVo.getExtContractHandlerName());
        extContractHandlerUsers.add(extEmployeeNumber);
        dingTalkClient.sendDingTalk(extContractHandlerUsers, DingTalkConstant.BID_NOTICE_CONTRACT_SIGN, var);
    }

    @ApiOperation("根据合同ID查询甲方和乙方的合同信息记录")
    @GetMapping("/getById/{id}")
    public List<ContractPartnerVo> getById(@PathVariable("id") Long id){
        return contractInterfceService.getById(id);
    }

    /**
     * 合同归档接口
     * @param contractHeadId
     * @return
     */
    @ApiOperation("合同归档接口")
    @GetMapping("/contractFiling")
    public Long contractFiling(@RequestParam("contractHeadId") Long contractHeadId) {
        return contractInterfceService.contractFiling(contractHeadId);
    }

    /**
     * 根据合同id获取电子签章平台url
     * @param contractHeadId
     * @return
     */
    @ApiOperation("根据合同id获取电子签章平台url")
    @GetMapping("/getUrlById")
    public String getUrlById(@RequestParam("contractHeadId") Long contractHeadId,@RequestParam("extStampSignSeq") String extStampSignSeq) {
        return contractInterfceService.getUrlById(contractHeadId,extStampSignSeq);
    }

    /**
     * 电子签章确认,合同可签署状态
     * @param contractHeadId
     * @return
     */
    @ApiOperation("电子签章确认,合同可签署状态")
    @GetMapping("/confirm")
    public void confirm(@RequestParam("contractHeadId") Long contractHeadId,@RequestParam("extStampSignSeq") String extStampSignSeq) {
        contractInterfceService.confirm(contractHeadId,extStampSignSeq);
    }

}
