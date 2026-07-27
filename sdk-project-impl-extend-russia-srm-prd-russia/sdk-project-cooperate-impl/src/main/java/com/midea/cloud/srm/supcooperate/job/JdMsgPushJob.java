package com.midea.cloud.srm.supcooperate.job;

import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.supcooperate.mtmapping.service.JdMsgPushService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * <pre>
 * 拉取京东推送信息和删除已经拉取到的信息
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/2 15:32
 *  修改内容:
 * </pre>
 */
@Job("jdMsgPushJob")
public class JdMsgPushJob implements ExecuteableJob {
    @Autowired
    protected JdMsgPushService jdMsgPushService;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        jdMsgPushService.saveAndDeleteJdMsg();
        return BaseResult.buildSuccess("执行成功！");
    }
}
