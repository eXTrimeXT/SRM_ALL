package com.midea.cloud.srm.sou.designplans.flow;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.designplans.entity.*;
import com.midea.cloud.srm.model.sou.designplans.enums.PaaAdjustEnums;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouInitDTO;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.designplans.mapper.*;
import com.midea.cloud.srm.sou.designplans.service.DesignPlanService;
import com.midea.cloud.srm.sou.designplans.service.PaaAdjustService;
import com.midea.cloud.srm.sou.inq.init.controller.WebInqSouInitForBuyerController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ex_liuxy46
 */
@Slf4j
@Service
public class PriceAdjustmentApplyFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource
    private PaaAdjustService paaAdjustService;

    @Resource
    private BaseExtClient baseExtClient;
    @Resource
    private PjProjectExtClient pjProjectExtClient;
    private static final String BUSINESS_TYPE = "PRICE_ADJUST_APPLY";
    @Resource
    private RedisUtil redisUtil;
    @Value("${bpm.tjsq.processGroupId2}")
    private String processGroupId2;

    @Resource
    private PaaMarketSituationMapper paaMarketSituationMapper;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        SccSouChPaaAdjust adjust = paaAdjustService.getById(businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                //询比价调整
                if (NumConstant.ONE_STR.equals(adjust.getAdjustType())) {
                    createInquiryComparisonPrices();
                }
                //市场行情调整
                if (NumConstant.TWO_STR.equals(adjust.getAdjustType())) {
                    createMarketSituation(adjust);
                }
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                adjust.setAdjustId(businessId);
                adjust.setStartBpmUsername(loginAppUser.getUsername());
                adjust.setStartBpmNickname(loginAppUser.getNickname());
                adjust.setStatus(PaaAdjustEnums.SUBMIT.getCode());
                paaAdjustService.updateById(adjust);

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            //询比价调整
            if (NumConstant.ONE_STR.equals(adjust.getAdjustType())) {
                createInquiryComparisonPrices();
            }
            //市场行情调整
            if (NumConstant.TWO_STR.equals(adjust.getAdjustType())) {
                createMarketSituation(adjust);
            }
            adjust.setAdjustId(businessId);
            adjust.setStatus(PaaAdjustEnums.SUBMIT.getCode());
            paaAdjustService.updateById(adjust);
        }
    }

    /**
     * 创建询比价
     */
    public void createInquiryComparisonPrices() {
        WebInqSouInitForBuyerController fb = new WebInqSouInitForBuyerController();
        ApiInqSouInitDTO param = new ApiInqSouInitDTO();
        //todo
        fb.editInq(param);
    }

    /**
     * 创建市场行情
     */
    public void createMarketSituation(SccSouChPaaAdjust adjust) {
        SccSouChPaaMarketSituation situation = new SccSouChPaaMarketSituation();
        situation.setAdjustId(adjust.getAdjustId());
        situation.setAdjustCode(adjust.getAdjustCode());
        paaMarketSituationMapper.insert(situation);
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        SccSouChPaaAdjust adjust = new SccSouChPaaAdjust();
        adjust.setAdjustId(businessId);
        adjust.setStatus(PaaAdjustEnums.PASS.getCode());
        paaAdjustService.updateById(adjust);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        SccSouChPaaAdjust adjust = new SccSouChPaaAdjust();
        adjust.setAdjustId(businessId);
        adjust.setStatus(PaaAdjustEnums.REJECT.getCode());
        paaAdjustService.updateById(adjust);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        SccSouChPaaAdjust adjust = new SccSouChPaaAdjust();
        adjust.setAdjustId(businessId);
        adjust.setStatus(PaaAdjustEnums.WITHDRAW.getCode());
        paaAdjustService.updateById(adjust);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        SccSouChPaaAdjust adjust = new SccSouChPaaAdjust();
        adjust.setAdjustId(businessId);
        adjust.setStatus(PaaAdjustEnums.ABANDONED.getCode());
        paaAdjustService.updateById(adjust);
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        SccSouChPaaAdjust adjust = paaAdjustService.getById(businessId);

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+adjust.getAdjustCode()+"-"+AppUserUtil.getLoginAppUser().getNickname());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(new JSONObject());
        return JSONObject.toJSONString(bpmParam);
    }
}
