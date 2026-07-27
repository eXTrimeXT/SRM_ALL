package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendBatchDTO;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PrPushConfig;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PrPushNotify;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.enums.NotifyTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PrPushConfigService;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PurchaseRequirementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Service
public class PrPushConfigServiceImpl implements PrPushConfigService {

    @Autowired
    private QlService qlService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;
    @Autowired
    private PurchaseRequirementService purchaseRequirementService;
    @Autowired
    private IDivisionCategoryService divisionCategoryService;

    /**
     * 自动分单短信模板
     */
    @Value("${pr.config.pushMsgTemplate}")
    private String prConfigPushMsgTemplate;

    /**
     * 自动分单有效时间范围（min）
     */
    @Value("${pr.config.pushValidMin:15}")
    private Integer PR_CONFIG_PUSH_VALID_MIN;

    /**
     * 自动分单
     */
    @Override
    public void autoPushPoolTask() {
        List<PrPushConfig> configs = qlService.queryByWrapper(QlWrappers.query("PrPushConfig"), PrPushConfig.class);
        if (CollectionUtils.isEmpty(configs)) {
            log.info("尚未配置自动领单规则~");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        Integer weekday = calendar.get(Calendar.DAY_OF_WEEK);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer min = calendar.get(Calendar.MINUTE);
        log.info("执行自动领单：周{}-{}:{}, 自动分单有效时间范围（min）:{}", weekday, hour, min, PR_CONFIG_PUSH_VALID_MIN);

        configs.stream().filter(e -> isTimeUp(e, weekday, hour, min)).forEach(e -> {
            log.info("执行自动领单业务单元：{}-{}", e.getOrgCode(), e.getOrgName());

            try {
                // TODO 分页执行
                List<PurchaseRequirementHeadDTO> requirementHeads = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementHead")
                        .eq(PurchaseRequirementHeadDTO::getOrgId, e.getOrgId())
                        .eq(PurchaseRequirementHeadDTO::getAuditStatus, RequirementApproveStatus.APPROVED)
                        .eq(PurchaseRequirementHeadDTO::getExtInPool, YesOrNo.NO.getValue()), PurchaseRequirementHeadDTO.class);
                log.info("执行自动领单业务单元：{}-{}，符合数量：{}", e.getOrgCode(), e.getOrgName(), requirementHeads.size());
                if (CollectionUtils.isEmpty(requirementHeads)) {
                    return;
                }

                // 领单
                int success = pushPool(requirementHeads, e);

                // 发送通知
                sendMsg(e, requirementHeads.size(), success);
            } catch (Exception ex) {
                log.error("执行自动领单失败，业务单元：{}-{}", e.getOrgCode(), e.getOrgName(), ex);
            }

        });
    }

    private int pushPool(List<PurchaseRequirementHeadDTO> requirementHeads, PrPushConfig e) {
        int success = 0;
        for (PurchaseRequirementHeadDTO requirementHead : requirementHeads) {
            try {
                purchaseRequirementService.pushPool(requirementHead, e.getPushUserCode(), e.getPushUserName());
                success++;
            } catch (Exception ex){
                log.error("执行自动领单单据异常，申请单号：{}", requirementHead.getRequirementHeadNum(), ex);
            }
        }
        log.info("执行自动领单完成，业务单元：{}-{}, 执行数量：{}，成功数量：{}", e.getOrgCode(), e.getOrgName(), requirementHeads.size(), success);
        return success;
    }

