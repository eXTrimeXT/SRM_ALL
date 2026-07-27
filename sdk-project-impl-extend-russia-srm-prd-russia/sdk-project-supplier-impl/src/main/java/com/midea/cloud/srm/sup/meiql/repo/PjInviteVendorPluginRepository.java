package com.midea.cloud.srm.sup.meiql.repo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.util.OpenApiUtil;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.RbacExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateCodeEnum;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import com.midea.cloud.srm.model.rbac.ExtUserPermissionDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.invite.entity.InviteVendor;
import com.midea.cloud.srm.model.supplier.invite.enums.InviteVendorStatusEnum;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.sup.invite.service.InviteVendorService;
import com.midea.cloud.srm.sup.meiql.dto.InviteVendorDTO;
import com.midea.cloud.srm.sup.meiql.util.RecordUtil;
import io.seata.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
 *  修改日期: 2023/1/5 11:01:48
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class PjInviteVendorPluginRepository extends CrudRepository {

    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ICompanyInfoService iCompanyInfoService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Value("${srm.baseUrl}")
    private String srmBaseUrl;

    @Autowired
    private RbacExtClient rbacExtClient;

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;

    public PjInviteVendorPluginRepository() {
        super();
        // 注册action
        this.register("publish", this::publish, true, "发布");
    }

    private QlResult publish(QlQueryAction queryAction) {
        // 获取前端传来的数据
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        if (recs.size() == 0) {
            throw new BaseException("数据为空");
        }
        //默认状态拟定
        recs.forEach(r -> {
            r.put("inviteStatus",InviteVendorStatusEnum.INVITING.getValue());
            r.put("publishDate",new Date());
        });
        QlResult qlResult = super.doSave(queryAction, recs);
        //默认状态拟定
        recs.forEach(r -> {
            String key = r.get("inviteVendorId").toString();
            InviteVendor inviteVendor = this.qlService.readByKey(queryAction.getType(), key, InviteVendor.class);
            //发布时需发邮件
            if (InviteVendorStatusEnum.INVITING.getValue().equals(inviteVendor.getInviteStatus())) {
                sendEmail(inviteVendor);
                sendPhone(inviteVendor);
            }
        });
        return qlResult;
    }

    private void sendEmail(InviteVendor inviteVendor){
        ExtUserPermissionDTO extUserPermissionDTO = rbacExtClient.getByBuyer(AppUserUtil.getLoginAppUser().getUserId());
        String extOfficePhone = StringUtils.isBlank(extUserPermissionDTO.getUser().getExtOfficePhone())?"":extUserPermissionDTO.getUser().getExtOfficePhone();
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setMsgTemplateCode(NoticeTemplateCodeEnum.INVITE_VENDOR.getValue());
        noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
        Map<String, Object> msgParams = new HashMap(16);
        msgParams.put("NOTICE_RECEIVER_INFO", inviteVendor.getContactEmail());
        msgParams.put("inviteVendor", inviteVendor);
        msgParams.put("extOfficePhone", extOfficePhone);
        noticeSendDTO.setMsgParams(msgParams);
        noticeSendGlobalClientService.send(noticeSendDTO);
    }

    /**
     * 发送短信
     * @param inviteVendor
     */
    private void sendPhone(InviteVendor inviteVendor) {
        try{
            if(StringUtils.isNotBlank(inviteVendor.getPhoneNumber())){
                ExtUserPermissionDTO extUserPermissionDTO = rbacExtClient.getByBuyer(AppUserUtil.getLoginAppUser().getUserId());
                String extOfficePhone = StringUtils.isBlank(extUserPermissionDTO.getUser().getExtOfficePhone())?"":extUserPermissionDTO.getUser().getExtOfficePhone();
                NoticeTemplate noticeTemplate = baseClient.listPageNoticeTemplate(new NoticeTemplate().setNoticeTemplateCode("INVITE_VENDOR")
                        .setNoticeTemplateMode(NoticeTemplateModeEnum.MESSAGE.getValue())).getList().get(0);
                String content = noticeTemplate.getNoticeTemplateContent();
                content = content.replace("${inviteVendor.vendorName}",inviteVendor.getVendorName());
                content = content.replace("${inviteVendor.contactPerson}",inviteVendor.getContactPerson());
                content = content.replace("${inviteVendor.createdFullName}", AppUserUtil.getLoginAppUser().getNickname());
                content = content.replace("${url}", srmBaseUrl+"/#/registered");
                content = content.replace("${extOfficePhone}", extOfficePhone);
                log.info("content:"+content);
                pjProjectExtClient.message(content,inviteVendor.getPhoneNumber());
            }
        }catch (Exception e){
            log.info("短信发送报错:"+e.getMessage());
        }
    }

    private static String TYPE="InviteVendor";

    @Override
    public QlResult save(QlQueryAction queryAction) {
        List<Record> recs = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        //默认状态拟定
        recs.forEach(r -> {
            r.put("inviteStatus",InviteVendorStatusEnum.DRAFT.getValue());
        });
        QlResult qlResult = super.save(queryAction);
        return qlResult;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateInviteStatus() {
        List<InviteVendorDTO> inviteVendors = qlService.queryByWrapper(MeiQl.newQueryWrapper(TYPE)
                        .eq("inviteStatus",InviteVendorStatusEnum.INVITING.getValue()),InviteVendorDTO.class);
        if (CollectionUtils.isNotEmpty(inviteVendors)) {
            // 根据名称去公司表中查询是否已经存在，存在则修改状态为已注册
            List<String> vendorName = inviteVendors.stream().map(InviteVendorDTO::getVendorName).collect(Collectors.toList());
            QueryWrapper<CompanyInfo> companyWrapper = new QueryWrapper<>();
            companyWrapper.in("COMPANY_NAME", vendorName);
            List<CompanyInfo> companyInfos = iCompanyInfoService.list(companyWrapper);
            if (CollectionUtils.isEmpty(companyInfos)) {
                return;
            }
            List<InviteVendorDTO> updateList = new ArrayList<>();
            for (CompanyInfo companyInfo : companyInfos) {
                for (InviteVendorDTO inviteVendor : inviteVendors) {
                    if (inviteVendor.getVendorName().equals(companyInfo.getCompanyName())) {
                        inviteVendor.setInviteStatus(InviteVendorStatusEnum.REGISTERED.getValue());
                        inviteVendor.setVendorId(companyInfo.getCompanyId());
                        updateList.add(inviteVendor);
                    }
                }
            }
            //更新
            super.update(OpenApiUtil.convertSaveRequest(TYPE,RecordUtil.UPDATE,updateList));
        }
    }
}
