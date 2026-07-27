package com.midea.cloud.srm.sou.req.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.req.mapper.SouReqHeadMapper;
import com.midea.cloud.srm.sou.req.service.SouReqHeadService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 寻源需求单头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@Service
@AllArgsConstructor
public class SouReqHeadServiceImpl extends BaseServiceImpl<SouReqHeadMapper, SouReqHead> implements SouReqHeadService {
    @Autowired
    private QlOpenClient qlOpenClient;

    @SneakyThrows(value = {Exception.class})
    @Async
    @Override
    public void handleSignupDone() {
        this.signupDone();
    }

    private void signupDone() {
        List<SouReqHead> reqHeadList = this.list(new LambdaQueryWrapper<SouReqHead>()
                //取报名时间半个小时内过期的单据
                .le(SouReqHead::getPublicEndTime, DateUtil.offsetMinute(new Date(), 30))
                //状态为报名中
                .eq(SouReqHead::getStatus, SouReqHeadStatusEnum.APPROVED.getCode()));
        List<SouReqHead> updateList = new ArrayList<>();
        reqHeadList.stream().filter(reqHead -> reqHead.getPublicEndTime().getTime() <= System.currentTimeMillis()).forEach(reqHead -> {
            SouReqHead update = new SouReqHead();
            update.setReqHeadId(reqHead.getReqHeadId());
            update.setStatus(SouReqHeadStatusEnum.SIGNUP_DONE.getCode());
            updateList.add(update);
        });
        if (!updateList.isEmpty()) {
            this.updateBatchById(updateList);
        }
    }

    @Override
    public void handleSignupDoneSync() {
        this.signupDone();
    }

    @Override
    public void updateExtPr(Map<String, Object> params) {
        qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD)
                .set("earnestMoney", params.get("depositAmount"))
                .in("requirementHeadId", Arrays.asList(params.get("requirementHeadIdList").toString().split(","))));
    }

    @Override
    public void updateRequirementHeadExtPr(Map<String, Object> params) {
        qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update(MqlType.PR_SOU_REQUIREMENT_POOL_FOR_BUYER)
                .set("extPublicEndTime", params.get("publicEndTime"))
                .eq("requirementHeadId", params.get("requirementHeadId")));
    }
}
