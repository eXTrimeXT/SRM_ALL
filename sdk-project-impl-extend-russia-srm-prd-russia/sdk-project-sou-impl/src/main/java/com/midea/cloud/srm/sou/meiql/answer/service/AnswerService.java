package com.midea.cloud.srm.sou.meiql.answer.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;

import java.util.Collection;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface AnswerService {
    /**
     * 拟定状态下保存设置参数
     * @param recs
     */
    void setDraftProperties(List<Record> recs);

    /**
     * 已发布状态下保存设置参数
     * @param recs
     */
    void setPubshProperties(Collection<Record> recs);

    /**
     * 备注
     * @param answerVendorId 备注
     */
    public void checkConfirm(Long answerVendorId);
}
