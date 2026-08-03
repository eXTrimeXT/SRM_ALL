package com.midea.cloud.srm.biz.pj.rocketmq.consumer;

import com.gw.rocketmq.consumer.properties.ConsumerProperties;
import com.midea.cloud.srm.biz.pj.rocketmq.base.BaseOrderlyConsumerTemplate;
import com.midea.cloud.srm.biz.pj.sunhonestyinfo.service.ExamRecordService;
import com.midea.cloud.srm.model.pj.sunhonesty.dto.StudentTrainStatusMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author GW00086630
 */
@Slf4j
public class SrmRocketMqConsumer extends BaseOrderlyConsumerTemplate<StudentTrainStatusMsgDto> {

    @Autowired
    ConsumerProperties consumerProperties;
    @Autowired
    ExamRecordService examRecordService;


    @Value("${rocketmq.consumer.sunHonestyMqConsumer.namesrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.consumer.sunHonestyMqConsumer.topic}")
    private String topic;

    @Value("${rocketmq.consumer.sunHonestyMqConsumer.groupName}")
    private String groupName;


    @Override
    public ConsumerProperties getConsumerProperties() {
        consumerProperties.setNamesrvAddr(namesrvAddr);
        consumerProperties.setTopic(topic);
        consumerProperties.setGroupName(groupName);
        return consumerProperties;
    }


    @Override
    public StudentTrainStatusMsgDto doMapping(MessageExt var1) throws Exception {
        return convertToEntity(var1, StudentTrainStatusMsgDto.class);
    }




    @Override
    public ConsumeOrderlyStatus doMappingThenSomething(StudentTrainStatusMsgDto studentTrainStatusMsgDto) {
        examRecordService.addExamRecord(studentTrainStatusMsgDto);
        return ConsumeOrderlyStatus.SUCCESS;
    }




}
