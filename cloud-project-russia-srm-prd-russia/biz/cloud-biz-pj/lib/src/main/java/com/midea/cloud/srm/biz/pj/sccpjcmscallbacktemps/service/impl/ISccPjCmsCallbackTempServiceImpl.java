package com.midea.cloud.srm.biz.pj.sccpjcmscallbacktemps.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sccpjcmscallbacktemps.mapper.SccPjCmsCallbackTempMapper;
import com.midea.cloud.srm.biz.pj.sccpjcmscallbacktemps.service.ISccPjCmsCallbackTempService;
import com.midea.cloud.srm.feign.pj.sou.SouSignClient;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity.SccPjCmsCallbackTemp;
import com.midea.cloud.srm.model.pj.extapis.cmscloud.dto.*;
import com.midea.cloud.srm.model.pj.extapis.cmscloud.enums.CmscloudResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description 财务共享-回推接口表-实现类
 * @author panmq
 * @date 2024-03-04
 */
@Slf4j
@Service
public class ISccPjCmsCallbackTempServiceImpl extends ServiceImpl<SccPjCmsCallbackTempMapper, SccPjCmsCallbackTemp> implements ISccPjCmsCallbackTempService {

    @Autowired
    private SouSignClient souSignClient;

    private static final int NUM_TWO_FIVE_ZERO = 250;

    @Override
    public CmscloudBodyDto<List<CmscloudBodyDataDto>> callbackSrm(CmscloudBodyDto<List<CmscloudBodyDataDto>> request) {

        List<SccPjCmsCallbackTemp> tempList = new ArrayList<>(50);

        Long processGroupId = System.currentTimeMillis();;

        if(CollectionUtils.isNotEmpty(request.getData())) {
            request.getData().stream().forEach(data -> tempList.add(buildTemp(data, request.getSerialNum(), processGroupId)));
        }

        if(CollectionUtils.isNotEmpty(tempList)) {
            this.saveBatch(tempList);
            this.handlerSrmWithBusiness(tempList);
        }

        /** 返回结果 */
        CmscloudBodyDto<List<CmscloudBodyDataDto>> responseBody = new CmscloudBodyDto<>();
        responseBody.setSerialNum(request.getSerialNum());
        responseBody.setResultCode(CmscloudResultEnum.SUCCESS.getCode());
        responseBody.setResultMsg(CmscloudResultEnum.SUCCESS.getMsg());
        responseBody.setData(request.getData());
        responseBody.getData().stream().forEach(data -> {
            data.setBusinessResultCode(CmscloudResultEnum.SUCCESS.getCode());
            data.setBusinessResultMsg(CmscloudResultEnum.SUCCESS.getMsg());
        });

        return responseBody;
    }

    @Override
    public void handlerSrmWithBusiness(List<SccPjCmsCallbackTemp> sccPjCmsCallbackTempList) {
        if(CollectionUtils.isEmpty(sccPjCmsCallbackTempList)) {
            return;
        }
        try {
            souSignClient.callBackAsApiPayment(sccPjCmsCallbackTempList);
            sccPjCmsCallbackTempList.stream().forEach(s -> {
                s.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
                s.setProcessDate(new Date());
                s.setProcessMessage(ProcessStatusEnum.COMPLETED.getName());
            });
        } catch (Exception e) {
            log.error("handlerSrmWithBusiness Exception", e);
            sccPjCmsCallbackTempList.stream().forEach(s -> {
                s.setProcessStatus(ProcessStatusEnum.ERROR.getCode());
                s.setProcessDate(new Date());
                s.setProcessMessage(limitStr(MessageFormat.format("回调业务接口处理异常：{0}", e.getMessage())));
            });
        }
        this.updateBatchById(sccPjCmsCallbackTempList);
    }

    private String limitStr(String value) {
        if(StringUtils.isBlank(value)) {
            return "";
        }
        if(value.length() > NUM_TWO_FIVE_ZERO) {
            return value.substring(0, NUM_TWO_FIVE_ZERO);
        }
        return value;
    }

    /**
     * 构建实体类
     * @param request
     * @param serialNum
     * @param processGroupId
     * @return
     */
    protected SccPjCmsCallbackTemp buildTemp(CmscloudBodyDataDto request, String serialNum, Long processGroupId) {
        SccPjCmsCallbackTemp temp = new SccPjCmsCallbackTemp();
        try {
            BeanCopyUtil.copyProperties(temp, request, true);
        } catch (Exception e) {
            log.error("SccPjCmsCallbackTemp buildTemp Exception as copyBean", e);
            throw new BaseException("实体类转换异常");
        }
        temp.setProcessSerialNum(serialNum);
        temp.setProcessStatus(ProcessStatusEnum.PENDING.getCode());
        temp.setProcessGroupId(processGroupId);
        temp.setCmsCallbackTempId(IdGenrator.generate());
        return temp;
    }

    @Override
    public void rehandlerSrmWithBusiness(Map<String, Object> param) {
        LambdaQueryWrapper<SccPjCmsCallbackTemp> queryWrapper = new LambdaQueryWrapper<>();

        List<String> requestItemIdList = (List<String>) param.get("requestItemIdList");
        queryWrapper.in(CollectionUtils.isNotEmpty(requestItemIdList), SccPjCmsCallbackTemp::getRequestItemId, requestItemIdList);

        List<String> processSerialNumList = (List<String>) param.get("processSerialNumList");
        queryWrapper.in(CollectionUtils.isNotEmpty(processSerialNumList), SccPjCmsCallbackTemp::getProcessSerialNum, processSerialNumList);

        queryWrapper.eq(SccPjCmsCallbackTemp::getProcessStatus, ProcessStatusEnum.PENDING.getCode());

        handlerSrmWithBusiness(this.list(queryWrapper));

    }
}

