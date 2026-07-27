package com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service.impl;

import cn.hutool.core.lang.func.LambdaUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service.ISaleSettleService;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.ccapiinvoices.dto.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author huangbf3
 * 发票开具接口实现类
 */
@Slf4j
@Service
public class SaleSettleServiceImpl implements ISaleSettleService {

    @ApiModelProperty("发票开具创建")
    @Value("${gwm.url.createInvoiceUrl}")
    private String createInvoiceUrl;

    @ApiModelProperty("结算结果查询")
    @Value("${gwm.url.settleResultUrl}")
    private String settleResultUrl;

    @Value("${gwm.appkey}")
    private String appkey;

    @Value("${gwm.secret}")
    private String secret;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @Autowired
    private IInterfaceLogService interfaceLogService;

    @Autowired
    private QlService qlService;

    private static final int NUM_TWO_FIVE_FIVE = 255;

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 发票开具创建
     * @param param 发票开具创建请求参数
     * @return 发票开具创建 返回结果
     */
    @Override
    public JSONObject createInvoice(JSONObject param) {
        //param.getJSONObject("header").put("systemCode",srcSystem);
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(ApiInfoEnum.CREATE_INVOICE,param);

        OpenClient openClient = new OpenClient(appkey,secret);

        String result = openClient.sendHttpPost(createInvoiceUrl,param.toJSONString(),"application/json");

        interfaceLog.setReturnInfo(result);
        interfaceLogService.createInterfaceLog(interfaceLog);

        return JSONObject.parseObject(result);
    }

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 结算结果查询
     * @param param 结算结果查询请求参数
     * @return 结算结果查询 返回结果
     */
    @Override
    public JSONObject settleResult(JSONObject param) {
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(ApiInfoEnum.SETTLE_RESULT,param);

        OpenClient openClient = new OpenClient(appkey,secret);

        String result = openClient.sendHttpPost(settleResultUrl,param.toJSONString(),"application/json");

        interfaceLog.setReturnInfo(result);
        interfaceLogService.createInterfaceLog(interfaceLog);
        return JSONObject.parseObject(result);
    }

    @Override
    public CcApiInvoiceCreateResponseDto createInvoiceSimple(CcApiInvoiceCreateRequestDto requestDto) {
        String processSerialNum = DateUtil.format(new Date(), DateUtil.DATE_FORMAT_14);
        Long processGroupId = IdGenrator.generate();

        saveCreateInvoiceIntf(processSerialNum, processGroupId, requestDto);
        Gson gson = new Gson();
        JSONObject jsonObject = JSON.parseObject(gson.toJson(requestDto));

        JSONObject resultObject = createInvoice(jsonObject);

        updateInfo(processSerialNum, processGroupId, gson.toJson(resultObject));

        CcApiInvoiceCreateResponseDto responseDto = new CcApiInvoiceCreateResponseDto();

        if(OpenClientConstant.CODE_SUCCESS.equals(Objects.toString(resultObject.get(LambdaUtil.getFieldName(CcApiInvoiceCreateResponseDto::getCode)), ""))) {
           responseDto = JSON.parseObject(gson.toJson(resultObject), CcApiInvoiceCreateResponseDto.class);
        } else {
            responseDto.setCode(resultObject.getInteger(LambdaUtil.getFieldName(CcApiInvoiceCreateResponseDto::getCode)));
            responseDto.setMsg(JSON.toJSONString(resultObject));
        }

        return responseDto;
    }

    private void updateInfo(String processSerialNum, Long processGroupId, String msg) {
        if(StringUtils.isNotBlank(msg) && Integer.compare(msg.length(), NUM_TWO_FIVE_FIVE) == 1) {
            msg = msg.substring(0, 250);
        }
        qlService.updateByWrapper(QlWrappers.update(MqlType.SCC_PJ_API_INVOICE_HEAD_INTF).set(SccPjApiInvoiceHeadIntfDto::getProcessMessage, msg)
                .eq(SccPjApiInvoiceHeadIntfDto::getProcessSerialNum, processSerialNum)
                .eq(SccPjApiInvoiceHeadIntfDto::getProcessGroupId, processGroupId));
    }

    private void saveCreateInvoiceIntf(String processSerialNum, Long processGroupId, CcApiInvoiceCreateRequestDto requestDto) {

        /** 保存请求头 */
        SccPjApiInvoiceHeadIntfDto headIntfDto = saveCreateInvoiceIntfHead(processSerialNum, processGroupId, requestDto.getHeader());

        /** 保存明细 */
        saveCreateInvoiceIntfItems(headIntfDto, requestDto.getItems());
    }


