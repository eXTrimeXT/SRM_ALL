package com.midea.cloud.srm.biz.pj.base.ocr.service;

import com.midea.cloud.srm.model.pj.base.ocr.dto.CompanyResponseDTO;
import com.midea.cloud.srm.model.pj.base.ocr.dto.IdCardBackDTO;
import com.midea.cloud.srm.model.pj.base.ocr.dto.PersonalInfoDTO;

/**
 * @author huangbf3
 */
public interface OcrService {
    /**
     * 备注
     * @param fileuploadId
     * @return
     */
    CompanyResponseDTO recognizeLicence(Long fileuploadId);

    /**
     * 备注
     * @param fileuploadId
     * @return
     */
    PersonalInfoDTO recognizeIdCardFront(Long fileuploadId);

    /**
     * 备注
     * @param fileuploadId
     * @return
     */
    IdCardBackDTO recognizeIdCardBack(Long fileuploadId);
}
