package com.midea.cloud.srm.sou.meiql.filecheck.service.impl;

import com.alibaba.nacos.common.utils.Objects;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.client.ExtFileCenterClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.oss.DownLoadResultDto;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.aihelper.FileCheckDto;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayFileDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerConfirmStatusEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouFileConfigTypeEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.filelink.entity.WordFileLink;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouOrderFileQueryDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.sou.bid.enums.ReviewFileTypeEnum;
import com.midea.cloud.srm.sou.meiql.bidnotices.util.XEasypdfUtil;
import com.midea.cloud.srm.sou.meiql.bidnotices.util.XwpfdUtils;
import com.midea.cloud.srm.sou.meiql.filecheck.service.FileCheckService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouOrderFileService;
import feign.Response;
import io.seata.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 备注
 * @author bs
 */
@Slf4j
@Service
public class FileCheckServiceImpl implements FileCheckService {

    @Autowired
    private IExtSouOrderFileService souOrderFileService;

    @Autowired
    private QlService qlService;

    @Autowired
    private ExtFileCenterClient extFileCenterClient;

    @Autowired
    private FileCenterClient fileCenterClient;
    @Autowired
    private PjSouClient pjSouClient;

    @Override
    public List<DownLoadResultDto> findOrderFile(FileCheckDto fileCheckDto) {
        //查询供应商报价附件列表
        List<ExtSouOrderFile> techOrderFiles = getExtSouOrderCheckFiles(fileCheckDto);
        //查询供应商报价附件列表 key wordFileId,value pdfFileId
        Map<Long, Long> wordFileLinkPdfFileMap = getOrderFileLinkPdfFileMap(techOrderFiles,fileCheckDto);

        //返回 oss地址
        return techOrderFiles.stream()
                .map(e -> {
                    Long fileuploadId = wordFileLinkPdfFileMap.containsKey(e.getOrderDocId()) ? wordFileLinkPdfFileMap.get(e.getOrderDocId()) : e.getOrderDocId();
                    DownLoadResultDto downLoadResultDto = extFileCenterClient.downloadTechFileSign(fileuploadId);
                    downLoadResultDto.setFileuploadId(e.getOrderDocId());
                    return downLoadResultDto;
                }).collect(Collectors.toList());
    }
    @Override
    public List<DownLoadResultDto> findAnswerFile(FileCheckDto fileCheckDto) {
        List<AnswerDTO> answerDTOS = qlService.queryByWrapper(QlWrappers.query(MqlType.ANSWER)
                .eq(AnswerDTO::getProjectId, fileCheckDto.getProjectId())
                .eq(AnswerDTO::getAnswerStatus, AnswerConfirmStatusEnum.COMFIRMED.getCode()), AnswerDTO.class);
        AssertUtils.isTrue(!answerDTOS.isEmpty(), "澄清文件不存在");
        List<AnswerVendorDTO> answerVendorDTOS = qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                .in(AnswerVendorDTO::getAnswerId,answerDTOS.stream().map(AnswerDTO::getAnswerId).collect(Collectors.toList()))
                .eq(AnswerVendorDTO::getConfirmStatus,AnswerConfirmStatusEnum.COMFIRMED.getCode())
                .isNotNull(AnswerVendorDTO::getReplayId),AnswerVendorDTO.class);
            AssertUtils.isTrue(!answerVendorDTOS.isEmpty(), "澄清文件不存在");

        //查询供应商澄清附件列表
        List<ReplayFileDTO> replayFileDtos = qlService.queryByWrapper(QlWrappers.query(TypeEnum.ReplayFile.getCode())
                .in(ReplayFileDTO::getReplayId,answerVendorDTOS.stream().map(AnswerVendorDTO::getReplayId).collect(Collectors.toList()))
                .in(ReplayFileDTO::getFileId,fileCheckDto.getOrderDocIds())
                .eq(ReplayFileDTO::getIsDelete, YesOrNo.NO.getValue()),ReplayFileDTO.class);
        replayFileDtos = replayFileDtos.stream()
                .filter(e-> ReviewFileTypeEnum.isValidReviewFileType(e.getFileName()))
                .collect(Collectors.toList());

        AssertUtils.isTrue(replayFileDtos.size() == fileCheckDto.getOrderDocIds().size(),"项目下文件不一致");

        //查询供应商报价附件列表 key wordId,value pdfFileId
        Map<Long, Long> wordFileLinkPdfFileMap = getReplayFileLinkPdfFileMap(replayFileDtos,fileCheckDto);