    /**
     * 保存明细
     * @param headIntfDto
     * @param items
     */
    private void saveCreateInvoiceIntfItems(SccPjApiInvoiceHeadIntfDto headIntfDto, List<CcApiInvoiceCreateItemsDto> items) {
        if(CollectionUtils.isEmpty(items)) {
            return;
        }

        List<SccPjApiInvoiceBaseIntfDto> baseIntfDtoList = new ArrayList<>(16);

        List<SccPjApiInvoiceConIntfDto> conIntfDtoList = new ArrayList<>(16);

        List<SccPjApiInvoiceSettleIntfDto> settleIntfDtoList = new ArrayList<>(16);

        List<SccPjApiSettleInvIntfDto> settleInvIntfDtoList = new ArrayList<>(16);

        List<SccPjApiSettleInfoIntfDto> settleInfoIntfDtoList = new ArrayList<>(16);

        List<SccPjApiInvoiceCostIntfDto> costIntfDtoList = new ArrayList<>(16);

        List<SccPjApiInvoiceCollIntfDto> collIntfDtoList = new ArrayList<>(16);

        List<SccPjApiInvoiceAttachIntfDto> attachIntfDtoList = new ArrayList<>(16);

        items.stream().forEach(item -> {
            if(!Objects.isNull(item.getBaseInfo())) {
                SccPjApiInvoiceBaseIntfDto baseIntfDto = baseIntf(headIntfDto, item.getBaseInfo());
                baseIntfDtoList.add(baseIntfDto);
            }

            if(!Objects.isNull(item.getContractInfo())) {
                SccPjApiInvoiceConIntfDto conIntfDto = contractIntf(headIntfDto, item.getContractInfo());
                conIntfDtoList.add(conIntfDto);
            }

            if(CollectionUtils.isNotEmpty(item.getSettleDetailList())) {
                item.getSettleDetailList().stream().forEach(settle -> {
                    SccPjApiInvoiceSettleIntfDto settleIntfDto = settleIntf(headIntfDto, settle);
                    settleIntfDtoList.add(settleIntfDto);

                    if(!Objects.isNull(settle.getInvoiceInfo())) {
                        SccPjApiSettleInvIntfDto invIntfDto = settleInvIntf(settleIntfDto, settle.getInvoiceInfo());
                        settleInvIntfDtoList.add(invIntfDto);
                    }

                    if(CollectionUtils.isNotEmpty(settle.getSettleInfoList())) {
                        settle.getSettleInfoList().stream().forEach(info -> {
                            SccPjApiSettleInfoIntfDto infoIntfDto = settleInfoIntf(settleIntfDto, info);
                            settleInfoIntfDtoList.add(infoIntfDto);
                        });
                    }
                });
            }

            if(CollectionUtils.isNotEmpty(item.getCostInfoList())) {
                item.getCostInfoList().stream().forEach(cost -> {
                    SccPjApiInvoiceCostIntfDto costIntfDto = costIntf(headIntfDto, cost);
                    costIntfDtoList.add(costIntfDto);
                });
            }

            if(CollectionUtils.isNotEmpty(item.getCollectionInfoList())) {
                item.getCollectionInfoList().stream().forEach(collectionInfoDto -> {
                    SccPjApiInvoiceCollIntfDto collIntfDto = collIntf(headIntfDto, collectionInfoDto);
                    collIntfDtoList.add(collIntfDto);
                });
            }

            if(CollectionUtils.isNotEmpty(item.getAttachList())) {
                item.getAttachList().stream().forEach(attach -> {
                    SccPjApiInvoiceAttachIntfDto attachIntfDto = attachIntf(headIntfDto, attach);
                    attachIntfDtoList.add(attachIntfDto);
                });
            }
        });


        if(CollectionUtils.isNotEmpty(baseIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_INVOICE_BASE_INTF, baseIntfDtoList);
        }

        if(CollectionUtils.isNotEmpty(conIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_INVOICE_CON_INTF, conIntfDtoList);
        }

        if(CollectionUtils.isNotEmpty(settleIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_INVOICE_SETTLE_INTF, settleIntfDtoList);
        }

        if(CollectionUtils.isNotEmpty(settleInvIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_SETTLE_INV_INTF, settleInvIntfDtoList);
        }

        if(CollectionUtils.isNotEmpty(settleInfoIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_SETTLE_INFO_INTF, settleInfoIntfDtoList);
        }

        if(CollectionUtils.isNotEmpty(costIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_INVOICE_COST_INTF, costIntfDtoList);
        }

        if(CollectionUtils.isNotEmpty(collIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_INVOICE_COLL_INTF, collIntfDtoList);
        }

        if(CollectionUtils.isNotEmpty(attachIntfDtoList)) {
            qlService.create(MqlType.SCC_PJ_API_INVOICE_ATTACH_INTF, attachIntfDtoList);
        }

    }

