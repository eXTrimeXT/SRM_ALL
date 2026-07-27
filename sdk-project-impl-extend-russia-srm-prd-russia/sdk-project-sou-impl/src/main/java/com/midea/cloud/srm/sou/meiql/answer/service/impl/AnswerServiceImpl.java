package com.midea.cloud.srm.sou.meiql.answer.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.midea.cloud.common.constant.SmsConstant;
import com.midea.cloud.common.sms.SmsClient;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerDTO;
import com.midea.cloud.srm.model.sou.answer.dto.AnswerVendorDTO;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerConfirmStatusEnum;
import com.midea.cloud.srm.model.sou.answer.enums.AnswerStatusEnum;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.question.dto.QuestionDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.meiql.answer.service.AnswerService;
import com.midea.cloud.srm.sou.meiql.question.service.QuestionService;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 *  修改日期: 2023/10/17 11:56:17
 *  修改内容:
 * </pre>
 */
@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Override
    public void setDraftProperties(List<Record> recs) {
        initProjectInfo(recs);
        for (Record rec : recs) {
            //初始化状态为拟定
            if (StringUtils.isEmpty(rec.get(AnswerDTO::getAnswerStatus))) {
                rec.put(AnswerDTO::getAnswerStatus, AnswerStatusEnum.DRAFT.getCode());
            }
        }
    }

    @Override
    public void setPubshProperties(Collection<Record> recs) {
        initProjectInfo(recs);
        for (Record rec : recs) {
            if (AppUserUtil.getLoginAppUser().getUserId().equals(rec.get(AnswerDTO::getExtBidUserId))) {
                rec.put(AnswerDTO::getAnswerStatus, AnswerStatusEnum.ISSUED.getCode());
                //发布发送通信通知
                sendSms(rec);
            } else {
                rec.put(AnswerDTO::getAnswerStatus, AnswerStatusEnum.WAIT_PUBLISH.getCode());
            }
        }
    }

    public void sendSms(Record record) {
        Long projectId = record.get(AnswerDTO::getProjectId);
        List<Record> records = record.getSubRecords("answerVendors");
        if(Objects.isNull(projectId) || CollectionUtils.isEmpty(records)) {
            return;
        }
        ExtSouProject project = projectService.getById(projectId);
        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId)
                .in(ExtSouVendor::getVendorId, records.stream().map(r -> r.get(AnswerVendorDTO::getVendorId)).distinct().collect(Collectors.toList()))
                .list();
        if(Objects.isNull(project) || CollectionUtils.isEmpty(vendorList)) {
            return;
        }

        //短信发送客户端
        SmsClient smsClient = SmsClient.newInstance(baseClient, pjProjectExtClient);
        //发送短信
        vendorList.stream().forEach(vendor -> {
            //您好,${souProject.souName}(${souProject.souNo})已发布澄清,请及时登录系统查看。如有疑问，请联系${souProject.linkMan}，联系电话 ${souProject.tel}。
            Map<String, String> var = new HashMap<>(15);
            var.put("${souProject.souName}", project.getSouName());
            var.put("${souProject.souNo}", project.getExtProjectNo());
            var.put("${souProject.linkMan}", project.getLinkman());
            var.put("${souProject.tel}", project.getTel());
            smsClient.sendSms(vendor.getPhone(), SmsConstant.ANSWER_PUBLISH, var);
        });
    }

    private void initProjectInfo(Collection<Record> recs) {
        for (Record rec : recs) {
            Long projectId = rec.get(AnswerDTO::getProjectId);
            ApiExtSouProjectInfoDTO projectInfoDTO = extSouInitQueryService.getProjectInfo(projectId);
            if(StringUtils.isNotBlank(projectInfoDTO.getProject().getApplicantId())) {
                questionService.initBidInfo(projectInfoDTO.getProject().getApplicantId().split(";")[0],rec);
            }
        }
    }
    @Override
    public void checkConfirm(Long answerVendorId) {
        List<AnswerVendorDTO> list = qlService.queryByWrapper(QlWrappers.query(TypeEnum.AnswerVendor.getCode())
                        .eq(AnswerVendorDTO::getAnswerVendorId,answerVendorId)
                        .eq(AnswerVendorDTO::getConfirmStatus, AnswerConfirmStatusEnum.COMFIRMED.getCode())
                        .select(AnswerVendorDTO::getAnswerVendorId)
                ,AnswerVendorDTO.class);
        if (CollectionUtils.isNotEmpty(list)) {
            AssertUtils.isTrue(false,"回复已确认，不能修改");
        }
    }
}
