package com.midea.cloud.srm.sou.meiql.answer.repo;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlUpdateWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.schema.QlType;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.SchemaUtil;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationsType;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.question.dto.QuestionDTO;
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
public class VendorAnswerRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    @Override
    protected QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition qlCondition = super.beforeQuery(queryAction, payload);
        if (null == qlCondition) {
            qlCondition = MeiQl.newCondition();
        }
        QlType qlType = SchemaUtil.getType(queryAction.getType());
        //状态为已发布的数据
        qlCondition.eq(AnswerDTO::getAnswerStatus, AnswerStatusEnum.ISSUED.getCode());

        String ifReplay = (String)payload.getFilter().getValueWithoutOperator("ifReplay");


        QlCondition existCondition = MeiQl.newCondition()
                .eq(AnswerVendorDTO::getVendorId,AppUserUtil.getLoginAppUser().getCompanyId())
                .eq("v",AnswerVendorDTO::getAnswerId, QlQueryFieldWrapper.field(qlType.getTableName(),AnswerDTO::getAnswerId));
        if(StringUtils.isNotEmpty(ifReplay)){
            if(YesOrNo.YES.getValue().equals(ifReplay)) {
                existCondition.eq("v", AnswerVendorDTO::getIfReplay, ifReplay);
            } else {
                existCondition.isNull("v", AnswerVendorDTO::getIfReplay);
            }
        }
        qlCondition.exists(TypeEnum.AnswerVendor.getCode(),"v",existCondition);
        return qlCondition;
    }
    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        List<Long> ids = records.stream().map(record -> record.get(AnswerDTO::getAnswerId)).distinct().collect(Collectors.toList());
        List<AnswerVendorDTO> list  =qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                .in(AnswerVendorDTO::getAnswerId,ids)
                .eq(AnswerVendorDTO::getVendorId,AppUserUtil.getLoginAppUser().getCompanyId()),AnswerVendorDTO.class);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<Long,List<AnswerVendorDTO>> map  = list.stream().collect(Collectors.groupingBy(AnswerVendorDTO::getAnswerId));
            for (Record record : records) {
                Long answerId = record.get(AnswerDTO::getAnswerId);
                if (map.containsKey(answerId) && null != map.get(answerId)) {
                    record.put(AnswerDTO::getConfirmStatus,map.get(answerId).get(0).getConfirmStatus());
                    if (StringUtils.isBlank(map.get(answerId).get(0).getIfReplay())) {
                        record.put(AnswerDTO::getIfReplay,YesOrNo.NO.getValue());
                    } else {
                        record.put(AnswerDTO::getIfReplay,map.get(answerId).get(0).getIfReplay());
                    }

                }
            }
        }
    }

    @Override
    public void afterRead(QlQueryAction queryAction, Collection<Record> records){
        for (Record record : records) {
            List<AnswerVendorDTO> list = qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                    .eq(AnswerVendorDTO::getVendorId,AppUserUtil.getLoginAppUser().getCompanyId())
                    .eq(AnswerVendorDTO::getAnswerId,record.get(AnswerVendorDTO::getAnswerId)),AnswerVendorDTO.class);
            if (CollectionUtils.isNotEmpty(list)) {
                record.put(AnswerDTO::getReplayId,list.get(0).getReplayId());
                record.put(AnswerDTO::getConfirmStatus,list.get(0).getConfirmStatus());
                record.put(AnswerDTO::getAnswerVendorId,list.get(0).getAnswerVendorId());
                //更新第一次已读
                if(!UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType())){
                    AnswerVendorDTO dto = list.get(0);
                    if (null == list.get(0).getIfRead() || YesOrNo.NO.getValue().equals(dto.getIfRead())) {
                        qlService.updateByWrapper(QlWrappers.update(TypeEnum.AnswerVendor.getCode())
                                .set(AnswerVendorDTO::getIfRead,YesOrNo.YES.getValue())
                                .set(AnswerVendorDTO::getReadTime,new Date())
                                .eq(AnswerVendorDTO::getAnswerVendorId,dto.getAnswerVendorId()));
                    }
                }
            }
        }
    }
}
