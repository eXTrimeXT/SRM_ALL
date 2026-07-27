package com.midea.cloud.srm.sou.meiql.bidnotices.repo;

import com.midea.cloud.common.constant.SmsConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.sms.SmsClient;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.QlContext;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDTO;
import com.midea.cloud.srm.model.sou.bidnotices.dto.BidNoticeDetailDTO;
import com.midea.cloud.srm.model.sou.enums.TypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.meiql.bidnotices.service.BidNoticeService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
@Component
public class BidNoticeDetailRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Value("${global.srm.register-address:没有配置地址}")
    private String cloudUrl;

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;

    public BidNoticeDetailRepository() {
        //注册action
        this.register("send",this::send,true,"发送供应商");
        //去掉保存事务
        this.register("save", this::save, false, "保存");
    }

    private QlResult send(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        for (Record record : recs) {
            record.put(BidNoticeDetailDTO::getIsSend,YesOrNo.YES.getValue());
            record.put(BidNoticeDetailDTO::getSendTime,new Date());
            //发送短信
            sendSms(record);
        }
        return super.update(ProxyQlQueryAction.proxy(queryAction, DefaultAction.UPDATE.value(),recs));
    }

    private void sendSms(Record record) {

        Long bidNoticeId = record.get(BidNoticeDetailDTO::getBidNoticeId);

        List<BidNoticeDTO> bidNoticeDTOS = qlService.queryByWrapper(QlWrappers.query(TypeEnum.BidNotice.getCode()).eq(BidNoticeDTO::getBidNoticeId, bidNoticeId), BidNoticeDTO.class);
        if(CollectionUtils.isEmpty(bidNoticeDTOS)) {
            return;
        }

        ExtSouProject project = projectService.getById(bidNoticeDTOS.get(0).getProjectId());
        if(Objects.isNull(project)) {
            return;
        }

        Long vendorId = record.get(BidNoticeDetailDTO::getVendorId);
        List<ExtSouOrder> orderList = orderService.lambdaQuery().eq(ExtSouOrder::getProjectId, project.getProjectId())
                .eq(ExtSouOrder::getVendorId, vendorId).list();
        if(CollectionUtils.isEmpty(orderList)) {
            return;
        }

        String isWin = record.get(BidNoticeDetailDTO::getIsWin);

        String smsCode = YesOrNo.YES.getValue().equals(isWin) ? SmsConstant.SOU_BID_WIN : SmsConstant.SOU_BID_LOSS;

        Map<String, String> var = new HashMap<>(15);
        var.put("${souProject.souName}", project.getSouName());
        var.put("${souProject.souNo}", project.getExtProjectNo());
        var.put("${souProject.linkMan}", project.getLinkman());
        var.put("${souProject.tel}", project.getTel());

        SmsClient smsClient = SmsClient.newInstance(baseClient, pjProjectExtClient);
        ExtSouOrder order = orderList.get(0);

        smsClient.sendSms(order.getExtTenderPhone(), smsCode, var);
        sendEmail(project.getProjectId(), project.getSouName(), isWin, vendorId);
    }

    public void sendEmail(Long projectId, String souName, String isWin, Long vendorId){
        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().eq(ExtSouVendor::getProjectId, projectId).eq(ExtSouVendor::getVendorId, vendorId).list();
        if(vendorList.isEmpty()) {
            return ;
        }
        String msgTemplateCode = YesOrNo.YES.getValue().equals(isWin) ? "PJ_BIDDING_WIN_VENDOR_NOTICE" : "PJ_BIDDING_LOSE_VENDOR_NOTICE";
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setMsgTemplateCode(msgTemplateCode);
        noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
        Map<String, Object> msgParams = new HashMap(15);
        msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, vendorList.get(0).getEmail());
        msgParams.put("projectName", souName);
        msgParams.put("baseUrl", cloudUrl);
        noticeSendDTO.setMsgParams(msgParams);
        noticeSendGlobalClientService.send(noticeSendDTO);
    }

    @Autowired
    private BidNoticeService bidNoticeService;

    @Override
    public QlResult doSave(QlQueryAction queryAction, List<Record> recs) {
       if(null != recs) {
           Record parentRecord = QlContext.getCurParentRecord();

           String extProjectNo = parentRecord.get(BidNoticeDTO::getExtProjectNo);
           String souName = parentRecord.get(BidNoticeDTO::getSouName);
           String now = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
           for (Record record : recs) {
               //如果不中标则生成落标附件
               if (YesOrNo.NO.getValue().equals(record.get(BidNoticeDetailDTO::getIsWin)) && Objects.isNull(record.get(BidNoticeDetailDTO::getNoticeAttachmentId))) {
                   String vendorName = record.get(BidNoticeDetailDTO::getVendorName);
                   String fileName = vendorName+"供应商落标通知书.PDF";
                   log.info(MessageFormat.format("BidNoticeDetailRepository.doSave 保存附件开始：{0},{1}", souName, fileName));
                   Long fileId = bidNoticeService.saveFile(extProjectNo,vendorName,souName,now,fileName);
                   log.info(MessageFormat.format("BidNoticeDetailRepository.doSave 保存附件结束：{0},{1}", souName, fileName));
                   record.put(BidNoticeDetailDTO::getNoticeAttachmentId,fileId);
                   record.put(BidNoticeDetailDTO::getNoticeAttachmentName,fileName);
               }
           }
       }
       return super.doSave(queryAction,recs);
    }
}
