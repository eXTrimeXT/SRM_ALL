package com.midea.cloud.srm.sou.meiql.question.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/17 08:45:03
 *  修改内容:
 * </pre>
 */
public interface QuestionService {
    /**
     * 保存时初始化值
     * @param recs
     */
    void initDraftValues(List<Record> recs);

    /**
     * 备注
     * @param recs 参数
     */
    void initSubmitValues(List<Record> recs);

    /**
     * 招标信息
     * @param applicantId 参数
     * @param rec 参数
     */
    public void initBidInfo(String applicantId,Record rec);

    /**
     * 备注
     * @param recs 参数
     */
    public void initReplayValues(List<Record> recs);

    /**
     * 质疑回复提交-发送短信
     * @param recs
     */
    public void sendSmsReplayValues(List<Record> recs);
}
