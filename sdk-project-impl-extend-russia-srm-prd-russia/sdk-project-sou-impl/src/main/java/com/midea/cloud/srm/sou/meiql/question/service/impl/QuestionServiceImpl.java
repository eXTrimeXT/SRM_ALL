package com.midea.cloud.srm.sou.meiql.question.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.constant.SmsConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.sms.SmsClient;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.pm.PmClient;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.question.dto.QuestionDTO;
import com.midea.cloud.srm.model.sou.question.enums.QuestionStatusEnum;
import com.midea.cloud.srm.model.sou.question.enums.QuestionTypeEnum;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.question.service.QuestionService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouProjectMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
 *  修改日期: 2023/10/17 08:45:51
 *  修改内容:
 * </pre>
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;

    @Resource
    private ExtSouProjectMapper projectMapper;

    @Autowired
    private PmClient pmClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public void initDraftValues(List<Record> recs) {
        for (Record rec : recs) {
            //初始化状态为拟定
            if (StringUtils.isEmpty(rec.get(QuestionDTO::getQuestionStatus))) {
                rec.put(QuestionDTO::getQuestionStatus, QuestionStatusEnum.DRAFT.getCode());
            }
            //设置供应商
            rec.put(QuestionDTO::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId());
            rec.put(QuestionDTO::getVendorCode, AppUserUtil.getLoginAppUser().getCompanyCode());
            rec.put(QuestionDTO::getVendorName, AppUserUtil.getLoginAppUser().getCompanyName());
            //初始化项目特色属性
            initProjectProperties(rec);
        }
    }

    @Override
    public void initSubmitValues(List<Record> recs) {
        for (Record rec : recs) {
            //初始化状态为拟定
            rec.put(QuestionDTO::getQuestionStatus, QuestionStatusEnum.SUBMITTED.getCode());
            rec.put(QuestionDTO::getSubmitTime,new Date());
            //设置供应商
            rec.put(QuestionDTO::getVendorId, AppUserUtil.getLoginAppUser().getCompanyId());
            rec.put(QuestionDTO::getVendorCode, AppUserUtil.getLoginAppUser().getCompanyCode());
            rec.put(QuestionDTO::getVendorName, AppUserUtil.getLoginAppUser().getCompanyName());
            //初始化项目特色属性
            initProjectProperties(rec);
        }
    }

    @Override
    public void initReplayValues(List<Record> recs) {
        for (Record rec : recs) {
            rec.put(QuestionDTO::getQuestionStatus, QuestionStatusEnum.REPLAYED.getCode());
        }
    }

    @Override
    public void sendSmsReplayValues(List<Record> recs) {
        for (Record rec : recs) {
            Long projectId = rec.get(QuestionDTO::getProjectId);
            Long vendorId = rec.get(QuestionDTO::getVendorId);
            if(Objects.isNull(projectId) || Objects.isNull(vendorId)) {
                continue;
            }
            ExtSouProject souProject = projectService.getById(projectId);
            List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getVendorId, vendorId)
                    .eq(ExtSouVendor::getProjectId, projectId).list();
            if(Objects.isNull(souProject) || CollectionUtils.isEmpty(vendorList)) {
                continue;
            }

            //短信发送客户端
            SmsClient smsClient = SmsClient.newInstance(baseClient, pjProjectExtClient);

            ExtSouVendor vendor = vendorList.get(0);

            Map<String, String> var = new HashMap<>(15);
            //您好，${souProject.souName}(${souProject.souNo})已回复质疑,请及时登录系统查看。如有疑问，请联系${souProject.linkMan}，联系电话 ${souProject.tel}。
            var.put("${souProject.souName}", souProject.getSouName());
            var.put("${souProject.souNo}", souProject.getExtProjectNo());
            var.put("${souProject.linkMan}", souProject.getLinkman());
            var.put("${souProject.tel}", souProject.getTel());

            smsClient.sendSms(vendor.getPhone(), SmsConstant.ANSWER_REPLY, var);
        }
    }

    public void initProjectProperties(Record rec) {
        String applicantId = null;
        String applicantNo = null;
        if (QuestionTypeEnum.SOU.getCode().equals(rec.get(QuestionDTO::getExtType))) {
            Long projectId = rec.get(QuestionDTO::getProjectId);
            //招标负责人、供应商负责人
            ApiExtSouProjectInfoDTO projectInfoDTO = extSouInitQueryService.getProjectInfo(projectId);
            if (null != projectInfoDTO.getProject().getApplicantNo()) {
                //获取第一个申请号
                applicantNo = projectInfoDTO.getProject().getApplicantNo().split(";")[0];
                if(StringUtils.isNotBlank(projectInfoDTO.getProject().getApplicantId())) {
                    applicantId = projectInfoDTO.getProject().getApplicantId().split(";")[0];
                }
            }
        } else {
            applicantNo = rec.get(QuestionDTO::getSouNo);
            if (null != applicantNo) {
                applicantId = String.valueOf(rec.get(QuestionDTO::getProjectId));
            }
        }
        if(null != applicantId) {
            initBidInfo(applicantId, rec);
        }
        //推荐是否完成
        List<Record> list = qlService.queryByWrapper(QlWrappers.query("RecommvendorProject")
                .eq(RecommvendorProjectDto::getSourceFromNo,applicantNo.split(";")[0])
                .eq(RecommvendorProjectDto::getCreateApprovalStatus,SouApprovalStatusEnum.APPROVED.name())
                .select(RecommvendorProjectDto::getProjectId),Record.class);
        if (CollectionUtils.isNotEmpty(list)) {
            rec.put(QuestionDTO::getExtIfRecommendFinish, YesOrNo.YES.getValue());
        } else {
            rec.put(QuestionDTO::getExtIfRecommendFinish, YesOrNo.NO.getValue());
        }
    }

    /**
     * 招标信息
     */
    @Override
    public void initBidInfo(String applicantId,Record rec) {
        //招标计划池,获取供应商负责人
        List<ExtPrSouRequirementGroup> list = qlOpenClient.query(ContextPath.SUP_CE,
                QlOpenWrappers.query("ExtPrSouRequirementGroup")
                        .eq(ExtPrSouRequirementGroup::getRequirementHeadId,applicantId),ExtPrSouRequirementGroup.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ExtPrSouRequirementGroup group : list) {
                if (PrSouRequirementGroupTypeEnum.SOU.name().equals(group.getGroupType())) {
                    rec.put(QuestionDTO::getExtBidUserId,group.getUserId());
                    rec.put(QuestionDTO::getExtBidUsername,group.getUsername());
                    rec.put(QuestionDTO::getExtBidNickname,group.getFullName());
                } else if (PrSouRequirementGroupTypeEnum.VENDOR.name().equals(group.getGroupType())) {
                    rec.put(QuestionDTO::getExtVendorUserId,group.getUserId());
                    rec.put(QuestionDTO::getExtVendorUsername,group.getUsername());
                    rec.put(QuestionDTO::getExtVendorNickname,group.getFullName());
                }
            }
        }
    }
}