    private SccPjApiInvoiceAttachIntfDto attachIntf(SccPjApiInvoiceHeadIntfDto headIntfDto, CcApiInvoiceCreateItemsAttachDto data) {
        SccPjApiInvoiceAttachIntfDto dto = new SccPjApiInvoiceAttachIntfDto();
        BeanCopyUtil.copyProperties(dto, data);

        dto.setAttachIntfId(IdGenrator.generate());
        dto.setInvoiceIntfId(headIntfDto.getInvoiceIntfId());
        dto.setProcessDate(headIntfDto.getProcessDate());
        dto.setProcessGroupId(headIntfDto.getProcessGroupId());
        dto.setProcessSerialNum(headIntfDto.getProcessSerialNum());
        dto.setProcessStatus(headIntfDto.getProcessStatus());

        return dto;
    }

    private SccPjApiInvoiceCollIntfDto collIntf(SccPjApiInvoiceHeadIntfDto headIntfDto, CcApiInvoiceCreateItemsCollectionInfoDto data) {
        SccPjApiInvoiceCollIntfDto dto = new SccPjApiInvoiceCollIntfDto();
        BeanCopyUtil.copyProperties(dto, data);
        if(!Objects.isNull(data.getCollectionItemNo())) {
            dto.setCollectionItemNo(Objects.toString(data.getCollectionItemNo()));
        }

        dto.setCollectionIntfId(IdGenrator.generate());
        dto.setInvoiceIntfId(headIntfDto.getInvoiceIntfId());
        dto.setProcessDate(headIntfDto.getProcessDate());
        dto.setProcessGroupId(headIntfDto.getProcessGroupId());
        dto.setProcessSerialNum(headIntfDto.getProcessSerialNum());
        dto.setProcessStatus(headIntfDto.getProcessStatus());

        return dto;
    }

    private SccPjApiInvoiceCostIntfDto costIntf(SccPjApiInvoiceHeadIntfDto headIntfDto, CcApiInvoiceCreateItemsCostInfoDto data) {
        SccPjApiInvoiceCostIntfDto dto = new SccPjApiInvoiceCostIntfDto();
        BeanCopyUtil.copyProperties(dto, data);

        dto.setCostIntfId(IdGenrator.generate());
        dto.setInvoiceIntfId(headIntfDto.getInvoiceIntfId());
        dto.setProcessDate(headIntfDto.getProcessDate());
        dto.setProcessGroupId(headIntfDto.getProcessGroupId());
        dto.setProcessSerialNum(headIntfDto.getProcessSerialNum());
        dto.setProcessStatus(headIntfDto.getProcessStatus());

        return dto;
    }

    private SccPjApiSettleInfoIntfDto settleInfoIntf(SccPjApiInvoiceSettleIntfDto settleIntfDto, CcApiInvoiceCreateItemsSettleDetailSettleInfoDto data) {
        SccPjApiSettleInfoIntfDto dto = new SccPjApiSettleInfoIntfDto();
        BeanCopyUtil.copyProperties(dto, data);
        dto.setDiscountFlag(booleanToString(data.getDiscountFlag()));
        dto.setPreferentialPolicy(booleanToString(data.getPreferentialPolicy()));

        dto.setSettleInfoIntfId(IdGenrator.generate());
        dto.setInvoiceIntfId(settleIntfDto.getInvoiceIntfId());
        dto.setSettleIntfId(settleIntfDto.getSettleIntfId());

        return dto;
    }

