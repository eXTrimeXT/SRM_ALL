package com.midea.cloud.srm.biz.pj.contractlock.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.contractlock.ContractLockService;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.qiyuesuo.sdk.bean.company.Company;
import net.qiyuesuo.sdk.bean.company.TenantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;


/**
 * @author huangbf3
 * 契约锁相关接口controller
 * **/
@Slf4j
@RestController
@RequestMapping("/external/ContractLock")
public class ContractLockController {

    @Autowired
    private ContractLockService contractLockService;



    @ApiOperation(value = "多文件创建合同文档-创建合同-获取签署页面-接口整合")
    @PostMapping("/contractSigning")
    public String contractSigningByUrl( @RequestBody JSONObject jsonObject )  {
        return contractLockService.contractSigning(jsonObject);
    }



    @ApiOperation(value = "多文件创建合同文档接口,fileIdList为附件id,title为合成的文件名")
    @PostMapping("/createbyfiles")
    public Long createbyfiles(@RequestBody List<Long> fileIdList,@RequestParam String title )  {
        return contractLockService.createbyfiles(fileIdList,title);
    }


    @ApiOperation(value = "契约锁创建合同,documents为合成的文件的id,subject为合同名称")
    @PostMapping("/createContractByCategory")
    public Long createContractByCategory(@RequestBody List<Long> documents,@RequestParam String subject)  {
        return contractLockService.createContractByCategory(documents,subject);
    }


    @ApiOperation(value = "合同签署页面接口,contractId为创建的合同的id,tnantName为接收方签署方名称")
    @PostMapping("/signUrl")
    public String signUrl(@RequestParam Long contractId,@RequestParam String tenantName)  {
        return contractLockService.signUrl(contractId,tenantName);
    }

    @ApiOperation(value = "合同签署页面接口,contractId为创建的合同的id,tnantName为接收方签署方名称")
    @PostMapping("/signUrl2")
    public String signUrl2(@RequestParam Long contractId,@RequestParam String tenantName,String contact,
                          String receiverNumber, String tenantType)  {
        return contractLockService.signUrl(contractId,tenantName,contact,receiverNumber,TenantType.valueOf(tenantType));
    }


    @ApiOperation(value = "锲约锁认证状态查询,companyId")
    @PostMapping("/getComnpanyAuthStatus")
    public String getComnpanyAuthStatus(@RequestParam BigInteger companyId)  {
        return contractLockService.getComnpanyAuthStatus(companyId);
    }


    @ApiOperation(value = "锲约锁下载合同,前端传业务单据id")
    @PostMapping("/download")
    public List<Fileupload> download(@RequestParam Long srmOrderId)  {
        return contractLockService.download(srmOrderId);
    }


    @ApiOperation(value = "发起合同")
    @PostMapping("/send")
    void send(@RequestParam Long contractId)  {
        contractLockService.send(contractId);
    }



    @ApiOperation(value = "预签署页面接口")
    @PostMapping("/preSignUrl")
    public String preSignUrl(@RequestParam Long contractId)  {
        return contractLockService.preSignUrl(contractId);
    }

    /**
     * 合同模块用
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "契约锁创建合同,documents为合成的文件的id,subject为合同名称(合同模块用)")
    @PostMapping("/createContractByCategoryForContract")
    public Long createContractByCategoryForContract(@RequestBody JSONObject jsonObject)  {
        return contractLockService.createContractByCategoryForContract(jsonObject);
    }

    /**
     * 通知用户企业认证
     * @param  companyId
     * @return
     */
    @ApiOperation(value = "通知用户企业认证")
    @PostMapping("/sendCompanyAuthNotify")
    public Company sendCompanyAuthNotify(@RequestParam Long companyId){
        return contractLockService.sendCompanyAuthNotify(companyId);
    }


    /**
     * 印章管理者员工信息
     * /external/ContractLock/sealDetailEmployees
     * @param sealId
     * @return
     */
    @ApiOperation("印章管理者员工信息")
    @GetMapping("/sealDetailEmployees")
    public String sealDetailEmployees(@RequestParam(value = "sealId") Long sealId) {
        return contractLockService.sealDetailEmployees(sealId);
    }

}
