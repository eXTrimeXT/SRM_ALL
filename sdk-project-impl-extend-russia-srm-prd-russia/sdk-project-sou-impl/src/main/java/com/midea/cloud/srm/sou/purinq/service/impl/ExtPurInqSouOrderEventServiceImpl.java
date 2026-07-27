package com.midea.cloud.srm.sou.purinq.service.impl;

import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.order.ApiPurInqSouOrderDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.purinq.excelhandler.ExtPurInqSouOrderItemImportConverter;
import com.midea.cloud.srm.sou.purinq.excelhandler.ExtPurInqSouOrderItemImportListener;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouOrderEventService;
import com.midea.cloud.srm.sou.sourcing.order.service.SouOrderEventService;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderJudgeHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouOrderEventServiceImpl implements ExtPurInqSouOrderEventService {

    @Autowired
    private ExtPurInqSouOrderItemImportConverter extPurInqSouOrderItemImportConverter;
    @Autowired
    private SouOrderEventService souOrderEventService;

    @Override
    public Map<String, Object>


    importOrderItems(long projectId, Integer round, long vendorId, boolean isBuyer, MultipartFile file, Fileupload fileupload) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(ExtPurInqSouTypeEnum.ext_pur_inq.name(), ApiSouOrderJudgeHandler.class).judgeOrderAuth(projectId, vendorId, isBuyer, ExtPurInqSouTypeEnum.ext_pur_inq.name());
        // 2: 导入数据，并校验数据
        ExtPurInqSouOrderItemImportListener listener = extPurInqSouOrderItemImportConverter.convert(projectId, round, vendorId, isBuyer, file, fileupload);
        if (listener.isHasError()) {
            return ImportStatus.importError(listener.getErrFileDocId(), file.getOriginalFilename());
        }
        // 3: 保存数据
        ApiPurInqSouOrderDTO inqOrderDTO = new ApiPurInqSouOrderDTO(); {
            if (listener.getExistInqOrder() != null) {
                BeanUtils.copyProperties(listener.getExistInqOrder(), inqOrderDTO);
            }
            inqOrderDTO.setOrderItemList(listener.getResultList());
            inqOrderDTO.setVendorId(vendorId);
            inqOrderDTO.setProjectId(projectId);
            inqOrderDTO.setOrderNoGenerateCode(ExtPurInqSouProject.EXT_SEQ_SOU_PURINQ_ORDER_NO);
        }
        souOrderEventService.editOrder(SouObjectXUtil.convertTargetObj(inqOrderDTO, ApiSouOrderDTO.class), ExtPurInqSouTypeEnum.ext_pur_inq.name());

        return null;
    }

}
