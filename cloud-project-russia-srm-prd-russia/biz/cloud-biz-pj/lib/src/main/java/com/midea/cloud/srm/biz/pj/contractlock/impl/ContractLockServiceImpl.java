package com.midea.cloud.srm.biz.pj.contractlock.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.redis.RSALockUtil;
import com.midea.cloud.srm.biz.pj.changchengapi.sign.service.ISccPjSignOrderService;
import com.midea.cloud.srm.biz.pj.common.PjInterfaceLogUtils;
import com.midea.cloud.srm.biz.pj.contractlock.ContractLockService;
import com.midea.cloud.srm.biz.pj.contractlock.SccPjSignOrderFileService;
import com.midea.cloud.srm.biz.pj.contractlock.SccPjSignOrderService;
import com.midea.cloud.srm.biz.pj.contractlock.enums.OrderFileSignTypeEnum;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrderFile;
import com.midea.cloud.srm.model.pj.sup.company.entity.AuthenticationScreen;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import net.qiyuesuo.sdk.SDKClient;
import net.qiyuesuo.sdk.api.CompanyService;
import net.qiyuesuo.sdk.api.ContractService;
import net.qiyuesuo.sdk.api.SealService;
import net.qiyuesuo.sdk.api.SignService;
import net.qiyuesuo.sdk.bean.company.Company;
import net.qiyuesuo.sdk.bean.company.CompanyAuthNoticeRequset;
import net.qiyuesuo.sdk.bean.company.TenantType;
import net.qiyuesuo.sdk.bean.contract.*;
import net.qiyuesuo.sdk.bean.seal.Seal;
import net.qiyuesuo.sdk.bean.sign.PreSignUrlRequest;
import net.qiyuesuo.sdk.bean.sign.SignUrlRequest;
import net.qiyuesuo.sdk.bean.sign.Signatory;
import net.qiyuesuo.sdk.common.exception.PrivateAppException;
import net.qiyuesuo.sdk.common.utils.IOUtils;
import net.qiyuesuo.sdk.impl.CompanyServiceImpl;
import net.qiyuesuo.sdk.impl.ContractServiceImpl;
import net.qiyuesuo.sdk.impl.SealServiceImpl;
import net.qiyuesuo.sdk.impl.SignServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * @author huangbf3
 */
@Slf4j
@Service
public class ContractLockServiceImpl implements ContractLockService {

    @Value("${gwm.url.accessKey}")
    private String accessKey;

    @Value("${gwm.url.accessSecret}")
    private String accessSecret;

    @Value("${gwm.url.categoryId}")
    private Long categoryId;

    @Value("${gwm.url.contractLock-createbyfiles}")
    private String createbyfilesUrl;

    @Autowired
    FileCenterClient fileCenterClient;

    @Autowired
    private RSALockUtil rsaLockUtil;

    @Resource
    private QlOpenClient qlOpenClient;

    @Autowired
    private SccPjSignOrderService sccPjSignOrderService;

    @Autowired
    private SccPjSignOrderFileService sccPjSignOrderFileService;

    @Autowired
    private ISccPjSignOrderService signOrderService;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private SupplierClient supplierClient;

    private static final String PERSONAL = "PERSONAL";

