package com.midea.cloud.srm.biz.pj.ccapipayments.service.impl;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.biz.pj.ccapipayments.service.CcApiPaymentService;
import com.midea.cloud.srm.biz.pj.common.OpenClientUtils;
import com.midea.cloud.srm.biz.pj.common.PjInterfaceLogUtils;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentRequestDto;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentResponseDto;
import com.midea.cloud.srm.model.pj.ccapipayments.entity.SccPjApiPaymentHeadIntf;
import com.midea.cloud.srm.model.pj.ccapipayments.entity.SccPjApiPaymentLineIntf;
import com.midea.cloud.srm.model.pj.ccapipayments.entity.SccPjApiPaymentResultIntf;
import com.midea.cloud.srm.model.pj.ccapipayments.enums.ApiResponseStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 长城财务接口实现类
 */
@Slf4j
@Service
public class CcApiPaymentServiceImpl implements CcApiPaymentService {

    @ApiModelProperty("供应商同步到SAP")
    @Value("${gwm.url.paymentSaveOutSourceOneUrl}")
    private String paymentSaveOutSourceOneUrl;

    @Autowired
    private QlService qlService;

    private static final String CONTENT_TYPE = "application/json";

    private static final Integer NUM_TWO_FIVE_FIVE = 255;

    @Override
    public ApiPaymentResponseDto saveOutSourceOneVo(ApiPaymentRequestDto apiPaymentRequestDto) {

        String processSerialNum = DateUtil.format(new Date(), DateUtil.DATE_FORMAT_14);
        Long processGroupId = IdGenrator.generate();

        SccPjApiPaymentHeadIntf headIntf = saveIntf(apiPaymentRequestDto, processSerialNum, processGroupId);

        String callApiJson = OpenClientUtils.sendHttpPost(paymentSaveOutSourceOneUrl, JSON.toJSONString(apiPaymentRequestDto), CONTENT_TYPE);

        /** 保存接口日志 */
        PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.SAVE_OUT_SOURCE_ONE_VO, JSON.toJSONString(apiPaymentRequestDto), callApiJson);

        ApiPaymentResponseDto apiPaymentResponseDto = JSON.parseObject(callApiJson, ApiPaymentResponseDto.class);

        try {
            saveResult(headIntf, apiPaymentResponseDto, processSerialNum, processGroupId);
            if(!ApiResponseStatusEnum.SUCCESS.name().equals(apiPaymentResponseDto.getStatus())) {
                updateIntf(processSerialNum, processGroupId, ProcessStatusEnum.ERROR.getCode(), MessageFormat.format("接口返回状态码失败：{0}" , callApiJson));
            } else {
                updateIntf(processSerialNum, processGroupId, ProcessStatusEnum.COMPLETED.getCode(), callApiJson);
            }
        } catch (Exception e) {
            log.error("saveResult Exception", e);
            updateIntf(processSerialNum, processGroupId, ProcessStatusEnum.ERROR.getCode(), "保存结果异常");
        }