    private SccPjApiSettleInvIntfDto settleInvIntf(SccPjApiInvoiceSettleIntfDto settleIntfDto, CcApiInvoiceCreateItemsSettleDetailInvoiceInfoDto data) {
        SccPjApiSettleInvIntfDto dto = new SccPjApiSettleInvIntfDto();
        BeanCopyUtil.copyProperties(dto, data);

        dto.setSettleInvIntfId(IdGenrator.generate());
        dto.setInvoiceIntfId(settleIntfDto.getInvoiceIntfId());
        dto.setSettleIntfId(settleIntfDto.getSettleIntfId());

        return dto;
    }

    private SccPjApiInvoiceSettleIntfDto settleIntf(SccPjApiInvoiceHeadIntfDto headIntfDto, CcApiInvoiceCreateItemsSettleDetailDto data) {
        SccPjApiInvoiceSettleIntfDto dto = new SccPjApiInvoiceSettleIntfDto();
        BeanCopyUtil.copyProperties(dto, data);

        dto.setSettleIntfId(IdGenrator.generate());
        dto.setInvoiceIntfId(headIntfDto.getInvoiceIntfId());
        dto.setProcessDate(headIntfDto.getProcessDate());
        dto.setProcessGroupId(headIntfDto.getProcessGroupId());
        dto.setProcessSerialNum(headIntfDto.getProcessSerialNum());
        dto.setProcessStatus(headIntfDto.getProcessStatus());

        return dto;
    }

    private SccPjApiInvoiceConIntfDto contractIntf(SccPjApiInvoiceHeadIntfDto headIntfDto, CcApiInvoiceCreateItemsContractInfoDto data) {
        SccPjApiInvoiceConIntfDto dto = new SccPjApiInvoiceConIntfDto();
        BeanCopyUtil.copyProperties(dto, data);
        dto.setContainTax(booleanToString(data.getContainTax()));

        dto.setContractIntfId(IdGenrator.generate());
        dto.setInvoiceIntfId(headIntfDto.getInvoiceIntfId());
        dto.setProcessDate(headIntfDto.getProcessDate());
        dto.setProcessGroupId(headIntfDto.getProcessGroupId());
        dto.setProcessSerialNum(headIntfDto.getProcessSerialNum());
        dto.setProcessStatus(headIntfDto.getProcessStatus());

        return dto;
    }

    private SccPjApiInvoiceBaseIntfDto baseIntf(SccPjApiInvoiceHeadIntfDto headIntfDto, CcApiInvoiceCreateItemsBaseInfoDto baseInfoDto) {
        SccPjApiInvoiceBaseIntfDto baseIntfDto = new SccPjApiInvoiceBaseIntfDto();
        BeanCopyUtil.copyProperties(baseIntfDto, baseInfoDto);
        baseIntfDto.setBaseIntfId(IdGenrator.generate());
        baseIntfDto.setInvoiceIntfId(headIntfDto.getInvoiceIntfId());
        baseIntfDto.setProcessDate(headIntfDto.getProcessDate());
        baseIntfDto.setProcessGroupId(headIntfDto.getProcessGroupId());
        baseIntfDto.setProcessSerialNum(headIntfDto.getProcessSerialNum());
        baseIntfDto.setProcessStatus(headIntfDto.getProcessStatus());

        return baseIntfDto;
    }


    /**
     * 保存请求头
     * @param processSerialNum
     * @param processGroupId
     * @param headerDto
     * @return
     */
    private SccPjApiInvoiceHeadIntfDto saveCreateInvoiceIntfHead(String processSerialNum, Long processGroupId, CcApiInvoiceCreateHeaderDto headerDto) {
        SccPjApiInvoiceHeadIntfDto headIntfDto = new SccPjApiInvoiceHeadIntfDto();

        BeanCopyUtil.copyProperties(headIntfDto, headerDto);

        headIntfDto.setNeedApprove(booleanToString(headerDto.getNeedApprove()));
        headIntfDto.setRedInvoice(booleanToString(headerDto.getRedInvoice()));

        headIntfDto.setInvoiceIntfId(IdGenrator.generate());
        headIntfDto.setProcessDate(new Date());
        headIntfDto.setProcessGroupId(processGroupId);
        headIntfDto.setProcessSerialNum(processSerialNum);
        headIntfDto.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());

        qlService.create(MqlType.SCC_PJ_API_INVOICE_HEAD_INTF, Collections.singletonList(headIntfDto));
        return headIntfDto;
    }

    private String booleanToString(Boolean value) {
        if(Objects.isNull(value)) {
            return null;
        }
        return Objects.toString(value);
    }
}
