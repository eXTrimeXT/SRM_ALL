package com.midea.cloud.srm.sou.inq.select.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouStartNewRoundDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouVendorAddDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouVendorRecordDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.inq.select.entity.SouVendor;
import com.midea.cloud.srm.sou.inq.select.entity.SouVendorRecord;
import com.midea.cloud.srm.sou.inq.select.mapper.SiSouVendorMapper;
import com.midea.cloud.srm.sou.inq.select.mapper.SiSouVendorRecordMapper;
import com.midea.cloud.srm.sou.inq.select.service.SiInqSouSelectEventWebService;
import com.midea.cloud.srm.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.control.ApiSouControlEventHandler;
import com.midea.cloud.srm.sou.sourcing.spi.control.ApiSouControlJudgeHandler;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendoradd.ApiSouVendorAddHandler;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendoradd.SouVendorAddPO;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendorrecord.ApiSouVendorAddRecordHandler;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendorrecord.SouVendorRecordPO;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderEventHandler;
import com.mideacloud.common.objectx.ExtensionMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ex_liuxy46
 */
@Service
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SiInqSouSelectEventWebServiceImpl implements SiInqSouSelectEventWebService {

    @Resource
    private SouProjectDAO souProjectDAO;
    @Resource
    private SouRoundDAO souRoundDAO;
    @Resource
    private SouVendorDAO souVendorDAO;
    @Resource
    private SouOrderItemDAO souOrderItemDAO;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private SouVendorAuthDAO souVendorAuthDAO;
    @Resource
    private SouVendorRecordDAO souVendorRecordDAO;
    @Resource
    private SiSouVendorRecordMapper siSouVendorRecordMapper;
    @Resource
    private SiSouVendorMapper siSouVendorMapper;
    public static final String SOU_VENDOR_ADD_LOCK = "SOU_VENDOR_ADD_LOCK_";
    private static final int SIX_TEEN = 16;

    private static final String DATE_FORMAT = "yyyy年MM月dd日 HH:mm:ss";

    private static final String INQ_CHOOSE_VENDOR_LIST = "inqChooseVendorList";

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;
    @Value("${global.srm.register-address:没有配置地址}")
    private String cloudUrl;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void startNewRound(ApiSouStartNewRoundDTO param, String reason) {
        String souType = SouTypeEnum.inq.name();
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeStartNewRoundAuth(param, souType);
        // 3: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeStartNewRound(param, souType);
        // 4: 发起新一轮
        souProjectDAO.lambdaUpdate()
                .set(SouProject::getOrderStartTime, param.getOrderStartTime())
                .set(SouProject::getOrderEndTime, param.getOrderEndTime())
                .set(SouProject::getCurrentRound, project.getCurrentRound() + 1)
                .set(SouProject::getEarliestBusinessOpenTime, param.getEarliestBusinessOpenTime())
                .set(SouProject::getProjectStatus, param.isStartNow() ?
                        SouProjectStatusEnum.ACCEPT_ORDER : SouProjectStatusEnum.ORDER_NOT_START)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        // 5: 新增轮次信息
        SouRound round = new SouRound(); {
            // ID
            round.setRoundId(IdGenrator.generate());
            // 寻源单ID
            round.setProjectId(param.getProjectId());
            // 轮次
            round.setRound(project.getCurrentRound() + 1);
            // 报价开始时间
            round.setOrderStartTime(param.getOrderStartTime());
            // 报价截止时间
            round.setOrderEndTime(param.getOrderEndTime());
            // 是否已公开本轮结果
            round.setHasPublishResult(Enable.N);
            int count;
            {
                count = (int) souOrderItemDAO.lambdaQuery()
                        .eq(SouOrderItem::getProjectId, param.getProjectId())
                        .eq(SouOrderItem::getRound, project.getCurrentRound())
                        .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.Y)
                        .list()
                        .stream().map(SouOrderItem::getVendorId).count();
                count += param.getNewVendors() != null ? param.getNewVendors().size() : 0;
            }
            // 本轮应报价供应商数量
            round.setInviteCount(count);
            // 已报价供应商数量
            round.setOrderCount(0);
            // 商务开标标识
            round.setBusinessOpen(Enable.N);
            // 报价解密标识
            round.setPriceDecrypt(Enable.N);
            // 开标密码信息
            round.setOpenPwdInfo(new HashMap<>(SIX_TEEN));
            // 最早开标时间
            round.setEarliestBusinessOpenTime(param.getEarliestBusinessOpenTime());
        }
        souRoundDAO.save(round);
        // 6: 新增供应商
        if (Enable.Y.equals(project.getAllowNewVendors())) {
            if (CollectionUtils.isNotEmpty(param.getNewVendors())) {
                // 6.1: 记录供应商追加信息
                ApiSouVendorRecordDTO vendorRecordDTO = new ApiSouVendorRecordDTO(); {
                    vendorRecordDTO.setProjectId(param.getProjectId());
                    vendorRecordDTO.setVendorList(param.getNewVendors());
                }
                this.recordSouVendorAddInfo(vendorRecordDTO, souType, reason);
            }
            // 6.2: 执行供应商追加信息
            ApiSouVendorAddDTO vendorAddDTO = new ApiSouVendorAddDTO(); {
                vendorAddDTO.setProjectId(param.getProjectId());
                vendorAddDTO.setAuthList(new ArrayList<>(32));
                if (CollectionUtils.isEmpty(param.getNewVendors())) { param.setNewVendors(new ArrayList<>(SIX_TEEN)); }
                param.getNewVendors().forEach(vendor -> {
                    if (CollectionUtils.isNotEmpty(vendor.getAuthList())) {
                        vendor.getAuthList().forEach(auth -> auth.setVendorId(vendor.getVendorId()));
                        vendorAddDTO.getAuthList().addAll(vendor.getAuthList());
                    }
                });
            }
            this.executeSouVendorAdd(vendorAddDTO, souType, reason);
        }
        // 7: 更新本轮的应/已报价供应商数量
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerForOrderCountWhileOrder(param.getProjectId());
        // 8: 行业包额外处理
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterStartNewRound(param, souType);
        // 9: 供应商发送邮件
        if(param.getExtensions().get(INQ_CHOOSE_VENDOR_LIST) != null) {
            String json = JSON.toJSONString(param.getExtensions().get("inqChooseVendorList"));
            JSONArray list = JSON.parseArray(json);
            sendEmailByTemplate(list);
        }

    }


    private void sendEmailByTemplate(JSONArray list) {
        for(int i = 0; i < list.size(); i++) {
            JSONObject dto = list.getJSONObject(i);
            if(StringUtils.isEmpty(dto.getString("email"))) {
                continue;
            }
            String email = dto.getString("email");
            NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
            noticeSendDTO.setMsgTemplateCode("PJ_INQ_VENDOR_NOTICE");
            noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
            Map<String, Object> msgParams = new HashMap<>(16);
            msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, email.trim());
            msgParams.put("srmAddress", cloudUrl);
            msgParams.put("sendTime", DateUtil.format(new Date(), DATE_FORMAT));
            noticeSendDTO.setMsgParams(msgParams);
            log.info("询价轮次发送邮件： {}", email);
            noticeSendGlobalClientService.send(noticeSendDTO);
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recordSouVendorAddInfo(ApiSouVendorRecordDTO param, String souType, String reason) {
        // 1: 入参校验
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeRecordSouVendorAddAuth(param, souType);
        // 3: 加锁
        String key = SOU_VENDOR_ADD_LOCK + param.getProjectId();
        boolean locked = redisUtil.tryLockInTime(
                key,
                // 过期时间
                10,
                // 获取锁的等待时间
                3,
                // 重试次数
                2);
        AssertUtils.isTrue(locked, "新增记录处理中，请稍后重试");
        try {
            // 4: 行业包额外处理(前置)
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeRecordVendorAddInfo(param, souType);
            // 5: 入参校验+转换处理
            SouVendorRecordPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouVendorAddRecordHandler.class).formatValidateAndConvert(param, souType);
            // 6: 保存数据
            if (CollectionUtils.isNotEmpty(po.getSaveRecordList())) {
                //souVendorRecordDAO.saveBatch(po.getSaveRecordList());
                po.getSaveRecordList().forEach(e -> {
                    SouVendorRecord svr = BeanUtil.copyProperties(e, SouVendorRecord.class);
                    svr.setVendorInfo(e.getVendorInfo());
                    svr.setReason(reason);
                    siSouVendorRecordMapper.insert(svr);
                });
            }
            if (CollectionUtils.isNotEmpty(po.getUpdateRecordList())) {
                //souVendorRecordDAO.updateBatchById(po.getUpdateRecordList());
                po.getUpdateRecordList().forEach(e -> {
                    SouVendorRecord svr = BeanUtil.copyProperties(e, SouVendorRecord.class);
                    svr.setVendorInfo(e.getVendorInfo());
                    svr.setReason(reason);
                    siSouVendorRecordMapper.updateById(svr);
                });
            }
            // 7: 行业包额外处理(后置)
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterRecordVendorAddInfo(param, souType, po);
        } finally {
            // 8: 解锁
            redisUtil.unLock(key);
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void executeSouVendorAdd(ApiSouVendorAddDTO param, String souType, String reason) {
        // 1: 入参校验
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeExecuteSouVendorAddAuth(param.getProjectId(), souType);
        // 3: 加锁
        String key = SOU_VENDOR_ADD_LOCK + param.getProjectId();
        boolean locked = redisUtil.tryLockInTime(
                key,
                // 过期时间
                10,
                // 获取锁的等待时间
                3,
                // 重试次数
                2);
        AssertUtils.isTrue(locked, "新增记录处理中，请稍后重试");
        try {
            // 4: 行业包额外处理(前置)
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeVendorAdd(param, souType);
            // 5: 执行物料变更
            SouVendorAddPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouVendorAddHandler.class).convert(param, souType);
            if (CollectionUtils.isNotEmpty(po.getUpdateRecordList())) {
                //souVendorRecordDAO.updateBatchById(po.getUpdateRecordList());
                po.getUpdateRecordList().forEach(e -> {
                    SouVendorRecord svr = BeanUtil.copyProperties(e, SouVendorRecord.class);
                    svr.setVendorInfo(e.getVendorInfo());
                    svr.setReason(reason);
                    siSouVendorRecordMapper.updateById(svr);
                });
            }
            if (CollectionUtils.isNotEmpty(po.getSaveVendorList())) {
                //souVendorDAO.saveBatch(po.getSaveVendorList());
                po.getSaveVendorList().forEach(e -> {
                    com.midea.cloud.srm.model.extapi.sou.inq.entity.SouVendor svr = BeanUtil.copyProperties(e, com.midea.cloud.srm.model.extapi.sou.inq.entity.SouVendor.class);
                    svr.setReason(reason);
                    siSouVendorMapper.insert(svr);
                });
            }
            if (CollectionUtils.isNotEmpty(po.getSaveAuthList())) {
                souVendorAuthDAO.saveBatch(po.getSaveAuthList());
            }
            // 6: 行业包额外处理(后置)
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterVendorAdd(param, souType, po);
        } finally {
            // 7: 解锁
            redisUtil.unLock(key);
        }
    }
}
