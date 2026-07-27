package com.midea.cloud.srm.sou.inq.ext.service.impl;

import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.order.ApiInqSouOrderDTO;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.order.ApiInqSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.ext.excelhandler.ExtInqSouOrderItemImportConverter;
import com.midea.cloud.srm.sou.inq.ext.excelhandler.ExtInqSouOrderItemImportListener;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouOrderEventService;
import com.midea.cloud.srm.sou.sourcing.order.service.SouOrderEventService;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderJudgeHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouOrderEventServiceImpl implements ExtInqSouOrderEventService {

    @Autowired
    private ExtInqSouOrderItemImportConverter extInqSouOrderItemImportConverter;
    @Autowired
    private SouOrderEventService souOrderEventService;

    @Override
    public Map<String, Object> importOrderItems(long projectId, Integer round, long vendorId, boolean isBuyer, MultipartFile file, Fileupload fileupload) {
        // 1: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(SouTypeEnum.inq.name(), ApiSouOrderJudgeHandler.class).judgeOrderAuth(projectId, vendorId, isBuyer, SouTypeEnum.inq.name());
        // 2: 导入数据，并校验数据
        ExtInqSouOrderItemImportListener listener = extInqSouOrderItemImportConverter.convert(projectId, round, vendorId, isBuyer, file, fileupload);
        if (listener.isHasError()) {
            return ImportStatus.importError(listener.getErrFileDocId(), file.getOriginalFilename());
        }
        // 3: 保存数据
        ApiInqSouOrderDTO inqOrderDTO = new ApiInqSouOrderDTO(); {
            if (listener.getExistInqOrder() != null) {
                BeanUtils.copyProperties(listener.getExistInqOrder(), inqOrderDTO);
            }
            inqOrderDTO.setOrderItemList(listener.getResultList());
            inqOrderDTO.setVendorId(vendorId);
            inqOrderDTO.setProjectId(projectId);
            inqOrderDTO.setOrderNoGenerateCode(SequenceCodeConstant.SOU.SEQ_INQ_ORDER_NO);
            inqOrderDTO.setIsTempSave(true);
            inqOrderDTO.getOrderItemList().forEach(e -> e.setOrderNotaxPrice(e.getOrderNotaxPrice().setScale(2, RoundingMode.HALF_UP)));
        }
        souOrderEventService.editOrder(SouObjectXUtil.convertTargetObj(inqOrderDTO, ApiSouOrderDTO.class), SouTypeEnum.inq.name());

        return null;
    }

}
