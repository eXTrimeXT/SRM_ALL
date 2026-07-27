package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.SccPrReqEdmattachSyncEntity;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.EdmattachSyncService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/5
 */
@Slf4j
@Service
public class EdmattachSyncServiceImpl implements EdmattachSyncService {

    @Autowired
    private QlService qlService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FileCenterClient fileCenterClient;

    /**
     * 同步EDM附件锁
     */
    private static final String EMD_SYNC_ATTACH_LOCK = "EMD_SYNC_ATTACH_LOCK";

    private static final int NUM246 = 246;

    @Override
    public void addSyncTask(Long requirementHeadId) {
        List<Record> recordList = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_LINE).eq(PurchaseRequirementLineDTO::getRequirementHeadId, requirementHeadId), Record.class);
        if(CollectionUtils.isEmpty(recordList)) {
            return;
        }

        List<SccPrReqEdmattachSyncEntity> attachList = new ArrayList<>(15);

        recordList.stream().forEach(record -> {
            if(StringUtils.isNotBlank(record.get(PurchaseRequirementLineDTO::getExtAttachName))) {
                SccPrReqEdmattachSyncEntity entity = new SccPrReqEdmattachSyncEntity();
                entity.setRequirementLineId(record.get(PurchaseRequirementLineDTO::getRequirementLineId));
                entity.setExtAttachName(record.get(PurchaseRequirementLineDTO::getExtAttachName));

                entity.setSyncStatus(ProcessStatusEnum.PENDING.getCode());
                attachList.add(entity);
            }
        });
        if(CollectionUtils.isNotEmpty(attachList)) {
            qlService.create(MqlType.SCC_PR_REQ_EDMATTACH_SYNC, attachList);
        }
    }

    @SneakyThrows(value = Exception.class)
    @Override
    public void syncAttach(Map<String, Object> param) {

        if(redisUtil.hasLock(EMD_SYNC_ATTACH_LOCK)) {
            throw new BaseException("EMD附件正在同步中，请勿重复请求");
        }
        if(!redisUtil.tryLock(EMD_SYNC_ATTACH_LOCK, 1, TimeUnit.HOURS)) {
            throw new BaseException("EMD附件同步任务，获取锁失败");
        }

        try {
            String lastUpdateDateFrom = MapUtils.getString(param, "lastUpdateDate");
            String lastUpdateDateTo = MapUtils.getString(param, "lastUpdateDateTo");

            Long requirementLineId = MapUtils.getLong(param, "requirementLineId");

            QlQueryWrapper queryWrapper = QlWrappers.query(MqlType.SCC_PR_REQ_EDMATTACH_SYNC);
            if(StringUtils.isNotBlank(lastUpdateDateFrom)) {
                queryWrapper.ge(SccPrReqEdmattachSyncEntity::getLastUpdateDate, DateUtil.parseDate(lastUpdateDateFrom));
            }
            if(StringUtils.isNotBlank(lastUpdateDateTo)) {
                queryWrapper.le(SccPrReqEdmattachSyncEntity::getLastUpdateDate, DateUtil.parseDate(lastUpdateDateTo));
            }
            if(ObjectUtils.allNotNull(requirementLineId)) {
                queryWrapper.eq(SccPrReqEdmattachSyncEntity::getRequirementLineId, requirementLineId);
            }

            queryWrapper.eq(SccPrReqEdmattachSyncEntity::getSyncStatus, ProcessStatusEnum.PENDING.getCode());

            List<SccPrReqEdmattachSyncEntity> syncEntityList = qlService.queryByWrapper(queryWrapper, SccPrReqEdmattachSyncEntity.class);

            if(CollectionUtils.isEmpty(syncEntityList)) {
                return;
            }

            syncEntityList.stream().forEach(entity -> {
                log.info("同步附件[" + entity.getExtAttachName() +"]开始");
                syncFile(entity);
                log.info("同步附件[" + entity.getExtAttachName() +"]结束");
            });

            List<SccPrReqEdmattachSyncEntity> successList = syncEntityList.stream().filter(entity -> ProcessStatusEnum.COMPLETED.getCode().equals(entity.getSyncStatus())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(successList)) {
                List<Long> requirementLineIdList = successList.stream().map(e -> e.getRequirementLineId()).collect(Collectors.toList());
                List<Record> requirementLineRecords = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_LINE).in(PurchaseRequirementLineDTO::getRequirementLineId, requirementLineIdList), Record.class);
                Map<Long, SccPrReqEdmattachSyncEntity> entityMap = successList.stream().collect(Collectors.toMap(k -> k.getRequirementLineId(), Function.identity(), (k1, k2) -> k2));

                if(CollectionUtils.isNotEmpty(requirementLineRecords)) {
                    requirementLineRecords.stream().forEach(line -> {
                        SccPrReqEdmattachSyncEntity entity = entityMap.get(line.get(PurchaseRequirementLineDTO::getRequirementLineId));
                        if(ObjectUtils.allNotNull(entity)) {
                            line.put(PurchaseRequirementLineDTO::getExtAttachId, entity.getExtAttachId());
                            line.put(PurchaseRequirementLineDTO::getExtAttachName, entity.getSyncMessage());
                        }
                    });

                    qlService.update(MqlType.PURCHASE_REQUIREMENT_LINE, requirementLineRecords);
                }
            }

            qlService.update(MqlType.SCC_PR_REQ_EDMATTACH_SYNC, syncEntityList);

        } catch (Exception e) {
            log.error("EMD附件同步失败", e);
            throw new BaseException(e.getMessage());
        } finally {
            redisUtil.unLock(EMD_SYNC_ATTACH_LOCK);
        }


    }

    private void syncFile(SccPrReqEdmattachSyncEntity entity) {
        InputStream inputStream = null;
        try {
            MockMultipartFile multipartFile;
            String aa = entity.getExtAttachName().substring(0, entity.getExtAttachName().indexOf("?"));
            String fileType = aa.substring(aa.lastIndexOf(".") + 1).toLowerCase();
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(entity.getExtAttachName());
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            // 获取输入流来读取文件内容
            inputStream = httpEntity.getContent();
            // 将输入流转为字节数组
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            String originalFilename = UUID.randomUUID().toString();
            log.info("附件类型信息---***===" + fileType);
            // 创建MultipartFile对象
            multipartFile =  new MockMultipartFile(originalFilename, originalFilename + "." + fileType, "application/" + fileType, fileBytes);
            String sourceType = "WEB_APP";
            String uploadType = FileUploadType.DEF.name();
            String fileModular = "sup";
            String fileFunction = "vendorBiddingManagement";
            log.info("附件类型信息===" + multipartFile.getContentType());
            Fileupload fl = fileCenterClient.feignClientUpload(multipartFile, sourceType, uploadType, fileModular, fileFunction, fileType);
            log.info("返回的信息===" + JSONObject.toJSONString(fl));

            entity.setExtAttachId(fl.getFileuploadId());
            entity.setSyncStatus(ProcessStatusEnum.COMPLETED.getCode());
            entity.setSyncMessage(fl.getFileSourceName());
        } catch (Exception e) {
            log.error("syncFile Exception", e);
            entity.setSyncStatus(ProcessStatusEnum.ERROR.getCode());
            String msg = e.getMessage();
            if(StringUtils.isNotBlank(msg) && msg.length() > NUM246) {
                msg = msg.substring(0, 246);
            }
            entity.setSyncMessage("同步失败:" + msg);
        } finally {
            if(ObjectUtils.allNotNull(inputStream)) {
                try {
                    inputStream.close(); // 关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