        return apiPaymentResponseDto;
    }

    /**
     * 更新状态
     * @param processSerialNum
     * @param processGroupId
     * @param status
     * @param msg
     */
    private void updateIntf(String processSerialNum, Long processGroupId, String status, String msg) {

        if(StringUtils.isNotBlank(msg) && Integer.compare(msg.length(), NUM_TWO_FIVE_FIVE) == 1) {
            msg = msg.substring(0, 250);
        }

        qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_PJ_API_PAYMENT_HEAD_INTF).set(SccPjApiPaymentHeadIntf::getProcessStatus, status)
                .set(SccPjApiPaymentHeadIntf::getProcessMessage, msg)
                .set(SccPjApiPaymentHeadIntf::getProcessDate, new Date()).eq(SccPjApiPaymentHeadIntf::getProcessSerialNum, processSerialNum).eq(SccPjApiPaymentHeadIntf::getProcessGroupId, processGroupId));
    }

    private void saveResult(SccPjApiPaymentHeadIntf headIntf, ApiPaymentResponseDto responseDto, String processSerialNum, Long processGroupId) {

        List<SccPjApiPaymentResultIntf> resultIntfList = new ArrayList<>(16);
        if(CollectionUtils.isNotEmpty(responseDto.getData()) && CollectionUtils.isNotEmpty(responseDto.getData().stream().filter(d -> !Objects.isNull(d)).collect(Collectors.toList()))) {
            responseDto.getData().stream().filter(d -> !Objects.isNull(d)).forEach(data -> {
                SccPjApiPaymentResultIntf resultIntf = new SccPjApiPaymentResultIntf();
                BeanCopyUtil.copyProperties(resultIntf, responseDto);
                resultIntf.setPaymentResultIntfId(IdGenrator.generate());
                resultIntf.setPaymentIntfId(headIntf.getPaymentIntfId());
                resultIntf.setOrderNo(data.getOrderNo());
                resultIntf.setItemNum(data.getItemNum());
                resultIntf.setRequestItemId(data.getRequestItemId());
                resultIntf.setProcessSerialNum(processSerialNum);
                resultIntf.setProcessGroupId(processGroupId);
                resultIntf.setProcessDate(new Date());
                resultIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
                resultIntfList.add(resultIntf);
            });
        } else {
            SccPjApiPaymentResultIntf resultIntf = new SccPjApiPaymentResultIntf();
            BeanCopyUtil.copyProperties(resultIntf, responseDto);
            resultIntf.setPaymentResultIntfId(IdGenrator.generate());
            resultIntf.setPaymentIntfId(headIntf.getPaymentIntfId());
            resultIntf.setProcessSerialNum(processSerialNum);
            resultIntf.setProcessGroupId(processGroupId);
            resultIntf.setProcessDate(new Date());
            resultIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
            resultIntfList.add(resultIntf);
        }

        qlService.create(MqlType.SCC_PJ_API_PAYMENT_RESULT_INTF, resultIntfList);
    }

    private SccPjApiPaymentHeadIntf saveIntf(ApiPaymentRequestDto apiPaymentRequestDto, String processSerialNum, Long processGroupId) {
        SccPjApiPaymentHeadIntf headIntf = new SccPjApiPaymentHeadIntf();
        BeanCopyUtil.copyProperties(headIntf, apiPaymentRequestDto.getPaymentRequestHead());
        if(!Objects.isNull((apiPaymentRequestDto.getPaymentRequestHead().getFileFlag())) && apiPaymentRequestDto.getPaymentRequestHead().getFileFlag()) {
            headIntf.setFileFlag(YesOrNo.YES.getValue());
        } else {
            headIntf.setFileFlag(YesOrNo.NO.getValue());
        }
        headIntf.setPaymentIntfId(IdGenrator.generate());
        headIntf.setProcessSerialNum(processSerialNum);
        headIntf.setProcessGroupId(processGroupId);
        headIntf.setProcessDate(new Date());
        headIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

        List<SccPjApiPaymentLineIntf> lineIntfList = new ArrayList<>(16);
        apiPaymentRequestDto.getBatchImportPaymentRequestItems().stream().forEach(item -> {
            SccPjApiPaymentLineIntf lineIntf = new SccPjApiPaymentLineIntf();
            BeanCopyUtil.copyProperties(lineIntf, item);
            lineIntf.setPaymentLineIntfId(IdGenrator.generate());
            lineIntf.setPaymentIntfId(headIntf.getPaymentIntfId());
            lineIntf.setProcessSerialNum(processSerialNum);
            lineIntf.setProcessGroupId(processGroupId);
            lineIntf.setProcessDate(new Date());
            lineIntf.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
            lineIntfList.add(lineIntf);
        });

        qlService.create(MqlType.SCC_PJ_API_PAYMENT_HEAD_INTF, Collections.singletonList(headIntf));

        qlService.create(MqlType.SCC_PJ_API_PAYMENT_LINE_INTF, lineIntfList);

        return headIntf;
    }
}
