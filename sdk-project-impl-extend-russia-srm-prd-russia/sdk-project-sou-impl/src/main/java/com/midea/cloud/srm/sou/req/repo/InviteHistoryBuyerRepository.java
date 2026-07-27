package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.SouExtRbacClient;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.req.SouInviteHead;
import com.midea.cloud.srm.model.sou.req.SouInviteHistory;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouInviteTypeEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * <pre>
 *  寻源需求邀请供应商历史
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class InviteHistoryBuyerRepository extends CrudRepository {
    @Autowired
    protected QlService qlService;
    @Autowired
    private PjSouClient pjSouClient;
    @Autowired
    protected QlOpenClient qlOpenClient;
    @Autowired
    private SouExtRbacClient souExtRbacClient;

    @Value("${global.srm.register-address:没有配置地址}")
    private String cloudUrl;

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;


    public InviteHistoryBuyerRepository() {
        //注册action
        this.register("publicInvite", this::publicInvite, this::beforePublicInvite, this::afterPublicInvite, true, "公示邀请");
        this.register("notPublicInvite", this::notPublicInvite, this::beforeNotPublicInvite, this::afterNotPublicInvite, true, "不公示邀请");
    }

    private QlResult notPublicInvite(QlQueryAction qlQueryAction) {
        //初始化参数
        this.handleParams(qlQueryAction, SouInviteTypeEnum.PR.getName());
        return super.save(ProxyQlQueryAction.proxy(qlQueryAction, "save"));
    }

    private void beforeNotPublicInvite(QlQueryAction queryAction, Payload payload) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        Record extPrSouRequirementHead = qlOpenClient.read(ContextPath.SUP_CE, MqlType.EXT_PR_SOU_REQUIREMENT_HEAD, records.get(0).get(SouInviteHistory::getSouId));
        Assert.isTrue(extPrSouRequirementHead.getString("needPublic").equals(Enable.N.name()), "邀请失败：非公示邀请只能选择非公示单据发起");
    }

    private QlResult publicInvite(QlQueryAction qlQueryAction) {
        //初始化参数
        this.handleParams(qlQueryAction, SouInviteTypeEnum.RFP.getName());
        return super.save(ProxyQlQueryAction.proxy(qlQueryAction, "save"));
    }

    private void sendMail(QlQueryAction qlQueryAction) {
        String msgTemplateCode = "PJ_SOU_INVITE_VENDOR_NOTICE";
        List<Record> records = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        records.forEach(s -> {
            NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
            noticeSendDTO.setMsgTemplateCode(msgTemplateCode);
            noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
            Map<String, Object> msgParams = new HashMap(15);
            msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, s.get("email"));
            msgParams.put("projectName", s.get("projectName"));
            msgParams.put("baseUrl", cloudUrl);
            msgParams.put("responsibilityUserName", s.get("responsibilityUserName"));
            msgParams.put("phone", s.get("phone"));
            msgParams.put("officePhone", s.get("officePhone"));
            String publicEndTime = DateUtils.formatDate(s.getDate("publicEndTime"),"yyyy年MM月dd日");
            msgParams.put("publicEndTime", publicEndTime);
            msgParams.put("categoryName", s.get("categoryName"));
            msgParams.put("projectScope", s.get("projectScope"));
            noticeSendDTO.setMsgParams(msgParams);
            noticeSendGlobalClientService.send(noticeSendDTO);
        });
    }

    private void beforePublicInvite(QlQueryAction qlQueryAction, Payload payload) {

    }

    private void afterPublicInvite(QlQueryAction queryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        ExtUser user = souExtRbacClient.getByUserId(AppUserUtil.getLoginAppUser().getUserId());
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //查询寻源单头表数据
        SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, records.get(0).get(SouInviteHistory::getSouId), SouReqHead.class);
        int nums = 0;
        //发送邮件
        sendMail(queryAction);
        for (Record record : records) {
            //查询邀请供应商头表数据
            SouInviteHead souInviteHead = qlService.readByKey(MqlType.SOU_INVITE_HEAD_BUYER, record.get(SouInviteHistory::getInviteHeadId), SouInviteHead.class);
            //短信内容
            String phone = ObjectUtil.isEmpty(user.getExtOfficePhone()) ? user.getPhone() : user.getExtOfficePhone() + "/" + user.getPhone();
            String content = "您好，" + record.get(SouInviteHistory::getProjectName) + "招标即将开始，现在已经在长城控股电子招标平台（网址： http://srm-cnp-bdtest.gwmit.cn/）公示，如有意向请在" + DateUtil.formatChineseDate(souReqHead.getPublicEndTime(), false, true) + "前完成网站平台报名。 如有疑问请联系：" + AppUserUtil.getLoginAppUser().getNickname() + "，" + phone + "。注：报名缴费时请在交款凭证上备注“项目简称”如为个人代缴，请在交款凭证上备注“项目简称+参标公司简称”。";
            //发送短信
            this.sendSms(content, souInviteHead.getPhone());
            //回写邀请人数
            long count = qlService.countByWrapper(QlWrappers.query(MqlType.SOU_INVITE_HISTORY_BUYER)
                    .notEq(SouInviteHistory::getInviteHistoryId, record.get(SouInviteHistory::getInviteHistoryId))
                    .eq(SouInviteHistory::getSouId, record.get(SouInviteHistory::getSouId))
                    .eq(SouInviteHistory::getSouType, SouInviteTypeEnum.RFP.getName())
                    .eq(SouInviteHistory::getVendorId, record.get(SouInviteHistory::getVendorId)));
            if (ObjectUtil.isEmpty(count) || count == 0) {
                nums += 1;
            }
        }
        qlService.updateByWrapper(QlWrappers.update(MqlType.SOU_REQ_HEAD_BUYER)
                .set(SouReqHead::getInviteQuantity, souReqHead.getInviteQuantity() + nums)
                .eq(SouReqHead::getReqHeadId, souReqHead.getReqHeadId()));
    }

    private void afterNotPublicInvite(QlQueryAction queryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        ExtUser user = souExtRbacClient.getByUserId(AppUserUtil.getLoginAppUser().getUserId());
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        for (Record record : records) {
            //查询邀请供应商头表数据
            SouInviteHead souInviteHead = qlService.readByKey(MqlType.SOU_INVITE_HEAD_BUYER, record.get(SouInviteHistory::getInviteHeadId), SouInviteHead.class);
            //短信内容
            String phone = ObjectUtil.isEmpty(user.getExtOfficePhone()) ? user.getPhone() : user.getExtOfficePhone() + "/" + user.getPhone();
            String content = "经理您好，长城汽车招标部通知您， " + record.get(SouInviteHistory::getProjectName) + "招标即将开始，由于项目特殊性，没有在长城控股电子招标平台对外公示，最终会在平台给您发送电子标书，已将项目基本概况发送至您邮箱，如有疑问请联系:" + AppUserUtil.getLoginAppUser().getNickname() + "，" + phone + "如有意向，请于接收到此短信之日起两天内邮件回复报名信息。回复后请与我电话确认。若您不参与也请邮件告知，非常感谢。";
            //发送短信
            this.sendSms(content, souInviteHead.getPhone());
        }
    }

    private void sendSms(String content, String phone) {
        try {
            JSONObject phoneMessage = pjSouClient.message(content, phone);
            Assert.isTrue("200".equals(phoneMessage.getString("code")), "发送短信失败");
        } catch (BaseException e) {
            log.error(e.getMessage(), e);
            throw new BaseException("发送短信失败");
        }
    }

    private void handleParams(QlQueryAction qlQueryAction, String name) {
        List<Record> records = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        records.forEach(record -> record.set(SouInviteHistory::getSouType, name));
    }
}
