package com.midea.cloud.srm.sou.meiql.bidnotices.service.impl;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sign.service.ISignCallbackService;
import com.midea.cloud.srm.model.sou.enums.BidSignStatusEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouFileConfigTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouOrderFileService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class BidBusinessSignCallbackServiceImpl implements ISignCallbackService {

    @Autowired
    private IExtSouOrderService souOrderService;

    @Autowired
    private IExtSouOrderFileService orderFileService;

    @Override
    public void complete(Long businessId, String param, List<Fileupload> fileuploads) throws Exception {
        log.info("-------------------商务报价线上签署文件回调--------------------");

        try {
            ExtSouOrderFile signFile = orderFileService.getById(businessId);

            ExtSouOrder order = souOrderService.getById(signFile.getOrderId());

            //查询原件
            List<ExtSouOrderFile> originalFileList = orderFileService.lambdaQuery().eq(ExtSouOrderFile::getOrderId, order.getOrderId())
                    .eq(ExtSouOrderFile::getFileType, ExtSouFileConfigTypeEnum.BUS_BID.getCode())
                    .eq(ExtSouOrderFile::getExtSignStatus, BidSignStatusEnum.NOT_SIGN.getCode()).list();
            Map<String, ExtSouOrderFile> originalFileMap = new HashMap<>(50);
            originalFileList.stream().forEach(file -> {
                //原件-带后缀名
                originalFileMap.put(file.getOrderFileName(), file);
                //原件-不带后缀名
                originalFileMap.put(file.getOrderFileName().substring(0, file.getOrderFileName().lastIndexOf(".")), file);
            });

            //更新原件
            List<ExtSouOrderFile> originalFileForUpdateList = new ArrayList<>();

            List<ExtSouOrderFile> orderFileList = new ArrayList<>();
            fileuploads.stream().filter(fileupload -> !SrmConstant.SIGN_EXCLUDE_LIST.contains(fileupload.getFileSourceName())).forEach(f -> {
                ExtSouOrderFile orderFile = new ExtSouOrderFile();
                orderFile.setOrderId(order.getOrderId());
                orderFile.setVendorId(order.getVendorId());
                orderFile.setOrderFileId(IdGenrator.generate());
                orderFile.setOrderDocId(f.getFileuploadId());
                orderFile.setOrderFileName(f.getFileSourceName());
                orderFile.setSouFileConfigId(-1L);
                orderFile.setProjectId(order.getProjectId());
                orderFile.setRound(order.getRound());
                orderFile.setFileType(ExtSouFileConfigTypeEnum.BUS_BID.getCode());
                orderFile.setExtSignStatus(BidSignStatusEnum.SIGN.getCode());

                //处理原件
                //签署文件名
                String fileName = orderFile.getOrderFileName().substring(0, orderFile.getOrderFileName().lastIndexOf("."));
                //签署文件后缀
                String suffix = orderFile.getOrderFileName().substring(orderFile.getOrderFileName().lastIndexOf("."));
                //拿到原件
                ExtSouOrderFile originalFile = originalFileMap.get(fileName);
                if(!Objects.isNull(originalFile)) {
                    //修正签署文件名（文件带后缀情况）
                    orderFile.setOrderFileName(StringUtils.join(originalFile.getOrderFileName().substring(0, originalFile.getOrderFileName().lastIndexOf(".")), suffix));
                    orderFile.setOrderRemark(originalFile.getOrderRemark());
                    orderFile.setExtOrderStatus(originalFile.getExtOrderStatus());
                    orderFile.setExtPackageName(originalFile.getExtPackageName());
                    //删除原件
                    originalFile.setFileType(StringUtils.joinWith("_", originalFile.getFileType(), "D"));
                    originalFileForUpdateList.add(originalFile);
                }

                orderFile.formattingPackageName();
                orderFileList.add(orderFile);
            });

            orderFileService.saveBatch(orderFileList);
            if(CollectionUtils.isNotEmpty(originalFileForUpdateList)) {
                orderFileService.updateBatchById(originalFileForUpdateList);
            }
            log.info("businessId:{},param:{}",businessId,param);
        } catch (Exception e) {
            log.error("商务报价线上签署文件回调异常", e);
            throw new BaseException(e.getMessage());
        }

    }
}
