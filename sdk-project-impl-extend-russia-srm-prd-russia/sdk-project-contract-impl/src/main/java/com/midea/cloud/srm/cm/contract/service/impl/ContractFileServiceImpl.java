package com.midea.cloud.srm.cm.contract.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.utils.PdfUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;

import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.srm.cm.contract.service.IContractFileService;
import com.midea.cloud.srm.cm.contract.utils.ContractFileUtil;
import com.midea.cloud.srm.cm.contract.utils.EggClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.cm.annex.Annex;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.contract.dto.ContractEggQueryDto;
import com.midea.cloud.srm.model.contract.dto.ContractHeadExt;
import com.midea.cloud.srm.model.contract.enums.ContractAgreementAttachmentType;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;

import feign.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author 100014336 ganyh19
 */
@Service
@Slf4j
public class ContractFileServiceImpl implements IContractFileService {

    @Autowired
    private FileCenterClient fileCenterClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private EggClient eggClient;

    @Override
    @SneakyThrows(Exception.class)
    public void makeHtmlFormalPdf(ContractHeadExt contractHeadExt) {
//        log.info(contractHead.getContent());
        if(ObjectUtil.isNotEmpty(contractHeadExt.getExtContentFinal())){
            String fileName = "合同正文.pdf";

            InputStream in = eggClient.upload(contractHeadExt.getExtContentFinal());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfUtil.removeBlankPdfPages(in, byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            PdfUtil.addPageCount(byteArrayInputStream, byteArrayOutputStream);
//            Fileupload fileupload1= fileCenterClient.feignClientUpload(ContractFileUtil.getPdfFile(contractHeadExt.getExtContentFinal(),fileName),"WEB_APP","PAAS_MINIO","contract","contractManagement","pdf") ;
            Fileupload fileupload1 = fileCenterClient.feignClientUpload(ContractFileUtil.getPdfUploadFile(byteArrayOutputStream.toByteArray(),fileName),"WEB_APP","PAAS_MINIO","contract","contractManagement","pdf");
            log.info("fileId:"+fileupload1.getFileuploadId());
            List<Record> records = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.Annex.getType()).eq("contractHeadId",contractHeadExt.getContractHeadId())
                    .and(sc->sc.eq("fileSourceName",fileName)), Record.class);
            Annex annex ;
            if(CollUtil.isEmpty(records)){
                annex = new Annex();
                annex.setFileType(ContractAgreementAttachmentType.CONTRACT_AGREEMENT.getCode());
                annex.setFileuploadId(fileupload1.getFileuploadId());
                annex.setContractHeadId(contractHeadExt.getContractHeadId());
                annex.setFileSourceName(fileupload1.getFileSourceName());
                qlService.create(ContractMqlSchemaType.Annex.getType(), Collections.singletonList(annex));
            } else {
               annex = OpenApiUtil.toValue(records.get(0),Annex.class);
               annex.setFileuploadId(fileupload1.getFileuploadId());
               qlService.update(ContractMqlSchemaType.Annex.getType(), Collections.singletonList(annex));
            }

        }
    }
}
