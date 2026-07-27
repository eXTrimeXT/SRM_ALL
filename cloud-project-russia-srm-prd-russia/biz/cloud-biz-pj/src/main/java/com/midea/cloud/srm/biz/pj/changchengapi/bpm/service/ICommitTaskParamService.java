package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service;


import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.CommitTaskParam;

/**
 * BPM提交参数记录接口
 * @author huangbf3
 */
public interface ICommitTaskParamService extends BaseService<CommitTaskParam> {

    /**
     * 保存或更新
     * @param dbCommitTaskParam
     */
    void saveOrUpdateCommitTaskParam(CommitTaskParam dbCommitTaskParam);
}
