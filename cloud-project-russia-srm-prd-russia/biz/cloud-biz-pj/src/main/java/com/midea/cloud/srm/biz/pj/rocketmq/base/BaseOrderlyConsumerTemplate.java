package com.midea.cloud.srm.biz.pj.rocketmq.base;

import cn.hutool.json.JSONUtil;
import com.gw.rocketmq.consumer.enums.GWMessageModel;
import com.gw.rocketmq.consumer.services.OrderlyConsumerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author GW00086630
 */
@Slf4j
public abstract class BaseOrderlyConsumerTemplate<T> extends OrderlyConsumerTemplate {

    public BaseOrderlyConsumerTemplate() {
    }

    @Override
    public void consumer() {
        this.consumer(this.getConsumerProperties().getNamesrvAddr(),this.getConsumerProperties().getTopic(), this.getConsumerProperties().getTag());
    }

    public void consumer(String namesrvAddr,String topic, String tag) {
        try {
            log.info("Consumer is starting..!ConsumerConfig:{}", this.getConsumerProperties());
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(this.getConsumerProperties().getGroupName());
            consumer.setUnitName(namesrvAddr);
            consumer.setNamesrvAddr(namesrvAddr);
            consumer.setMessageModel(GWMessageModel.getMessageModel(this.getConsumerProperties().getMessageModel()));
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.setVipChannelEnabled(false);
            consumer.subscribe(topic, tag);

            //重试间隔 最大值30000
            consumer.setSuspendCurrentQueueTimeMillis(30000);
            //重试次数
//            consumer.setMaxReconsumeTimes(1200);


            consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
                log.info("{} Receive New Messages: {}", Thread.currentThread().getName(), msgs);
                //消息转换失败 默认消费成功
                T t;
                try {
                    t = doMapping(msgs.get(0));
                } catch (Exception e) {
                    return ConsumeOrderlyStatus.SUCCESS;
                }
                //消费逻辑
                try {
                    return doMappingThenSomething(t);
                } catch (Exception e) {
                    log.error(t.getClass().getName()+"业务消费失败",e);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            });
            consumer.start();
            log.info("Consumer Started.");
        } catch (MQClientException var4) {
            log.error("Consumer Exception:", var4);
        }

    }

    /**
     * 消息转化
     * @param var1 var1
     * @return T class
     * @throws Exception Exception
     */
    public abstract T doMapping(MessageExt var1) throws Exception;


    public T convertToEntity(MessageExt message, Class<T> clazz) throws Exception {
        byte[] body = message.getBody();
        if (body == null || body.length == 0) {
            throw new Exception("无消息内容");
        }
        return JSONUtil.toBean(new String(body), clazz);
    }


    /**
     * doMappingThenSomething
     * @param t t
     * @return ConsumeOrderlyStatus
     */
    public abstract ConsumeOrderlyStatus doMappingThenSomething(T t);




    @Override
    public ConsumeOrderlyStatus doSomething(MessageExt var1){
        return null;
    }


}