        //返回 oss地址
        return replayFileDtos.stream()
                .map(e -> {
                    Long fileuploadId = wordFileLinkPdfFileMap.containsKey(e.getFileId()) ? wordFileLinkPdfFileMap.get(e.getFileId()) : e.getFileId();
                    DownLoadResultDto downLoadResultDto = extFileCenterClient.downloadTechFileSign(fileuploadId);
                    downLoadResultDto.setFileuploadId(e.getFileId());
                    return downLoadResultDto;
                }).collect(Collectors.toList());
    }


    private Map<Long, Long> getOrderFileLinkPdfFileMap(List<ExtSouOrderFile> techOrderFiles, FileCheckDto fileCheckDto) {
        return techOrderFiles.parallelStream()
                .filter(e -> Arrays.asList(ReviewFileTypeEnum.DOCX.name(),ReviewFileTypeEnum.DOC.name()).contains(FilenameUtils.getExtension(e.getOrderFileName()).toUpperCase()))
                .map(extSouOrderFile -> {
                    try {
                        return wordFileLinkPdfFile(extSouOrderFile.getOrderDocId(),extSouOrderFile.getOrderFileName(),fileCheckDto);
                    } catch (Exception e) {
                        log.error("order wordLinkPdf error", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(WordFileLink::getWordFileLinkId, WordFileLink::getPdfFileLinkId));
    }
    private Map<Long, Long> getReplayFileLinkPdfFileMap(List<ReplayFileDTO> replayFileDTOs, FileCheckDto fileCheckDto) {
        return replayFileDTOs.parallelStream()
                .filter(replayFileDTO -> Arrays.asList(ReviewFileTypeEnum.DOCX.name(),ReviewFileTypeEnum.DOC.name()).contains(FilenameUtils.getExtension(replayFileDTO.getFileName()).toUpperCase()))
                .map(replayFileDTO -> {
                    try {
                        return wordFileLinkPdfFile(replayFileDTO.getFileId(),replayFileDTO.getFileName(), fileCheckDto);
                    } catch (Exception e) {
                        log.error("order wordLinkPdf error", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(WordFileLink::getWordFileLinkId, WordFileLink::getPdfFileLinkId));
    }

    private WordFileLink wordFileLinkPdfFile(Long fileId, String fileName, FileCheckDto fileCheckDto) throws Exception {
        WordFileLink wordFileLink = qlService.readByKey(MqlType.WORD_FILE_LINK, fileId, WordFileLink.class);
        if(Objects.nonNull(wordFileLink)){
            return wordFileLink;
        }
        //word 转pdf
       fileName = String.join(".",fileName.substring(0,fileName.lastIndexOf(".")),ReviewFileTypeEnum.PDF.name().toLowerCase());

        Fileupload upload = null;
        if(Objects.isNull(fileCheckDto.getIsNeedTotal())){
            upload = pjSouClient.wordTransPdf(fileId,fileName);
        }else{
            upload =  uploadPdfFile(XwpfdUtils.instance().covertFile(fileName, getFile(fileId), fileId));
        }
        wordFileLink = new WordFileLink();
        wordFileLink.setWordFileLinkId(fileId);
        wordFileLink.setPdfFileLinkId(upload.getFileuploadId());
        wordFileLink.setPdfFileLinkName(fileName);
        qlService.create(MqlType.WORD_FILE_LINK, Collections.singletonList(wordFileLink));
        return wordFileLink;
    }

    private Fileupload uploadPdfFile(MockMultipartFile pdfFile) {
        String uploadType = FileUploadType.DEF.name();
        String sourceType = "WEB_APP";
        String fileModular = "sou";
        String fileFunction = "aiReviewFile";
        String fileType = "PDF";

        return fileCenterClient.feignClientUpload(
                pdfFile,
                sourceType,
                uploadType,
                fileModular,
                fileFunction,
                fileType);
    }


    private List<ExtSouOrderFile> getExtSouOrderCheckFiles(FileCheckDto fileCheckDto) {
        //供应商报价附件表
        ApiExtSouOrderFileQueryDto queryDto = new ApiExtSouOrderFileQueryDto();
        queryDto.setProjectId(fileCheckDto.getProjectId());
        queryDto.setFileTypeList(Arrays.asList(ExtSouFileConfigTypeEnum.TECH_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_SOLUTION_BID.getCode(), ExtSouFileConfigTypeEnum.TECH_QUA_PERF.getCode(), ExtSouFileConfigTypeEnum.TECH_OTHER.getCode()));
        List<ExtSouOrderFile> extSouOrderFiles = souOrderFileService.listOrderFile(queryDto);

        List<ExtSouOrderFile> techOrderFiles = extSouOrderFiles.stream()
                .filter(e -> fileCheckDto.getOrderDocIds().contains(e.getOrderDocId()))
                .collect(Collectors.toList());
        AssertUtils.notEmpty(techOrderFiles,"项目下文件不存在");
        AssertUtils.isTrue(techOrderFiles.size() == fileCheckDto.getOrderDocIds().size(),"项目下文件不一致");
        return techOrderFiles;
    }


    private InputStream getFile(Long fileuploadId) throws Exception {
        Fileupload fileupload = new Fileupload();
        fileupload.setFileuploadId(fileuploadId);
        Response response1 = extFileCenterClient.downloadFileByParamForAnon(fileupload);
        return response1.body().asInputStream();
    }


    @Override
    public void pageFile(FileCheckDto fileCheckDto, HttpServletResponse response) throws Exception {
        Long fileuploadId = fileCheckDto.getOrderDocIds().get(0);
        WordFileLink wordFileLink = qlService.readByKey(MqlType.WORD_FILE_LINK, fileuploadId, WordFileLink.class);
        if(Objects.nonNull(wordFileLink)){
            fileuploadId = wordFileLink.getPdfFileLinkId();
        }
        Fileupload fileupload = extFileCenterClient.queryById(fileuploadId);
        String fileName = fileupload.getFileSourceName();
        AssertUtils.isTrue(StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(fileName),ReviewFileTypeEnum.PDF.name()),"参数有误");
        XEasypdfUtil.getPageFile(fileCheckDto.getPageNum(), fileName, getFile(fileuploadId),response);
    }
}
