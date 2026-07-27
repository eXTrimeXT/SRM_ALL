package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.mapper.CommitTaskParamMapper;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.ICommitTaskParamService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.CommitTaskParam;
import io.seata.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author huangbf3
 * BPM提交参数记录接口实现类
 */
@Slf4j
@Service
public class CommitTaskParamServiceImpl extends BaseServiceImpl<CommitTaskParamMapper, CommitTaskParam> implements ICommitTaskParamService {
    @Override
    public void saveOrUpdateCommitTaskParam(CommitTaskParam commitTaskParam) {

        CommitTaskParam dbCommitTaskParam = this.lambdaQuery()
                .eq(CommitTaskParam::getBusinessId,commitTaskParam.getBusinessId())
                .eq(CommitTaskParam::getBussinessType,commitTaskParam.getBussinessType())
                .one();
        if(dbCommitTaskParam==null){
            dbCommitTaskParam = new CommitTaskParam();
            dbCommitTaskParam.setCommitRaskParamId(IdGenrator.generate());
            dbCommitTaskParam.setBusinessId(commitTaskParam.getBusinessId());
            dbCommitTaskParam.setBussinessType(commitTaskParam.getBussinessType());
            if(StringUtils.isNotBlank(commitTaskParam.getPredictActivityParam())){
                dbCommitTaskParam.setFirstPredictActivityParam(commitTaskParam.getPredictActivityParam());
            }
        }
        if(StringUtils.isNotBlank(commitTaskParam.getSubmitParam())){
            dbCommitTaskParam.setSubmitParam(commitTaskParam.getSubmitParam());
        }
        if(StringUtils.isNotBlank(commitTaskParam.getPredictActivityParam())){
            dbCommitTaskParam.setPredictActivityParam(commitTaskParam.getPredictActivityParam());
            if(StringUtils.isBlank(dbCommitTaskParam.getFirstPredictActivityParam())){
                dbCommitTaskParam.setFirstPredictActivityParam(commitTaskParam.getPredictActivityParam());
            }
        }
        this.saveOrUpdate(dbCommitTaskParam);
    }
}
