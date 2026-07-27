package com.midea.cloud.srm.sou.req.utils;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/18 12:37
 *  修改内容:
 * </pre>
 */
@Component
public class SouReqCommonUtil {
    @Autowired
    private BaseClient baseClient;
    /**
     * 删除附件
     *
     * @param queryAction
     * @param sceneModuleCode 场景编码
     * @param businessIdName  业务ID名称
     */
    public void deleteFiles(QlQueryAction queryAction, String sceneModuleCode, String businessIdName) {
        try {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Assert.isTrue(ObjectUtil.isNotEmpty(recs), "删除附件数据不能为空");
        recs.stream().filter(record -> ObjectUtil.isNotEmpty(record.get(SouReqHead::getReqHeadId))).forEach(record -> baseClient.removeBusinessIdBatch(sceneModuleCode, Collections.singletonList(record.getLong(businessIdName))));
        } catch (Exception e) {
            throw new RuntimeException("操作失败：更新附件数据异常");
        }
    }
}