    /**
     * 契约锁-多文件创建合同文档-创建合同-获取签署页面 接口组合
     * 入参 业务单据类型 、业务单据ID、签署方名称、附件id集合 等
     **/
    @Override
    public String contractSigning(JSONObject jsonObject) {

//        业务单据类型
        String orderType = jsonObject.getString("orderType");
//        业务单据ID
        Long orderId = jsonObject.getLong("orderId");
//        合同文件名称
        String title = jsonObject.getString("title");

//        是否立即发起合同
        String send = jsonObject.getString("send");

        JSONArray fileIdListJsonArray = jsonObject.getJSONArray("fileIdList");
//        附件id
        List<Long> fileIdList = fileIdListJsonArray.toJavaList(Long.class);

        Assert.isTrue(orderType != null, "业务单据类型不能为空");
        Assert.isTrue(orderId != null, "业务单据id不能为空");

        // 签署方集合信息
        JSONArray signatoryJsonArray = jsonObject.getJSONArray("SignatoryList");
        // 签署方信息
        List<Signatory> signatoryList = signatoryJsonArray.toJavaList(Signatory.class);
        for (Signatory sign : signatoryList) {
            performActions(sign,orderType);

            sign.setTenantType(TenantType.COMPANY);
        }

//        根据附件id集合下载的附件集合
        List<Fileupload> fileUploadList = fileCenterClient.find(fileIdList);

        List<Long> documents = new ArrayList<>();
        Map<Long, Long> idMap = new HashMap(50);
        Map<Long, String> fileNameMap = new HashMap<>(50);
        for (int i = 0; i < fileUploadList.size(); i++) {
            List<Long> fileList = new ArrayList<>();
            Long fileId = fileUploadList.get(i).getFileuploadId();
            String fileName = fileUploadList.get(i).getFileSourceName();
            fileList.add(fileId);
            // 1、多文件创建合同文档接口
            Long documentId = createbyfiles(fileList, fileName);
            documents.add(documentId);
            idMap.put(fileId, documentId);
            fileNameMap.put(fileId, fileName);
        }

        CreateContractRequest createContractRequest = new CreateContractRequest();
        createContractRequest.setSubject(title);
        createContractRequest.setDocuments(documents);
        createContractRequest.setSignatories(signatoryList);
//        业务分类ID
        createContractRequest.setCategoryId(categoryId);
        String zeroText = "0";
        if(zeroText.equals(send)){
            createContractRequest.setSend(false);
        }
//        创建合同接口
        Long contractId = createContractByCategory2(createContractRequest);

//        3、合同签署页面
        String signUrl = "";
        if (OrderFileSignTypeEnum.TYPE_BID_TECH.getCode().equals(orderType)){
            signUrl = signUrl(contractId,signatoryList.get(0).getTenantName(),signatoryList.get(0).getContact(),null,TenantType.COMPANY);
        }else{
            signUrl = signUrl(contractId, signatoryList.get(0).getTenantName());

        }

        SccPjSignOrder sccPjSignOrder = new SccPjSignOrder();
//        业务单据类型
        sccPjSignOrder.setOrderType(orderType);
//        业务单据ID
        sccPjSignOrder.setSrmOrderId(orderId);
//        签署单id【合同id,这个是否是要等到到契约锁下载附件后才能得到附件id】
        sccPjSignOrder.setSignId(contractId);
//        盖章状态
        sccPjSignOrder.setSignStatus(null);
        Long signOrderId  = IdGenrator.generate();
        sccPjSignOrder.setSignOrderId(signOrderId);
        sccPjSignOrderService.save(sccPjSignOrder);

        List<SccPjSignOrderFile> sccPjSignOrderFiles = new ArrayList<>();
        SccPjSignOrderFile sccPjSignOrderFile;
        for (Long id : fileIdList
        ) {
            sccPjSignOrderFile = new SccPjSignOrderFile();
            sccPjSignOrderFile.setSignOrderId(signOrderId);
            sccPjSignOrderFile.setOrderFileId(id);
            sccPjSignOrderFile.setFromType("SRM");
            sccPjSignOrderFile.setSignFileId(idMap.get(id));
            sccPjSignOrderFile.setFilename(fileNameMap.get(id));
            sccPjSignOrderFiles.add(sccPjSignOrderFile);
        }
        sccPjSignOrderFileService.saveBatch(sccPjSignOrderFiles);
        return signUrl;

    }
    void performActions(Signatory sign , String orderType) {
        if (!OrderFileSignTypeEnum.TYPE_BID_TECH.getCode().equals(orderType)){
            return;
        }
        // 创建公司盖章的动作
        Action action1 = new Action();
        action1.setType(ActionType.CORPORATE);
        action1.setSerialNo(1);
        action1.setActionOperators(Collections.singletonList(new Operator(sign.getTenantName(), sign.getContact())));

        // 创建个人签字的动作
        Action action2 = new Action();
        action2.setType(ActionType.PERSONAL);
        action2.setSerialNo(1);
        action2.setActionOperators(Collections.singletonList(new Operator(sign.getTenantName(), sign.getContact())));

        // 将动作添加到sign对象中
        sign.addAction(action1);
        sign.addAction(action2);
    }
    /**
     * 契约锁-多文件创建合同文档接口
     * 入参 附件id集合 、多文件创建合同文档的文档名
     **/
    @Override
    public Long createbyfiles(List<Long> fileIdList, String title) {

        Assert.isTrue(title != null, "合同文件名称不能为空");
        Assert.isTrue(fileIdList != null && fileIdList.size() >= 0, "文件id不能为空");
        List<InputStream> inputstreams = new ArrayList<>();
        List<WaterMarkContent> waterMarkContentList = new ArrayList<WaterMarkContent>();
        try {
            for (Long fileId : fileIdList) {
                Fileupload fileupload = new Fileupload();
                String fileKey = rsaLockUtil.encrypt(fileId.toString());
                fileupload.setFileKey(fileKey);
                Response download = fileCenterClient.downloadFileByParam(fileupload);
                Assert.notNull(download.body(), "附件下载失败");
                InputStream inputStream = download.body().asInputStream();
                inputstreams.add(inputStream);
            }
        } catch (Exception e) {
            log.error("根据附件id获取文件失败", e.getMessage());
            throw new BaseException("根据附件id获取文件失败" + e.getMessage());
        }

        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        try {
            return contractService.createDocument(inputstreams, title, waterMarkContentList);
        } catch (Exception e) {
            log.error("调契约锁多文件创建合同文档接口失败", e.getMessage());
            throw new BaseException("调契约锁多文件创建合同文档接口失败" + e.getMessage());
        }

    }

