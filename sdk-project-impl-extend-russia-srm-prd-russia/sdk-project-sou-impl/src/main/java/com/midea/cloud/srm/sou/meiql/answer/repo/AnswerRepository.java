package com.midea.cloud.srm.sou.meiql.answer.repo;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryFilter;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.meiql.answer.service.AnswerService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
 *  修改日期: 2023/10/17 09:38:58
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class AnswerRepository extends CrudRepository {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouVendorService vendorService;

    public AnswerRepository() {
        this.register("submit", this::submit, true, "提交");
        this.register("replayClose", this::replayClose, true, "回复关闭");
    }


    private QlResult submit(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        answerService.setPubshProperties(recs);
        return super.doSave(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value()),recs);
    }

    private QlResult replayClose(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        for(Record answer : recs) {
            qlService.updateByWrapper(QlWrappers.update(queryAction.getType()).set(AnswerDTO::getExtReplayFlag, YesOrNo.YES.getValue()).eq(AnswerDTO::getAnswerId, answer.get(AnswerDTO::getAnswerId)));
        }
        return ResultUtil.build(queryAction, QlQueryFieldWrapper.field(AnswerDTO::getAnswerId).getFieldName(), recs, false);
    }

    @Override
    public QlResult doSave(QlQueryAction queryAction,List<Record> recs) {
        answerService.setDraftProperties(recs);
        return super.doSave(queryAction,recs);
    }

    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        List<Long> ids = records.stream().map(record -> record.get(AnswerDTO::getAnswerId)).distinct().collect(Collectors.toList());
        List<AnswerVendorDTO> list  =qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                .in(AnswerVendorDTO::getAnswerId,ids)
                .select(AnswerVendorDTO::getAnswerVendorId,AnswerVendorDTO::getAnswerId,
                        AnswerVendorDTO::getIfRead,AnswerVendorDTO::getIfReplay, AnswerVendorDTO::getVendorId),AnswerVendorDTO.class);

        List<Long> projectIdList = records.stream().map(record -> record.get(AnswerDTO::getProjectId)).distinct().filter(projectId -> !Objects.isNull(projectId)).collect(Collectors.toList());
        Map<String, ExtSouVendor> souVendorMap = new HashMap<>(16);
        if(CollectionUtils.isNotEmpty(projectIdList)) {
            souVendorMap = vendorService.listVendorInfoAsShieldVendorNameBatch(projectService.listByIds(projectIdList));
        }


        Map<Long,Integer> readNumMap = new HashMap<>(16);
        Map<Long,Integer> replayNumMap = new HashMap<>(16);
        Map<Long, List<Long>> vendorMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(list)) {
            for (AnswerVendorDTO dto : list) {
                Long key = dto.getAnswerId();
                if (YesOrNo.YES.getValue().equals(dto.getIfRead())) {
                    if (readNumMap.containsKey(key)) {
                        readNumMap.put(key,readNumMap.get(key)+1);
                    } else {
                        readNumMap.put(key,1);
                    }
                }
                if (YesOrNo.YES.getValue().equals(dto.getIfReplay())) {
                    if (replayNumMap.containsKey(key)) {
                        replayNumMap.put(key,replayNumMap.get(key)+1);
                    } else {
                        replayNumMap.put(key,1);
                    }
                }

                if(!vendorMap.containsKey(key)) {
                    vendorMap.put(key, new ArrayList<>(16));
                }
                if(!vendorMap.get(key).contains(dto.getVendorId())) {
                    vendorMap.get(key).add(dto.getVendorId());
                }
            }
            Map<String, ExtSouVendor> finalSouVendorMap = souVendorMap;
            for (Record record : records) {
                Long answerId = record.get(AnswerDTO::getAnswerId);
                Long projectId = record.get(AnswerDTO::getProjectId);
                if (readNumMap.containsKey(answerId)) {
                    record.put(AnswerDTO::getReadNum,readNumMap.get(answerId));
                }
                if (replayNumMap.containsKey(answerId)) {
                    record.put(AnswerDTO::getReplayNum,replayNumMap.get(answerId));
                }
                if(vendorMap.containsKey(answerId)) {
                    record.put(AnswerDTO::getVendorName, vendorMap.get(answerId).stream().map(vendorId -> {
                        String vendorKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, projectId, vendorId);
                        if(finalSouVendorMap.containsKey(vendorKey)) {
                            return finalSouVendorMap.get(vendorKey).getVendorName();
                        }
                        return "";
                    }).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.joining(SrmConstant.SIG_3)));
                }
            }
        }
    }

    @Override
    public QlResult read(QlQueryAction queryAction) {
        QlResult qlResult = super.read(queryAction);
        if(MqlType.ANSWER.equals(queryAction.getType())) {
            Collection<Record> records = qlResult.getRefValues(MqlType.ANSWER);
            Collection<Record> vendors = qlResult.getRefValues(MqlType.ANSWER_VENDOR);
            if(CollectionUtils.isNotEmpty(vendors)) {
                /** 澄清单ID 和 招标单ID 对照Map */
                Map<Long, Long> answerToProjectIdMap = new HashMap<>(16);

                records.stream().forEach(record -> {
                    Long projectId = record.get(AnswerDTO::getProjectId);
                    if(!Objects.isNull(projectId)) {
                        answerToProjectIdMap.put(record.get(AnswerDTO::getAnswerId), projectId);
                    }
                });

                /** 查询供应商脱敏名字 */
                Map<String, ExtSouVendor> vendorMap = vendorService.listVendorInfoAsShieldVendorNameBatch(projectService.listByIds(answerToProjectIdMap.values()));

                /** 澄清单供应商名字脱敏 */
                vendors.stream().forEach(vendor -> {
                    String vendorKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, answerToProjectIdMap.get(vendor.get(AnswerVendorDTO::getAnswerId)), vendor.get(AnswerVendorDTO::getVendorId));

                    if(vendorMap.containsKey(vendorKey)) {
                        ExtSouVendor souVendor = vendorMap.get(vendorKey);
                        vendor.put(AnswerVendorDTO::getVendorCode, souVendor.getVendorCode());
                        vendor.put(AnswerVendorDTO::getVendorName, souVendor.getVendorName());
                    }

                });

            }

        }
        return qlResult;
    }

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
            QueryParam param = MeiQl.toValue(queryAction.getPayload(), QueryParam.class);
            QueryFilter qf = param.getFilter();
            if (qf != null) {
                Object answerStatus = qf.get("answerStatus");
                if(answerStatus == null){
                    qlCondition.notEq(AnswerDTO::getAnswerStatus, AnswerStatusEnum.COMFIRMED.getCode());
                }
            }else{
                qlCondition.notEq(AnswerDTO::getAnswerStatus, AnswerStatusEnum.COMFIRMED.getCode());
            }
        }
        return qlCondition;
    }
}
