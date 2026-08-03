package com.midea.cloud.srm.biz.pj.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.gw.rocketmq.consumer.properties.ConsumerProperties;
import com.gwm.open.sdk.ClientConfig;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.biz.pj.common.OpenClientConstant;
import com.midea.cloud.srm.biz.pj.file.anon.service.FileAnonService;
import com.midea.cloud.srm.biz.pj.rocketmq.base.BaseOrderlyConsumerTemplate;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.feign.pj.sou.SouSignClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.aihelper.BidReviewDto;
import com.midea.cloud.srm.model.pj.aihelper.FileCheckDto;
import com.midea.cloud.srm.model.pj.enums.BidReviewEnum;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GW00086630
 */
@Slf4j
public class SrmProjectReviewConsumer extends BaseOrderlyConsumerTemplate<BidReviewDto> {

    private static final String MSG_KEY = "msg";
    private static final String MSG_ERROR = "文件推送失败，缺少必要的评审项。";


    @Autowired
    ConsumerProperties consumerProperties;



    @Value("${rocketmq.consumer.srmProjectReviewConsumer.namesrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.consumer.srmProjectReviewConsumer.topic}")
    private String topic;

    @Value("${rocketmq.consumer.srmProjectReviewConsumer.groupName}")
    private String groupName;

    @Value("${gwm.gwkb.appkey}")
    private String appKey;
    @Value("${gwm.gwkb.secret}")
    private String secret;

    @Value("${gwm.gwkb.bidReviewPush-url}")
    private String bidReviewPushUrl;

    @Autowired
    private SouSignClient souSignClient;
    @Autowired
    private FileAnonService fileAnonService;
    @Autowired
    private QlOpenClient qlOpenClient;

    @Override
    public ConsumerProperties getConsumerProperties() {
        consumerProperties.setNamesrvAddr(namesrvAddr);
        consumerProperties.setTopic(topic);
        consumerProperties.setGroupName(groupName);
        return consumerProperties;
    }


    @Override
    public BidReviewDto doMapping(MessageExt var1) throws Exception {
        return convertToEntity(var1, BidReviewDto.class);
    }




    @Override
    public ConsumeOrderlyStatus doMappingThenSomething(BidReviewDto bidReviewDto) {
        log.info("SrmProjectReview push start,{}",bidReviewDto.getProjectId());
        //文件转换超时或失败 不影响评标数据推送
        List<Fileupload> fileuploads = doFileData(bidReviewDto);

        fileuploads.stream().forEach(item -> {
            Fileupload fileupload = null;
            try {
                fileupload = fileAnonService.wordToPdf(item.getFileuploadId(), String.join(".",FilenameUtils.getBaseName(item.getFileSourceName()),"pdf"));
            } catch (Exception e) {
                log.error("智能评标数据推送转换文件异常",e);
            }
            try {
                Long id = Objects.isNull(fileupload) ? item.getFileuploadId() : fileupload.getFileuploadId();
                String name = Objects.isNull(fileupload) ? item.getFileSourceName() : fileupload.getFileSourceName();
                saveData(item, id, name);
            } catch (Exception e) {
            }
        });
        try {
            bidReviewPush(bidReviewDto);
        } catch (Exception e) {
        }
        log.info("SrmProjectReview push end,{}",bidReviewDto.getProjectId());
        return ConsumeOrderlyStatus.SUCCESS;

    }

    private void saveData(Fileupload item, Long fileuploadId, String fileSourceName) {
        List<Record> recordList = new ArrayList<>();
        Record record = new Record();
        record.put("wordFileLinkId", item.getFileuploadId());
        record.put("pdfFileLinkId", fileuploadId);
        record.put("pdfFileLinkName", fileSourceName);
        recordList.add(record);
        qlOpenClient.create(ContextPath.SOU, MqlType.WORD_FILE_LINK, recordList);
    }

    private List<Fileupload> doFileData(BidReviewDto bidReviewDto) {
        FileCheckDto fileCheckDto = new FileCheckDto();
        fileCheckDto.setProjectId(bidReviewDto.getProjectId());

        Map<String, List<Long>> fileTypeGroup = bidReviewDto.getCompanyList().stream()
                .flatMap(company -> company.getFileList().stream())
                .collect(Collectors.groupingBy(BidReviewDto.File::getFileType, Collectors.mapping(BidReviewDto.File::getFileId, Collectors.toList())));
        if(fileTypeGroup.containsKey(BidReviewEnum.TECH.name())){
            fileCheckDto.setOrderDocIds(fileTypeGroup.get(BidReviewEnum.TECH.name()));
            return souSignClient.findOrderTransFile(fileCheckDto);
        }else if(fileTypeGroup.containsKey(BidReviewEnum.ANSWER.name())){
            fileCheckDto.setOrderDocIds(fileTypeGroup.get(BidReviewEnum.ANSWER.name()));
            return souSignClient.findAnswerTransFile(fileCheckDto);
        }
        return Collections.emptyList();
    }


    private void bidReviewPush(BidReviewDto bidReviewDto) {
        OpenClient openClient = new OpenClient(appKey,secret);
        // 设置请求超时时间为50秒（单位：毫秒）
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setConnectTimeout(50000);
        clientConfig.setConnectionRequestTimeout(50000);
        openClient.setClientConfig(clientConfig);
        openClient.setClientConfig(clientConfig);
        String resultStr = openClient.sendHttpPost(bidReviewPushUrl, JSONObject.toJSONString(bidReviewDto),"application/json");
        log.info("bidReviewPush projectId:{} result:{}",bidReviewDto.getProjectId(),resultStr);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (!Objects.equals(OpenClientConstant.CODE_SUCCESS,result.getString(OpenClientConstant.CODE_KEY))
                && !Objects.equals(MSG_ERROR,result.getString(MSG_KEY))) {
            throw new BaseException("智能评标数据推送接口" + result.get("msg"));
        }

    }


}
