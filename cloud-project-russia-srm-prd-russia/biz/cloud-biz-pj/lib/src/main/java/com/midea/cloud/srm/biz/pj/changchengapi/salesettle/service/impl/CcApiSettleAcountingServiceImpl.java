package com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service.impl;

import cn.hutool.core.lang.func.LambdaUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service.CcApiSettleAcountingService;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.common.OpenClientUtils;
import com.midea.cloud.srm.biz.pj.common.PjInterfaceLogUtils;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.*;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: panmq
 * @Date: 2024/04/10/ $
 * @Description: 结算记账 销售结算记账接口，完成会计记账
 */

@Slf4j
@Service
public class CcApiSettleAcountingServiceImpl implements CcApiSettleAcountingService {

    @Autowired
    private QlService qlService;

    @Value("${gwm.url.settleAccounting}")
    private String settleAccounting;

    private static final String CONTENT_TYPE = "application/json";

    private static final Integer NUM_TWO_FIVE_SIX = 256;

    @Override
    public ApiSettleAcountingResponseDto accounting(ApiSettleAcountingRequestDto requestDto) {
        String processSerialNum = DateUtil.format(new Date(), DateUtil.DATE_FORMAT_14);
        Long processGroupId = IdGenrator.generate();

        //保存接口
        SccPjApiAcountHeadIntf headIntf = saveAccountingIntf(processSerialNum, processGroupId, requestDto);

        //请求财务共享接口
        try {
            String callApiJson = OpenClientUtils.sendHttpPost(settleAccounting, JSON.toJSONString(requestDto), CONTENT_TYPE);
            /** 保存接口日志 */
            PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.SETTLE_ACCOUNTING, JSON.toJSONString(requestDto), callApiJson);

            JSONObject resultObject = JSON.parseObject(callApiJson);
            ApiSettleAcountingResponseDto responseDto = new ApiSettleAcountingResponseDto();
            if(!OpenClientConstant.CODE_SUCCESS.equals(Objects.toString(resultObject.get(LambdaUtil.getFieldName(ApiSettleAcountingResponseDto::getCode))))) {
                responseDto.setCode(Objects.toString(resultObject.get(LambdaUtil.getFieldName(ApiSettleAcountingResponseDto::getCode))));
                responseDto.setMsg(callApiJson);
                updateIntf(processSerialNum, processGroupId, ProcessStatusEnum.ERROR.getCode(), callApiJson);
            } else {
                responseDto = JSON.parseObject(callApiJson, ApiSettleAcountingResponseDto.class);
                saveAccountingResponse(headIntf, responseDto);
                updateIntf(processSerialNum, processGroupId, ProcessStatusEnum.COMPLETED.getCode(), callApiJson);
            }

            return responseDto;
        } catch (Exception e) {
            log.error("accounting Exception", e);
            updateIntf(processSerialNum, processGroupId, ProcessStatusEnum.ERROR.getCode(), "接口异常："+e.getMessage());
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 保存结果
     * @param headIntf
     * @param responseDto
     */
    private void saveAccountingResponse(SccPjApiAcountHeadIntf headIntf, ApiSettleAcountingResponseDto responseDto) {
        if(Objects.isNull(responseDto.getData())) {
            return;
        }
        List<SccPjApiAcountRespIntf> respIntfList = new ArrayList<>(16);
        if(CollectionUtils.isNotEmpty(responseDto.getData().getVoucherList())) {
            responseDto.getData().getVoucherList().stream().forEach(voucher -> {
                SccPjApiAcountRespIntf respIntf = new SccPjApiAcountRespIntf();
                BeanCopyUtil.copyProperties(respIntf, voucher);
                respIntf.setSystemCode(responseDto.getData().getSystemCode());
                respIntf.setBusinessNo(responseDto.getData().getBusinessNo());
                respIntf.setReqSn(responseDto.getData().getReqSn());
                respIntf.setSettleDocumentCode(responseDto.getData().getSettleDocumentCode());

                respIntf.setRespIntfId(IdGenrator.generate());
                respIntf.setAcountingIntfId(headIntf.getAcountingIntfId());

                respIntf.setProcessDate(new Date());
                respIntf.setProcessGroupId(headIntf.getProcessGroupId());
                respIntf.setProcessSerialNum(headIntf.getProcessSerialNum());
                respIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

                respIntfList.add(respIntf);
            });
        } else {
            SccPjApiAcountRespIntf respIntf = new SccPjApiAcountRespIntf();
            BeanCopyUtil.copyProperties(respIntf, responseDto.getData());
            respIntf.setRespIntfId(IdGenrator.generate());
            respIntf.setAcountingIntfId(headIntf.getAcountingIntfId());

            respIntf.setProcessDate(new Date());
            respIntf.setProcessGroupId(headIntf.getProcessGroupId());
            respIntf.setProcessSerialNum(headIntf.getProcessSerialNum());
            respIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

            respIntfList.add(respIntf);
        }

        if(CollectionUtils.isNotEmpty(respIntfList)) {
            qlService.create(MqlType.SCC_PJ_API_ACOUNT_RESP_INTF, respIntfList);
        }
    }

    /**
     * 保存接口表
     * @param processSerialNum
     * @param processGroupId
     * @param requestDto
     * @return
     */
    private SccPjApiAcountHeadIntf saveAccountingIntf(String processSerialNum, Long processGroupId, ApiSettleAcountingRequestDto requestDto) {

        SccPjApiAcountHeadIntf headIntf = saveAccountingIntfHead(processSerialNum, processGroupId, requestDto.getHeader());

        saveAccountingIntfItems(headIntf, requestDto.getItems());

        return headIntf;
    }

    private void updateIntf(String processSerialNum, Long processGroupId, String status, String msg) {
        if(StringUtils.isNotBlank(msg) && Integer.compare(msg.length(), NUM_TWO_FIVE_SIX) == 1) {
            msg = msg.substring(0, 250);
        }

        qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_PJ_API_ACOUNT_HEAD_INTF)
                .set(SccPjApiAcountHeadIntf::getProcessStatus, status)
                .set(SccPjApiAcountHeadIntf::getProcessMessage, msg)
                .set(SccPjApiAcountHeadIntf::getProcessDate, new Date())
                .eq(SccPjApiAcountHeadIntf::getProcessSerialNum, processSerialNum)
                .eq(SccPjApiAcountHeadIntf::getProcessGroupId, processGroupId));

    }

    /**
     * 保存明细
     * @param headIntf
     * @param items
     */
    private void saveAccountingIntfItems(SccPjApiAcountHeadIntf headIntf, List<ApiSettleAcountingRequestItems> items) {

        if(CollectionUtils.isEmpty(items)) {
            return;
        }

        //基本信息
        List<SccPjApiAcountBaseIntf> baseIntfList = new ArrayList<>(16);
        //结算信息
        List<SccPjApiAcountSettleIntf> settleIntfList = new ArrayList<>(16);
        //结算基本信息
        List<SccPjApiAcountSetinfoIntf> infoIntfList = new ArrayList<>(16);
        //成本结转
        List<SccPjApiAcountCostIntf> costIntfList = new ArrayList<>(16);
        //附件信息
        List<SccPjApiAcountAttachIntf> attachIntfList = new ArrayList<>(16);

        items.stream().forEach(item -> {
            saveAccountingIntfItemsBase(headIntf, item.getBaseInfo(), baseIntfList);
            saveAccountingIntfItemsSettle(headIntf, item.getSettleDetailList(), settleIntfList, infoIntfList);
            saveAccountingIntfItemsCost(headIntf, item.getCostInfoList(), costIntfList);
            saveAccountingIntfItemsAttach(headIntf, item.getAttachList(), attachIntfList);
        });

        if(CollectionUtils.isNotEmpty(baseIntfList)) {
            qlService.create(MqlType.SCC_PJ_API_ACOUNT_BASE_INTF, baseIntfList);
        }

        if(CollectionUtils.isNotEmpty(settleIntfList)) {
            qlService.create(MqlType.SCC_PJ_API_ACOUNT_SETTLE_INTF, settleIntfList);
        }

        if(CollectionUtils.isNotEmpty(infoIntfList)) {
            qlService.create(MqlType.SCC_PJ_API_ACOUNT_SETINFO_INTF, infoIntfList);
        }

        if(CollectionUtils.isNotEmpty(costIntfList)) {
            qlService.create(MqlType.SCC_PJ_API_ACOUNT_COST_INTF, costIntfList);
        }

        if(CollectionUtils.isNotEmpty(attachIntfList)) {
            qlService.create(MqlType.SCC_PJ_API_ACOUNT_ATTACH_INTF, attachIntfList);
        }

    }

    /**
     * 保存附件
     * @param headIntf
     * @param attachList
     * @param attachIntfList
     */
    private void saveAccountingIntfItemsAttach(SccPjApiAcountHeadIntf headIntf, List<ApiSettleAcountingRequestItemsAttach> attachList, List<SccPjApiAcountAttachIntf> attachIntfList){
        if(CollectionUtils.isEmpty(attachList)) {
            return;
        }

        attachList.stream().forEach(attach -> {
            SccPjApiAcountAttachIntf attachIntf = new SccPjApiAcountAttachIntf();
            BeanCopyUtil.copyProperties(attachIntf, attach);

            attachIntf.setAttachIntfId(IdGenrator.generate());
            attachIntf.setAcountingIntfId(headIntf.getAcountingIntfId());

            attachIntf.setProcessDate(new Date());
            attachIntf.setProcessGroupId(headIntf.getProcessGroupId());
            attachIntf.setProcessSerialNum(headIntf.getProcessSerialNum());
            attachIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

            attachIntfList.add(attachIntf);

        });

    }

    /**
     * 保存成本
     * @param headIntf
     * @param costInfoList
     * @param costIntfList
     */
    private void saveAccountingIntfItemsCost(SccPjApiAcountHeadIntf headIntf, List<ApiSettleAcountingRequestItemsCost> costInfoList, List<SccPjApiAcountCostIntf> costIntfList){
        if(CollectionUtils.isEmpty(costInfoList)) {
            return;
        }

        costInfoList.stream().forEach(cost -> {
            SccPjApiAcountCostIntf costIntf = new SccPjApiAcountCostIntf();
            BeanCopyUtil.copyProperties(costIntf, cost);

            costIntf.setCostIntfId(IdGenrator.generate());
            costIntf.setAcountingIntfId(headIntf.getAcountingIntfId());

            costIntf.setProcessDate(new Date());
            costIntf.setProcessGroupId(headIntf.getProcessGroupId());
            costIntf.setProcessSerialNum(headIntf.getProcessSerialNum());
            costIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

            costIntfList.add(costIntf);

        });

    }

    /**
     * 保存结算信息
     * @param headIntf
     * @param settleDetailList
     * @param settleIntfList
     * @param infoIntfList
     */
    private void saveAccountingIntfItemsSettle(SccPjApiAcountHeadIntf headIntf, List<ApiSettleAcountingRequestItemsSettle> settleDetailList, List<SccPjApiAcountSettleIntf> settleIntfList, List<SccPjApiAcountSetinfoIntf> infoIntfList){
        if(CollectionUtils.isEmpty(settleDetailList)) {
            return;
        }

        settleDetailList.stream().forEach(settle -> {
            SccPjApiAcountSettleIntf settleIntf = new SccPjApiAcountSettleIntf();
            BeanCopyUtil.copyProperties(settleIntf, settle);

            settleIntf.setSettleIntfId(IdGenrator.generate());
            settleIntf.setAcountingIntfId(headIntf.getAcountingIntfId());

            settleIntf.setProcessDate(new Date());
            settleIntf.setProcessGroupId(headIntf.getProcessGroupId());
            settleIntf.setProcessSerialNum(headIntf.getProcessSerialNum());
            settleIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

            settleIntfList.add(settleIntf);

            saveAccountingIntfItemsSettleInfo(headIntf, settleIntf, settle.getSettleInfoList(), infoIntfList);
        });

    }

    /**
     * 保存结算基本信息
     * @param headIntf
     * @param settleIntf
     * @param settleInfoList
     * @param infoIntfList
     */
    private void saveAccountingIntfItemsSettleInfo(SccPjApiAcountHeadIntf headIntf, SccPjApiAcountSettleIntf settleIntf, List<ApiSettleAcountingRequestItemsSettleInfo> settleInfoList,  List<SccPjApiAcountSetinfoIntf> infoIntfList) {
        if(CollectionUtils.isEmpty(settleInfoList)) {
            return;
        }

        settleInfoList.stream().forEach(settleInfoData -> {
            SccPjApiAcountSetinfoIntf setinfoIntf = new SccPjApiAcountSetinfoIntf();
            BeanCopyUtil.copyProperties(setinfoIntf, settleInfoData);
            setinfoIntf.setDiscountFlag(Objects.toString(settleInfoData.getDiscountFlag(), ""));

            setinfoIntf.setSettleInfoIntfId(IdGenrator.generate());
            setinfoIntf.setAcountingIntfId(headIntf.getAcountingIntfId());
            setinfoIntf.setSettleIntfId(settleIntf.getSettleIntfId());

            infoIntfList.add(setinfoIntf);
        });
    }


    /**
     * 保存基本信息
     * @param headIntf
     * @param baseInfo
     * @param baseIntfList
     */
    private void saveAccountingIntfItemsBase(SccPjApiAcountHeadIntf headIntf, ApiSettleAcountingRequestItemsBase baseInfo, List<SccPjApiAcountBaseIntf> baseIntfList) {
        if(Objects.isNull(baseInfo)) {
            return;
        }
        SccPjApiAcountBaseIntf baseIntf = new SccPjApiAcountBaseIntf();
        BeanCopyUtil.copyProperties(baseIntf, baseInfo);
        baseIntf.setContainTax(Objects.toString(baseInfo.getContainTax(), ""));
        baseIntf.setItemNo(Objects.toString(baseInfo.getItemNo(), ""));

        baseIntf.setBaseIntfId(IdGenrator.generate());
        baseIntf.setAcountingIntfId(headIntf.getAcountingIntfId());

        baseIntf.setProcessDate(new Date());
        baseIntf.setProcessGroupId(headIntf.getProcessGroupId());
        baseIntf.setProcessSerialNum(headIntf.getProcessSerialNum());
        baseIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

        baseIntfList.add(baseIntf);
    }

    /**
     * 保存请求头
     * @param processSerialNum
     * @param processGroupId
     * @param header
     * @return
     */
    private SccPjApiAcountHeadIntf saveAccountingIntfHead(String processSerialNum, Long processGroupId, ApiSettleAcountingRequestHead header) {
        SccPjApiAcountHeadIntf headIntf = new SccPjApiAcountHeadIntf();
        BeanCopyUtil.copyProperties(headIntf, header);
        headIntf.setAcountingIntfId(IdGenrator.generate());
        headIntf.setNeedApprove(Objects.toString(header.getNeedApprove(), ""));

        headIntf.setProcessDate(new Date());
        headIntf.setProcessGroupId(processGroupId);
        headIntf.setProcessSerialNum(processSerialNum);
        headIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

        qlService.create(MqlType.SCC_PJ_API_ACOUNT_HEAD_INTF, Collections.singletonList(headIntf));
        return headIntf;
    }
}
