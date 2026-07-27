package com.midea.cloud.srm.biz.pj.contractlock;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import io.swagger.annotations.ApiOperation;
import net.qiyuesuo.sdk.bean.company.Company;
import net.qiyuesuo.sdk.bean.company.TenantType;
import net.qiyuesuo.sdk.bean.contract.CreateContractRequest;
import net.qiyuesuo.sdk.common.exception.PrivateAppException;

import java.math.BigInteger;
import java.util.List;

/**
 * @author huangbf3
 */
public interface ContractLockService {

    /**
     * 备注
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "多文件创建合同文档-创建合同-获取签署页面-接口整合")
    String contractSigning(JSONObject jsonObject);


    /**
     * 备注
     * @param fileIdList
     * @param title
     * @return
     */
    @ApiOperation(value = "多文件创建合同文档接口")
    Long createbyfiles(List<Long> fileIdList,String title);

    /**
     * 备注
     * @param documents
     * @param subject
     * @return
     */
    @ApiOperation(value = "契约锁创建合同")
    Long createContractByCategory(List<Long> documents,String subject);

    /**
     * 契约锁创建合同传整体入参
     * @param createContractRequest 参数
     * @return
     */
    @ApiOperation(value = "契约锁创建合同传整体入参")
    Long createContractByCategory2(CreateContractRequest createContractRequest);

    /**
     * 备注
     * @param contractId
     * @param tenantName
     * @return
     */
    @ApiOperation(value = "合同签署页面接口")
    String signUrl(Long contractId,String tenantName);

    @ApiOperation(value = "合同签署页面接口")
    String signUrl(Long contractId, String tenantName, String contact,
                   String receiverNumber, TenantType tenantType);


    /**
     * 备注
     * @param companyId
     * @return
     */
    @ApiOperation(value = "锲约锁认证状态查询")
    String getComnpanyAuthStatus(BigInteger companyId);

    /**
     * 备注
     * @param srmOrderId
     * @return
     */
    @ApiOperation(value = "锲约锁下载合同")
    List<Fileupload> download(Long srmOrderId);

    /**
     * 备注
     * @param srmOrderId
     * @return
     */
    @ApiOperation(value = "锲约锁下载合同")
    List<Fileupload> downloadNew(Long srmOrderId);

    /**
     * 备注
     * @param contractId
     */
    @ApiOperation(value = "发起合同")
    void  send(Long  contractId);

    /**
     * 备注
     * @param contractId
     * @return
     */
    @ApiOperation(value = "预签署页面接口")
    String  preSignUrl(Long  contractId);


    /**
     * 合同模块用
     * @param jsonObject
     * @return
     */
    Long createContractByCategoryForContract(JSONObject jsonObject);

    /**
     * 发送权限认证信息
     * @param companyId
     * @return
     */
    Company sendCompanyAuthNotify(Long companyId) ;

    /**
     * 印章管理者员工信息
     * @param sealId
     * @return
     */
    public String sealDetailEmployees(Long sealId);
}
