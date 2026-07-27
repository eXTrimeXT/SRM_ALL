package com.midea.cloud.srm.sou.meiql.recruit.repo;

import com.midea.cloud.common.constant.SmsConstant;
import com.midea.cloud.common.sms.SmsClient;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.SouExtRbacClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.SccFlowInstanceRecord;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import com.midea.cloud.srm.model.sou.req.SouInviteItem;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recruit.dto.Recruit;
import com.midea.cloud.srm.sou.meiql.recruit.dto.RecruitVendor;
import com.midea.cloud.srm.sou.meiql.recruit.enums.RecruitStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
@Component
public class RecruitVendorRepository extends CrudRepository {

    public RecruitVendorRepository() {
        //注册action
        this.register("visitEnroll", this::visitEnroll, true, "报名");
    }

    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private SouExtRbacClient souExtRbacClient;

    private QlResult visitEnroll(QlQueryAction action) {
        List<Record> records = PayloadWrapper.of(action.getType(), action.getPayload()).asRecords();
        Assert.notEmpty(records, "参数缺失");
        Assert.isTrue(records.size() == 1, "仅支持1条数据");
        Record record = records.get(0);

        Long recruitId = record.get(RecruitVendor::getRecruitId);
        Record recruit = qlService.readByKey("Recruit",recruitId,Record.class);
        Assert.notNull(recruit, "招募申请ID不存在");
        Assert.isTrue(RecruitStatusEnum.APPROVED.getCode().equals(recruit.get(Recruit::getStatus)), "招募申请无效");
        Assert.isTrue(recruit.get(Recruit::getDeadlineTime).compareTo(new Date())>0, "招募已截止");
        //判断同个招募公司名称不能重复
        List<RecruitVendor> result = qlService.queryByWrapper(QlWrappers.query("RecruitVendor")
                .eq(RecruitVendor::getRecruitId, recruitId), RecruitVendor.class);
        String companyName = record.get(RecruitVendor::getCompanyName);
        for(RecruitVendor recruitVendor : result) {
            Assert.isTrue(!companyName.equals(recruitVendor.getCompanyName()), "贵司已经报名，请勿重复提交！");
        }

        //短信通知
//        sendSms(recruit, record);

        return super.doSave(action, records);
    }

    private void sendSms(Record recruit, Record recruitDetail) {
        Long createById = recruit.get(Recruit::getCreatedId);
        ExtUser extUser = souExtRbacClient.getByUserId(createById);
        if(Objects.isNull(extUser)) {
            return;
        }

        SmsClient smsClient = SmsClient.newInstance(baseClient, pjProjectExtClient);

        //您好，${recruit.name}您已报名通过,请及时登录平台注册，如有疑问,请联系招募负责人${recruit.fullName}，办公电话${recruit.tel}。
        Map<String, String> var = new HashMap<>(15);
        var.put("${recruit.name}", recruit.get(Recruit::getName));
        var.put("${recruit.fullName}", extUser.getNickname());
        var.put("${recruit.tel}", extUser.getExtOfficePhone());

        smsClient.sendSms(recruitDetail.get(RecruitVendor::getContactMobile), SmsConstant.RECRUIT_SIGNUP, var);
    }

}