    private void sendMsg(PrPushConfig e, int total, int success) {
        if (YesOrNo.NO.getValue().equals(e.getNotifyFlag())) {
            log.info("发送短信通知未开启");
            return;
        }
        if (StringUtils.isBlank(prConfigPushMsgTemplate)) {
            log.info("消息模板未配置");
            return;
        }

        try {
            Map<String, String> userMobileMap = null;
            if (NotifyTypeEnum.PURCHASER.getName().equals(e.getNotifyType())) {
                // 查询采购员
                List<DivisionCategory> purchasers = divisionCategoryService.list(new LambdaQueryWrapper<DivisionCategory>()
                        .eq(DivisionCategory::getOrgId, e.getOrgId())
                        .eq(DivisionCategory::getDuty, "purchaser")
                        .le(DivisionCategory::getStartDate, LocalDate.now())
                        .and(wrapper -> wrapper.isNull(DivisionCategory::getEndDate).or().ge(DivisionCategory::getEndDate, LocalDate.now())));
                if (CollectionUtils.isEmpty(purchasers)) {
                    log.info("执行自动领单未发送短信，业务单元：{}-{}，未查询到采购员");
                    return;
                }
                List<Long> userIds = purchasers.stream().map(purchaser -> purchaser.getPersonInChargeUserId()).collect(Collectors.toList());
                List<User> users = qlOpenClient.query(ContextPath.RBAC, QlOpenWrappers.query("rbac_user_ide")
                        .in(User::getUserId, userIds), User.class);
                if (CollectionUtils.isEmpty(users)) {
                    log.info("执行自动领单未发送短信，业务单元：{}-{}，未查询到采购员手机号");
                    return;
                }
                userMobileMap = users.stream().collect(Collectors.toMap(user -> user.getUsername(), user -> user.getPhone()));
            } else {
                // 查询配置用户
                List<PrPushNotify> users = qlService.queryByWrapper(QlWrappers.query("PrPushNotify")
                        .eq(PrPushNotify::getConfigId, e.getConfigId()), PrPushNotify.class);
                if (CollectionUtils.isEmpty(users)) {
                    log.info("执行自动领单未发送短信，业务单元：{}-{}，未配置发短信人员");
                    return;
                }
                userMobileMap = users.stream().collect(Collectors.toMap(user -> user.getUserName(), user -> user.getUserMobile()));
            }
            sendSms(userMobileMap, total, success);
        } catch (Exception ex) {
            log.error("执行自动领单失败，业务单元：{}-{}，发送短信失败", e.getOrgCode(), e.getOrgName(), ex);
        }
    }

    private void sendSms(Map<String, String> userMobileMap, int total, int success) {
        if (MapUtils.isEmpty(userMobileMap)) {
            return;
        }

        List<NoticeSendDTO> list = new ArrayList<>();
        userMobileMap.forEach((username, mobile) -> {
            if (StringUtils.isBlank(mobile)) {
                log.info("手机号为空, username:{}", username);
                return;
            }

            Map<String, Object> argMap = new HashMap<>(15);
            argMap.put("total", total);
            argMap.put("success", success);
            argMap.put("fail", total - success);

            Map<String, Object> msgParams = new HashMap<>(15);
            msgParams.put(NoticeSendDTO.NOTICE_SMS_PARAM, argMap);
            msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, mobile);

            NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
            noticeSendDTO.setMsgTemplateCode(prConfigPushMsgTemplate);
            noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
            noticeSendDTO.setMsgParams(msgParams);
            list.add(noticeSendDTO);
        });

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        log.info("调用发短信接口：param: {}", prConfigPushMsgTemplate, JSONUtil.toJsonStr(list));
        NoticeSendBatchDTO noticeSendBatchDTO = new NoticeSendBatchDTO();
        noticeSendBatchDTO.setList(list);
        noticeSendGlobalClientService.sendBatch(noticeSendBatchDTO);
    }

    private boolean isTimeUp(PrPushConfig config, Integer currentWeekday, Integer currentHour, Integer currentMin) {
        try {
            List<String> configWeekdays = Arrays.asList(config.getPushDate().split(","));
            if (!configWeekdays.contains(currentWeekday.toString())) {
                return false;
            }

            String[] times = config.getPushTime().split(":");
            Integer configHour = Integer.parseInt(times[0]);
            Integer configMin = Integer.parseInt(times[1]);
            int configMinOfDay = configHour * 60 + configMin;
            int currentMinOfDay = currentHour * 60 + currentMin;
            if (configMinOfDay <= currentMinOfDay && configMinOfDay > (currentMinOfDay - PR_CONFIG_PUSH_VALID_MIN)) {
                return true;
            }
        } catch (Exception ex) {
            log.error("获取分单调度周期失败，record:{}", JSONUtil.toJsonStr(config), ex);
        }

        return false;
    }

}
