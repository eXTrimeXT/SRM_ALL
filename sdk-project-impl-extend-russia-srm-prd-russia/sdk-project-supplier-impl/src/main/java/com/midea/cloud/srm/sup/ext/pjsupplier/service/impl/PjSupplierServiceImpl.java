package com.midea.cloud.srm.sup.ext.pjsupplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.esotericsoftware.minlog.Log;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.emun.PjCompanyStatusEmun;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResultDto;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.InternalSupplierQuery;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.model.supplier.invite.entity.InviteVendor;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.sup.ext.pjsupplier.service.PjSupplierService;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.sup.info.service.IContactInfoService;
import com.midea.cloud.srm.sup.invite.service.InviteVendorService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author luxc18
 */
@Slf4j
@Primary
@Service
public class PjSupplierServiceImpl implements PjSupplierService {
    @Autowired
    private QlService qlService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private ICompanyInfoService companyInfoService;

    @Autowired
    private InviteVendorService inviteVendorService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private IContactInfoService contactInfoService;

    @Override
    public void getMdmCodeByCompanyId(Long companyId) {
        // TODO 1.获取主信息,看账户组是否有值
        List<Record> companyInfos = qlService.queryByWrapper(QlWrappers.query(MqlType.SUPPLIER)
                .eq("companyId", companyId), Record.class);
        String accountGroup = companyInfos.get(0).getString("accountGroup");
        if (StringUtils.isEmpty(accountGroup)) {
            String lcCode = companyInfos.get(0).getString("lcCode");
            try {
                Map<String, List<InternalSupplierQuery>> internalSupplierData = pjProjectExtClient.getInternalSupplierData(Arrays.asList(lcCode));
                if (internalSupplierData.containsKey(lcCode)) {
                    List<InternalSupplierQuery> internalSupplierQueries = internalSupplierData.get(lcCode);
                    List<InternalSupplierQuery> existList = internalSupplierQueries.stream().filter(item -> StringUtils.isNotEmpty(item.getTrade_partner_code())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(existList)) {
                        companyInfos.get(0).put("accountGroup", "Z003");
                        companyInfos.get(0).put("partner", existList.get(0).getTrade_partner_code());
                    } else {
                        companyInfos.get(0).put("accountGroup", "Z001");
                    }
                } else {
                    companyInfos.get(0).put("accountGroup", "Z001");
                }
            } catch (Exception e) {
                throw new BaseException("调用获取账户组接口失败,请重试");
            }
        }
        String partner = companyInfos.get(0).getString("partner");
        String accountGroupFromApi = companyInfos.get(0).getString("accountGroup");
        log.info("账户组接口返回信息,partner:{},accountGroupFromApi:{}", partner, accountGroupFromApi);
        // 2.更新MDM编码到供应商主数据
        try {
            CompanyInfo companyInfoParam = new CompanyInfo();
            companyInfoParam.putX("partner", partner);
            companyInfoParam.putX("accountGroup", accountGroupFromApi);
            MdmResponseDto mdmResponseDto = pjProjectExtClient.sendCompanyInfoToMdm(companyInfoParam.setCompanyId(companyId));
            log.info("mdmResponseDto,MDM接口返回:" + JSONObject.toJSONString(mdmResponseDto));
            List<MdmResultDto> result = JSONArray.parseArray(JSONArray.toJSONString(mdmResponseDto.getResult()), MdmResultDto.class);
            String mdmCode = result.get(0).getOrgCode();
            companyInfos.get(0).put("companyCode", mdmCode);
            qlService.update(MqlType.SUPPLIER, companyInfos);

            /*companyInfoService.update(Wrappers.lambdaUpdate(CompanyInfo.class)
                    .set(CompanyInfo::getCompanyCode,mdmCode)
                    .eq(CompanyInfo::getCompanyId,companyId)
            );*/
        } catch (Exception e) {
            Log.error("获取MDM编码失败:" + e);
            throw new BaseException("获取MDM编码失败:" + e.getMessage());
        }
        // 3.供应商状态为准供应商
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .eq("companyId", companyId)
                .set("pjCompanyStatus", PjCompanyStatusEmun.QUASI_SUPPLIER.name()));
        // 回调sou方法
        try {
            ContactInfo contactInfo = contactInfoService.selectFirst(Wrappers.lambdaQuery(ContactInfo.class)
                    .eq(ContactInfo::getCompanyId, companyId)
                    .eq(ContactInfo::getCeeaDefaultContact, Enable.Y.name()));
            List<Record> recordList = new ArrayList<>();
            Record record = new Record();
            record.put("vendorId", companyId);
            record.put("vendorCode", companyInfos.get(0).get("companyCode"));
            record.put("vendorName", companyInfos.get(0).get("companyName"));
            if (contactInfo != null) {
                record.put("contactName", contactInfo.getContactName());
                record.put("phone", contactInfo.getCeeaContactMethod());
                record.put("email", contactInfo.getEmail());
            }
            recordList.add(record);
            qlOpenClient.create(ContextPath.SOU, "SouInviteHeadBuyer", recordList);
        } catch (Exception e) {
            log.error("sup回写SouInviteHeadBuyer失败:" + e);
            log.error(e.getMessage());
        }
    }

    @Override
    public void sendDingDingMsg(Long companyId) {
        CompanyInfo companyInfo = companyInfoService.getById(companyId);
        String companyName = companyInfo.getCompanyName();

        List<InviteVendor> list = inviteVendorService.list(Wrappers.lambdaQuery(InviteVendor.class)
                .eq(InviteVendor::getVendorName, companyName));
        try {
            if (CollectionUtils.isNotEmpty(list)) {
                String content = "您好，您邀请的供应商(" + companyName + ")已注册提交，请及时到【供应商清单】审批";
                List<String> userList = list.stream().map(InviteVendor::getCreatedBy).collect(Collectors.toList());
                pjProjectExtClient.workNotices(content, userList);
            }
        } catch (Exception e) {
            Log.error("发送钉钉到供应商邀请人失败:" + e);
        }
    }
}
