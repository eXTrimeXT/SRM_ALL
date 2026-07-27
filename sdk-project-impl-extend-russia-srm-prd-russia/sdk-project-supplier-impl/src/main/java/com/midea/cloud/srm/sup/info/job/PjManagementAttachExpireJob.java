package com.midea.cloud.srm.sup.info.job;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeTemplateDto;
import com.midea.cloud.srm.model.base.noticetemplate.dto.SendNoticeTemplateDto;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateCodeEnum;
import com.midea.cloud.srm.model.cost.base.reduce.constants.ReduceConstant;
import com.midea.cloud.srm.model.sup.info.dto.ExtManagementAttachDTO;
import com.midea.cloud.srm.model.supplier.info.dto.ManagementAttachRequestDTO;
import com.midea.cloud.srm.model.supplier.info.entity.ManagementAttach;
import com.midea.cloud.srm.sup.info.service.VendorInformationPjService;
import com.midea.cloud.srm.sup.info.service.impl.VendorInformationPjServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 *  证件提醒发送邮件-定时任务
 * </pre>
 *
 * @author luxc18@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023-9-13 19:04
 *  修改内容:
 * </pre>
 */
@Job("pjManagementAttachExpireJob")
@Slf4j
public class PjManagementAttachExpireJob implements ExecuteableJob {

    @Value("${global.srm.register-address:没有配置地址}")
    private String cloudUrl;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;

    @Autowired
    private QlService qlService;

    @Autowired
    private VendorInformationPjService vendorInformationPjService;

    private final String COMPANY_TYPE = "CompanyInfo";

    private final String COMPANY_CONTACT_TYPE = "ContactInfo";

    public static final int PAGE_SIZE = 1000;