    /**
     * 契约锁-契约锁创建合同
     **/
    @Override
    public Long createContractByCategory(List<Long> documents, String subject) {

        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        CreateContractRequest createContractRequest = new CreateContractRequest();

        //合同名称  必填
        createContractRequest.setSubject(subject);
//        文档ID的集合（一个文档只能属于一个合同） 【非必填，不过接口校验了要填，文档id是指什么？】
        createContractRequest.setDocuments(documents);
//        业务分类ID
        createContractRequest.setCategoryId(categoryId);
        Long contractId;
        String msg = ProcessStatusEnum.COMPLETED.getName();
        try {
            log.info("创建合同入参文件id===" + documents.get(0));
            log.info("创建合同入参合同名称===" + subject);

            contractId = contractService.createContractByCategory(createContractRequest);
            msg = "合同ID" + contractId;
            log.info("创建合同返回的合同文件id===" + contractId);
            return contractId;
        } catch (Exception e) {
            log.error("调契约锁创建合同接口失败", e.getMessage());
            msg = "接口异常" + e.getMessage();
            throw new BaseException("调契约锁创建合同接口失败" + e.getMessage());
        } finally {
            PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.QYS_CONTRACT_CREATE, JSON.toJSONString(createContractRequest), msg);
        }
    }

    /***
     * 契约锁创建合同传整体入参
     * **/
    @Override
    public Long createContractByCategory2(CreateContractRequest createContractRequest) {
        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        Long contractId;
        String msg = ProcessStatusEnum.COMPLETED.getName();
        try {
            contractId = contractService.createContractByCategory(createContractRequest);
            log.info("创建合同返回的合同文件id===" + contractId);
            msg = "合同ID" + contractId;
            return contractId;
        } catch (Exception e) {
            log.error("调契约锁创建合同接口失败", e.getMessage());
            msg = "接口异常" + e.getMessage();
            throw new BaseException("调契约锁创建合同接口失败" + e.getMessage());
        } finally {
            PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.QYS_CONTRACT_CREATE, JSON.toJSONString(createContractRequest), msg);
        }

    }

    /**
     * 契约锁-合同签署页面接口
     **/
    @Override
    public String signUrl(Long contractId, String tenantName) {
        return  this.signUrl(contractId, tenantName, null, null, TenantType.COMPANY);

    }

    /**
     * 契约锁-合同签署页面接口
     **/
    @Override
    public String signUrl(Long contractId, String tenantName, String contact,
           String receiverNumber,TenantType tenantType) {

        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        SignService signService = new SignServiceImpl(client);

        try {
            SignUrlRequest request = new SignUrlRequest();
//            合同ID
            request.setContractId(contractId);
//            签署方类型
            request.setTenantType(tenantType);
//            签署方名称
            request.setTenantName(tenantName);

            request.setContact(contact);
            request.setReceiverNumber(receiverNumber);
            request.setExpireTime(1800);
            String signurl = signService.signUrl(request);
            log.info("合同签署页面接口入参合同ID===" + contractId);
            log.info("合同签署页面接口入参签署方名称===" + tenantName);
            log.info("=======签署地址========" + signurl);
            return signurl;
        } catch (Exception e) {
            log.error("调契约锁合同签署页面接口失败", e.getMessage());
            throw new BaseException("调契约锁合同签署页面接口失败" + e.getMessage());
        }

    }

    /**
     * 契约锁-锲约锁认证状态查询
     **/
    @Override
    public String getComnpanyAuthStatus(BigInteger companyId) {

        Assert.isTrue(companyId != null, "供应商ID不能为空");
        AuthenticationScreen authenticationScreen = qlOpenClient.read(ContextPath.SUP, "AuthenticationScreen", companyId, AuthenticationScreen.class);
        Assert.isTrue(authenticationScreen != null, "供应商不存在");
        // String 认证状态：UNSUBMIT（未提交认证）,APPLIED（认证申请中）,PASSED（认证通过）,REJECTED（认证拒绝）
        String companyName = authenticationScreen.getCompanyName();
        // 数据库中认证状态只存 Y和N
        String contractVerification = authenticationScreen.getContractVerification();
        String passedText = "PASSED";
        if(YesOrNo.Y.name().equals(contractVerification)){
             return  passedText;
        }else{
            SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
            CompanyService companyService = new CompanyServiceImpl(client);
            try {
                /* 契约锁接口查询返回的认证状态 */
                String companyStatus = companyService.getComnpanyAuthStatus(companyName);
                String statu = "N";
                if(passedText.equals(companyStatus)){
                    statu = "Y";
                }
                authenticationScreen.setContractVerification(companyStatus);

                QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("AuthenticationScreen")
                        .eq(AuthenticationScreen::getCompanyId, authenticationScreen.getCompanyId())
                        .set(AuthenticationScreen::getContractVerification, statu);
                qlOpenClient.update(ContextPath.SUP, wrapper);
                log.info("=======锲约锁认证状态========" + companyStatus);
                log.info("=======锲约锁认证状态========" + companyStatus);
                return companyStatus;
            } catch (Exception e) {
                log.error("调契约锁合同签署页面接口失败", e.getMessage());
                throw new BaseException("调契约锁锲约锁认证状态查询接口失败" + e.getMessage());
            }
        }

    }

    /**
     * 契约锁-锲约锁下载合同
     **/
    @Override
    public List<Fileupload> download(Long signId) {
        /**
         * 根据业务单据id，查询签署业务单据关系表，得到合同id，然后根据合同id查询契约锁下载附件
         * **/
        SccPjSignOrder signOrder = signOrderService.lambdaQuery().eq(SccPjSignOrder::getSignId, signId).one();
        log.info("契约锁下载附件的signOrder===" + signOrder.toString());
        Long contractId = signOrder.getSignId();
        Long srmOrderId = signOrder.getSrmOrderId();
        log.info("契约锁下载附件的contractId===" + contractId);
        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        /*方法调用 */
        List<Fileupload> fileuploads = new ArrayList<>();
        try {

            String zipFilePath = Paths.get("").toAbsolutePath().toString();
            log.info("契约锁存放下载zip附件的路径" + zipFilePath);
            OutputStream outputStream = new FileOutputStream(new File(srmOrderId + ".zip"));
//            从契约锁平台下载zip文件
            contractService.download(contractId, outputStream);
            IOUtils.safeClose(outputStream);
            log.info("契约锁解压zip文件r===");

//            解压文件
            List<String> fileNames = unzip(zipFilePath + "//" + srmOrderId + ".zip", zipFilePath + "//" + srmOrderId);
            /* 上传解压文件 */
            String descDir = zipFilePath + "//" + srmOrderId;
            log.info("=========解压后文件夹名==" + descDir);
            log.info("=========系统文件编码格式" + System.getProperties().get("file.encoding"));
            File file = new File(descDir);

            /* 如果这个路径是文件夹 */
            if (file.isDirectory()) {
                MultipartFile multipartFile;
                Fileupload fileupload;
                List<SccPjSignOrderFile> sccPjSignOrderFiles = new ArrayList<>();
                SccPjSignOrderFile sccPjSignOrderFile;
                // 获取路径下的所有文件
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    byte[] bytesArray = read(files[i]);

                    String result = "";
                    String qysfileName = "";
                    String fileNameut8 = new String(fileNames.get(i).getBytes(StandardCharsets.UTF_8), "UTF-8");
                    log.info("契约锁文件之一名===" + result);
                    log.info("契约锁文件之一名转码后===" + fileNameut8);
                    if ("签署摘要.pdf".equals(fileNames.get(i))) {
                        result = fileNames.get(i);
                    } else {
                        qysfileName = fileNameut8.substring(0, fileNameut8.length() - 4);
                        log.info("契约锁文件之一名去掉后缀后r===" + qysfileName);
                        int index = qysfileName.lastIndexOf(".");
                        if (index > 0) {
                            result = qysfileName.substring(0, index) + ".pdf";
                        } else {
                            result = qysfileName + ".pdf";
                        }

                    }
                    multipartFile = new MockMultipartFile("file", result, "", bytesArray);
                    multipartFile.getOriginalFilename();
                    String sourceType = "modifyFace";
                    String uploadType = FileUploadType.PAAS_MINIO.name();
                    String fileModular = "base";
                    String fileFunction = "profile";
                    String fileType = "pdf";
                    fileupload = fileCenterClient.feignClientUpload(multipartFile, sourceType, uploadType, fileModular, fileFunction, fileType);
                    fileuploads.add(fileupload);
                    log.info("========契约锁文件上传服务器文件id==========" + fileupload.getFileuploadId());

                    String fileName = fileupload.getFileSourceName();
                    Long signFileId = null;
                    List<SccPjSignOrderFile> orderFileList = sccPjSignOrderFileService.lambdaQuery()
                            .eq(SccPjSignOrderFile::getSignOrderId, signOrder.getSignOrderId())
                            .eq(SccPjSignOrderFile::getFilename, qysfileName)
                            .list();
                    if (orderFileList.size() > 0) {
                        signFileId = orderFileList.get(0).getSignFileId();
                    }
                    /* 在这里将用印文件维护进 签署业务附件关系表 */
                    sccPjSignOrderFile  = new SccPjSignOrderFile();
                    sccPjSignOrderFile.setSignOrderId(signOrder.getSignOrderId());
                    sccPjSignOrderFile.setOrderFileId(fileupload.getFileuploadId());
                    sccPjSignOrderFile.setFromType("changcheng");
                    sccPjSignOrderFile.setFilename(fileupload.getFileSourceName());
                    sccPjSignOrderFile.setSignFileId(signFileId);
                    sccPjSignOrderFiles.add(sccPjSignOrderFile);
                }
                sccPjSignOrderFileService.saveBatch(sccPjSignOrderFiles);
            }
            /* 将服务器上目录的zip文件及解压的文件清除 */
                deleteFileByIo(zipFilePath + "//" + srmOrderId + ".zip");
                deleteFileByIo(zipFilePath + "//" + srmOrderId);


        } catch (Exception e) {
            log.error("契约锁-锲约锁下载合同接口失败", e.getMessage());
            throw new BaseException("契约锁-锲约锁下载合同接口失败" + e.getMessage());
        }
        return fileuploads;
    }


    /**
     * 契约锁-锲约锁下载合同
     **/
    @Override
    public List<Fileupload> downloadNew(Long signId) {
        /**
         * 根据业务单据id，查询签署业务单据关系表，得到合同id，然后根据合同id查询契约锁下载附件
         * **/
        SccPjSignOrder signOrder = signOrderService.lambdaQuery().eq(SccPjSignOrder::getSignId, signId).one();
        log.info("契约锁下载附件的signOrder===" + signOrder.toString());
        Long contractId = signOrder.getSignId();
        log.info("契约锁下载附件的contractId===" + contractId);

        //查询附件
        List<SccPjSignOrderFile> orderFileList = sccPjSignOrderFileService.lambdaQuery()
                .eq(SccPjSignOrderFile::getSignOrderId, signOrder.getSignOrderId())
                .list();
        Map<String, SccPjSignOrderFile> orderFileMap = orderFileList.stream().collect(Collectors.toMap(k -> k.getFilename(), Function.identity(), (k1, k2) -> k2));


        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        /*方法调用 */
        List<Fileupload> fileuploads = new ArrayList<>();
        try {

            String zipFilePath = Paths.get("").toAbsolutePath().toString();
            log.info("契约锁存放下载zip附件的路径" + zipFilePath);
            String filePath = zipFilePath + "//" +IdGenrator.generate();
            String fileName = filePath + ".zip";
            OutputStream outputStream = new FileOutputStream(new File(fileName));
//            从契约锁平台下载zip文件
            contractService.download(contractId, outputStream);
            IOUtils.safeClose(outputStream);
            log.info("契约锁解压zip文件r===");

            BufferedInputStream zipInputStream = new BufferedInputStream(new FileInputStream(fileName));

            ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(zipInputStream);

            List<SccPjSignOrderFile> sccPjSignOrderFiles = new ArrayList<>();

            String ponit = ".";
            while (true) {
                ZipArchiveEntry zipArchiveEntry = zipArchiveInputStream.getNextZipEntry();
                if(Objects.isNull(zipArchiveEntry)) {
                    break;
                }
                if(zipArchiveEntry.getName().endsWith("/")) {
                    continue;
                }
                System.out.println(zipArchiveEntry.getName());

                ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();

                byte[] bytes = new byte[1024];
                int num;
                while ((num = zipArchiveInputStream.read(bytes, 0, bytes.length)) > 0) {
                    bufferedOutputStream.write(bytes, 0, num);
                }

                String sourceFileName = zipArchiveEntry.getName().substring(0, zipArchiveEntry.getName().lastIndexOf(ponit));

                System.out.println("契约锁原始文件名: "+ sourceFileName);
                Long signFileId = null;
                SccPjSignOrderFile signOrderFile = orderFileMap.get(sourceFileName);
                if(!orderFileMap.containsKey(sourceFileName)) {
                    sourceFileName = zipArchiveEntry.getName();
                } else {
                    signFileId = signOrderFile.getSignFileId();
                    sourceFileName = sourceFileName.substring(0, sourceFileName.lastIndexOf(ponit)) + zipArchiveEntry.getName().substring(zipArchiveEntry.getName().lastIndexOf(ponit));

                }

                System.out.println("契约锁原始文件名替换: "+ sourceFileName);

                MultipartFile multipartFile = new MockMultipartFile("file", sourceFileName, "", bufferedOutputStream.toByteArray());
                String sourceType = "modifyFace";
                String uploadType = FileUploadType.PAAS_MINIO.name();
                String fileModular = "base";
                String fileFunction = "profile";
                String fileType = "pdf";
                Fileupload fileupload = fileCenterClient.feignClientUpload(multipartFile, sourceType, uploadType, fileModular, fileFunction, fileType);
                fileuploads.add(fileupload);
                bufferedOutputStream.close();

                // 在这里将用印文件维护进 签署业务附件关系表
                SccPjSignOrderFile sccPjSignOrderFile = new SccPjSignOrderFile();
                sccPjSignOrderFile.setSignOrderId(signOrder.getSignOrderId());
                sccPjSignOrderFile.setOrderFileId(fileupload.getFileuploadId());
                sccPjSignOrderFile.setFromType("changcheng");
                sccPjSignOrderFile.setFilename(fileupload.getFileSourceName());
                sccPjSignOrderFile.setSignFileId(signFileId);
                sccPjSignOrderFiles.add(sccPjSignOrderFile);

            }

            sccPjSignOrderFileService.saveBatch(sccPjSignOrderFiles);


            // 将服务器上目录的zip文件及解压的文件清除
            deleteFileByIo(fileName);
            deleteFileByIo(filePath);

        } catch (Exception e) {
            log.error("契约锁-锲约锁下载合同接口失败", e.getMessage());
            throw new BaseException("契约锁-锲约锁下载合同接口失败" + e.getMessage());
        }
        return fileuploads;
    }

    /**
     * 契约锁-发起合同
     **/
    @Override
    public void send(Long contractId) {

        Assert.isTrue(contractId != null, "合同ID不能为空");

        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        SendContractRequest request = new SendContractRequest();
        request.setContractId(contractId);
        try {
            contractService.send(request);
        } catch (Exception e) {
            log.error("调契约锁发起合同接口失败", e.getMessage());
            throw new BaseException("调契约锁发起合同接口失败" + e.getMessage());

        }
        log.info("合同发起成功");

    }


    /**
     * 契约锁-预签署页面接口
     **/
    @Override
    public String preSignUrl(Long contractId) {

        Assert.isTrue(contractId != null, "合同ID不能为空");
        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        PreSignUrlRequest request = new PreSignUrlRequest();

        request.setContractId(contractId);
        request.setCanSend(false);

        String preSignUrl = null;
        try {
            preSignUrl = contractService.preSignUrl(request);
        } catch (PrivateAppException e) {
            log.error("调契约锁预签署页面接口失败", e.getMessage());
            throw new BaseException("调契约锁预签署页面接口失败" + e.getMessage());
        }
        log.info("预签署链接:{}", preSignUrl);

        return preSignUrl;
    }

    @Override
    public Long createContractByCategoryForContract(JSONObject jsonObject) {
        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        ContractService contractService = new ContractServiceImpl(client);
        // 获取数据
        //业务单据类型
        String orderType = jsonObject.getString("orderType");
        //业务单据ID
        Long orderId = jsonObject.getLong("orderId");
        JSONArray fileIdListJsonArray = jsonObject.getJSONArray("fileIdList");
        //附件id
        List<Long> fileIdList = fileIdListJsonArray.toJavaList(Long.class);
        // 根据附件id集合下载的附件集合
        List<Fileupload> fileUploadList = fileCenterClient.find(fileIdList);
        List<Long> documents = new ArrayList<>();
        Map<Long, Long> idMap = new HashMap<>(15);
        Map<Long, String> fileNameMap = new HashMap<>(15);
        for (int i = 0; i < fileUploadList.size(); i++) {
            List<Long> fileList = new ArrayList<>();
            Long fileId = fileUploadList.get(i).getFileuploadId();
            String fileName = fileUploadList.get(i).getFileSourceName();
            fileList.add(fileId);
            // 1、多文件创建合同文档接口
            Long documentId = createbyfiles(fileList, fileName);
            documents.add(documentId);
            idMap.put(fileId, documentId);
            fileNameMap.put(fileId, fileName);
        }

        JSONArray signatoryJsonArray = jsonObject.getJSONArray("signatoryList");
        // 签署方信息
        List<Signatory> signatoryList = signatoryJsonArray.toJavaList(Signatory.class);
        for (Signatory sign : signatoryList) {
            sign.setTenantType(TenantType.COMPANY);
            List<Action> actions = sign.getActions();
            actions.get(0).setType(ActionType.CORPORATE);
            actions.get(1).setType(ActionType.PERSONAL);
        }
        String subject = jsonObject.getString("contractName");

        CreateContractRequest createContractRequest = new CreateContractRequest();
        createContractRequest.setSignatories(signatoryList);
        // 合同名称  必填
        createContractRequest.setSubject(subject);
        // 文档ID的集合（一个文档只能属于一个合同） 【非必填，不过接口校验了要填，文档id是指什么？】
        createContractRequest.setDocuments(documents);
        // 业务分类ID
        createContractRequest.setCategoryId(categoryId);
        createContractRequest.setSend(false);

        Long contractId;
        String msg = ProcessStatusEnum.COMPLETED.getName();
        try {
            log.info("ForContract请求入参===" + JSONObject.toJSONString(createContractRequest));
            contractId = contractService.createContractByCategory(createContractRequest);
            msg = "合同ID" + contractId;
            log.info("ForContract创建合同返回的合同文件id===" + contractId);
            // 保存附件信息
            saveFileRelation(orderType,orderId,contractId,fileIdList,idMap,fileNameMap);
            return contractId;
        } catch (Exception e) {
            log.error("ForContract调契约锁创建合同接口失败{}", e.getMessage());
            msg = "接口异常" + e.getMessage();
            throw new BaseException("ForContract调契约锁创建合同接口失败" + e.getMessage());
        } finally {
            PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.QYS_CONTRACT_CREATE, JSON.toJSONString(createContractRequest), msg);
        }
    }

    @Override
    public Company sendCompanyAuthNotify(Long companyId)  {
//        CompanyInfo companyInfo = qlOpenClient.read(ContextPath.SUP,"CompanyInfo",companyId, CompanyInfo.class);
        CompanyInfo companyInfo = supplierClient.getCompanyInfo(companyId);
        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);
        CompanyService companyService = new CompanyServiceImpl(client);
        try {
            return companyService.sendCompanyAuthNotify(createAuthenticationRequest(companyInfo));
        } catch (PrivateAppException e) {
            log.error("通知用户企业认证接口调用失败{}"+e.getMessage());
            throw new BaseException("通知认证失败"+e.getMessage());
        }
    }

    private CompanyAuthNoticeRequset createAuthenticationRequest(CompanyInfo companyInfo) {
        CompanyAuthNoticeRequset request = new CompanyAuthNoticeRequset();
        request.setName(companyInfo.getCompanyName());
        request.setRegisterNo(getRegisterNo(companyInfo.getOverseasRelation(),companyInfo));
        LoginAppUser loginAppUser = rbacClient.getUser();
        request.setCharger(loginAppUser.getNickname());
        request.setMobile(loginAppUser.getPhone());
        request.setLegalPerson(companyInfo.getLegalPerson());
        return request;
    }



    private String getRegisterNo(String overseasRelation,CompanyInfo companyInfo) {
        if(PERSONAL.equals(overseasRelation)){
            return companyInfo.getIdNumber();
        } else {
            return companyInfo.getLcCode();
        }
    }


    private void saveFileRelation(String orderType,Long orderId,Long contractId,List<Long> fileIdList,Map<Long, Long> idMap,Map<Long, String> fileNameMap){
        // 23-12-02 保存附件信息
        SccPjSignOrder sccPjSignOrder = new SccPjSignOrder();
        // 业务单据类型
        sccPjSignOrder.setOrderType(orderType);
        // 业务单据ID
        sccPjSignOrder.setSrmOrderId(orderId);
        // 签署单id【合同id,这个是否是要等到到契约锁下载附件后才能得到附件id】
        sccPjSignOrder.setSignId(contractId);
        // 盖章状态
        sccPjSignOrder.setSignStatus(null);
        Long signOrderId = IdGenrator.generate();
        sccPjSignOrder.setSignOrderId(signOrderId);
        sccPjSignOrderService.save(sccPjSignOrder);

        List<SccPjSignOrderFile> sccPjSignOrderFiles = new ArrayList<>();
        SccPjSignOrderFile sccPjSignOrderFile;
        for (Long id : fileIdList
        ) {
            sccPjSignOrderFile = new SccPjSignOrderFile();
            sccPjSignOrderFile.setSignOrderId(signOrderId);
            sccPjSignOrderFile.setOrderFileId(id);
            sccPjSignOrderFile.setFromType("SRM");
            sccPjSignOrderFile.setSignFileId(idMap.get(id));
            sccPjSignOrderFile.setFilename(fileNameMap.get(id));
            sccPjSignOrderFiles.add(sccPjSignOrderFile);
        }
        sccPjSignOrderFileService.saveBatch(sccPjSignOrderFiles);
    }



    /**
     * * 解压
     * zipFilePath zip压缩文件路径
     * descDir     目录
     **/
    public List<String> unzip(String zipFilePath, String descDir) {
        log.info("契约锁解压zip文件zipFilePath" + zipFilePath);
        log.info("契约锁解压zip文件descDir" + descDir);
        List<String> fileNames = new ArrayList<>();
        try {
            File descFile = new File(descDir);
            if (!descFile.exists()) {
                descFile.mkdirs();
            }
            ZipFile zipFile = new ZipFile(zipFilePath, Charset.forName("GBK"));
            /*列出所有项，包含子目录和子目录内文件 */
            Enumeration<? extends ZipEntry> zs = zipFile.entries();
            while (zs.hasMoreElements()) {
                ZipEntry zipEntry = zs.nextElement();
                log.info(zipEntry.getName());
                fileNames.add(zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    InputStream in = zipFile.getInputStream(zipEntry);
                    OutputStream os = new FileOutputStream(descDir + File.separator + zipEntry.getName());
                    byte[] data = new byte[1024];
                    int len = -1;
                    while ((len = in.read(data)) != -1) {
                        os.write(data, 0, len);
                    }
                    os.flush();
                    os.close();
                    in.close();
                } else {
                    log.info("====解压文件名" + zipEntry.getName());
                    new File(descDir + File.separator + zipEntry.getName()).mkdirs();

                }
            }
            zipFile.close();
            log.info("解压完成...");
        } catch (IOException e) {
            log.error(zipFilePath + " 解压失败...", e);
        }
        return fileNames;
    }


    public static void deleteFileByIo(String filePath) {
        File file = new File(filePath);
        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                deleteFileByIo(temp.getAbsolutePath());
            }
        }
        file.delete();
    }


    public byte[] read(File file) {
        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                throw new IOException(
                        "EOF reached while trying to read the whole file");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }
        return buffer;
    }

    @Override
    public String sealDetailEmployees(Long sealId) {
        SDKClient client = new SDKClient(createbyfilesUrl, accessKey, accessSecret);

        SealService sealService = new SealServiceImpl(client);
        try {
            Seal seal = sealService.detail(sealId);
            if(!Objects.isNull(seal) && CollectionUtils.isNotEmpty(seal.getEmployees())) {
                return seal.getEmployees().stream().map(s -> s.getNumber()).collect(Collectors.joining(" "));
            }
            return "";
        } catch (Exception e) {
            log.error("sealDetailEmployees Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

}