    @Override
    public BaseResult executeJob(Map<String, String> params) {
        Long total =(long) Integer.MAX_VALUE;
        int pageNum = 1;
        try{
            Map<Long,List<ManagementAttach>> sentEmailManagementAttachMap=new HashMap<>(16);
            //查询供应商证件到期数据
            ManagementAttachRequestDTO queryParam=new ManagementAttachRequestDTO();
            queryParam.setAuthDate(VendorInformationPjServiceImpl.getDateByAddingDays(15));
            queryParam.setEndDate(VendorInformationPjServiceImpl.getDateByAddingDays(15));
            while ((pageNum - 1) * PAGE_SIZE < total) {
                queryParam.setPageNum(pageNum);
                queryParam.setPageSize(Integer.valueOf(total.toString()));
                PageInfo<ExtManagementAttachDTO> page= vendorInformationPjService.listManagementAttachPage(queryParam);
                if(CollectionUtils.isNotEmpty(page.getList())){
                    List<ExtManagementAttachDTO> attachList= page.getList();
                    // 遍历 attachList
                    for (ManagementAttach attach : attachList) {
                        Long companyId = attach.getCompanyId();

                        // 检查 Map 中是否已经有对应的键（companyId）
                        if (sentEmailManagementAttachMap.containsKey(companyId)) {
                            // 如果已经存在，获取对应的 List 并添加当前的 ManagementAttach 对象
                            List<ManagementAttach> companyAttachments = sentEmailManagementAttachMap.get(companyId);
                            companyAttachments.add(attach);
                        } else {
                            // 如果不存在，创建一个新的 List，并将当前的 ManagementAttach 对象添加到该 List
                            List<ManagementAttach> companyAttachments = new ArrayList<>();
                            companyAttachments.add(attach);
                            sentEmailManagementAttachMap.put(companyId, companyAttachments);
                        }
                    }
                }
                total = page.getTotal();
                pageNum++;
            }
            if(sentEmailManagementAttachMap.size()==0){
                return BaseResult.buildSuccess("没有证件到期提醒邮件发送数据");
            }
            Set<Long> companyIds=sentEmailManagementAttachMap.keySet();
            Map<Long,List<String>> sentEmailMap=this.getSentEmail(companyIds);
            if(sentEmailMap.size()>0){
                Iterator<Map.Entry<Long, List<String>>> iterator = sentEmailMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<Long, List<String>> entry = iterator.next();
                    // 获取Map中的键（Long类型）
                    Long key = entry.getKey();
                    // 获取Map中的值（List<String>类型）
                    List<String> value = entry.getValue();
                    //获取供应商名称
                    List<ManagementAttach> attachList=sentEmailManagementAttachMap.get(key);
                    String companyName=attachList.get(0).getCompanyName();
                    // 使用Set来去重
                    Set<String> uniqueEmails = new HashSet<>(value);
                    for (ManagementAttach attach : attachList) {
                        // 在这里对每个 ManagementAttach 对象进行操作
                        String authType = "";
                        List<DictItem> formTypeDict = baseClient.listDictItemByDictCode("ExpiredCertificateType");
                        Map<String, String> formTypeMap = formTypeDict.stream().collect(Collectors.toMap(DictItem::getDictItemCode, DictItem::getDictItemName));
                        List<DictItem> authNumDict = baseClient.listDictItemByDictCode("CERTIFICATE_TYPE");
                        Map<String, String> authNumMap = authNumDict.stream().collect(Collectors.toMap(DictItem::getDictItemCode, DictItem::getDictItemName));
                        //ExpiredCertificateType 字典
                        String formType = attach.getFormType();
                        // CERTIFICATE_TYPE
                        String authNum = attach.getAuthNum();
                        if("MANAGEMENT_ATTACH".equals(formType)){
                            authType = authNumMap.get(authNum);
                        }else{
                            authType = formTypeMap.get(formType);
                        }
                        // 获取 endDate 字段的值
                        LocalDate endDate = attach.getEndDate();
                        //发送邮件
                        this.sendEmailByTemplate(companyName,uniqueEmails,authType,endDate);
                    }
                }
            }
        }catch (Exception e){
            log.error("证件提醒邮件发送异常",e);
            return BaseResult.buildSuccess("证件提醒邮件发送异常");
        }
        return BaseResult.buildSuccess("证件提醒邮件发送成功");
    }


    /**
     * 根据供应商ID集合获取供应商默认联系人
     * @param companyIds
     * @return
     */
    private Map<Long,List<String>> getSentEmail(Set<Long> companyIds){
        //取默认联系人
        Map<Long, List<String>> sentEmailMap = new HashMap<>(16);
        QlCondition contactCondition = MeiQl.newCondition();
        contactCondition.in("companyId", companyIds);
        contactCondition.eq("ceeaDefaultContact", YesOrNo.YES.getValue());
        contactCondition.isNotNull("email");
        List<Record> contactInfoList = qlService.query(COMPANY_CONTACT_TYPE, contactCondition, Record.class);
        if (CollectionUtils.isNotEmpty(contactInfoList)) {
            // 遍历查询结果并分组
            for (Record contactInfo : contactInfoList) {
                Long companyId = contactInfo.getLong("companyId");
                String email = contactInfo.getString("email");

                // 检查 Map 中是否已经有对应的键（companyId）
                if (sentEmailMap.containsKey(companyId)) {
                    // 如果已经存在，获取对应的 List 并添加当前的邮箱地址
                    List<String> emailList = sentEmailMap.get(companyId);
                    emailList.add(email);
                } else {
                    // 如果不存在，创建一个新的 List，并将当前的邮箱地址添加到该 List
                    List<String> emailList = new ArrayList<>();
                    emailList.add(email);
                    sentEmailMap.put(companyId, emailList);
                }
            }
        }
        return sentEmailMap;
    }

    private void sendEmailByTemplate(String companyName, Set<String> emailList, String fileName, LocalDate businessEndDate) {
        for (String email : emailList) {
            NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
            noticeSendDTO.setMsgTemplateCode("PJ_VENDOR_ATTACH_INFO_NOTICE");
            noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
            Map<String, Object> msgParams = new HashMap<>(16);
            msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, email.trim());
            msgParams.put("vendorName", companyName);
            msgParams.put("fileName", fileName);
            msgParams.put("endDate", businessEndDate);
            msgParams.put("srmAddress", cloudUrl);
            noticeSendDTO.setMsgParams(msgParams);
            noticeSendGlobalClientService.send(noticeSendDTO);
        }
    }


}
