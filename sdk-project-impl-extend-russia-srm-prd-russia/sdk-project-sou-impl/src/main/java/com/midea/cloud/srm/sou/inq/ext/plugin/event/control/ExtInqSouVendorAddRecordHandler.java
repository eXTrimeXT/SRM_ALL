package com.midea.cloud.srm.sou.inq.ext.plugin.event.control;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouVendorRecordDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorEditDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendorRecord;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouVendorAddStatusEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProcessConfigDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorRecordDAO;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendorrecord.ApiSouVendorAddRecordHandler;
import com.midea.cloud.srm.sou.sourcing.spi.control.vendorrecord.SouVendorRecordPO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
public class ExtInqSouVendorAddRecordHandler extends ApiSouVendorAddRecordHandler {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouProcessConfigDAO souProcessConfigDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private SouVendorRecordDAO souVendorRecordDAO;
    @Autowired
    private SupplierClient supplierClient;

    @Override
    public SouVendorRecordPO formatValidateAndConvert(ApiSouVendorRecordDTO param, String souType) {
        SouVendorRecordPO po = new SouVendorRecordPO(); {
            po.setSaveRecordList(new ArrayList<>(param.getVendorList().size()));
            po.setUpdateRecordList(new ArrayList<>(param.getVendorList().size()));
        }

        SouProject project = souProjectDAO.getById(param.getProjectId());
        SouProcessConfig processConfig = souProcessConfigDAO.getById(project.getProcessConfigId());
        // 1: 查询寻源单现有的供应商信息
        Map<Long/* vendorId */, SouVendor> existVendorList = souVendorDAO.list(SouVendor::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 2: 查询现有(未执行、执行失败的记录)
        Map<Long/* vendorId */, SouVendorRecord> latestRecordMap = souVendorRecordDAO.lambdaQuery()
                .eq(SouVendorRecord::getProjectId, param.getProjectId())
                .in(SouVendorRecord::getAddStatus, SouVendorAddStatusEnum.DRAFT, SouVendorAddStatusEnum.FAIL)
                .list().stream().collect(Collectors.toMap(SouVendorRecord::getVendorId, Function.identity()));
        // 3: 查询公司信息
        Map<Long/* vendorId */, CompanyInfo> companyMap = Collections.emptyMap(); {
            Set<Long> vendorIds = param.getVendorList().stream().map(ApiSouVendorEditDTO::getVendorId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!vendorIds.isEmpty()) {
                companyMap = supplierClient
                        .getComponyByIds(new ArrayList<>(vendorIds))
                        .stream().collect(Collectors.toMap(CompanyInfo::getCompanyId, Function.identity()));
            }
        }
        // 4: 查询联系人信息
        Map<Long/* vendorId */, List<ContactInfo>> contactInfoMap = supplierClient
                .listContactInfoByParam(param.getVendorList().stream().map(ApiSouVendorDTO::getVendorId).filter(Objects::nonNull).collect(Collectors.toList()))
                .stream().collect(Collectors.groupingBy(ContactInfo::getCompanyId));
        // 3: 校验、构造数据
        for (ApiSouVendorEditDTO vendorDTO : param.getVendorList()) {
            // 1: 校验
            AssertUtils.notNull(vendorDTO.getVendorId(), "缺少vendorId参数");
            vendorDTO.setLinkmanName(StringUtils.trimToNull(vendorDTO.getLinkmanName()));
            AssertUtils.isTrue(vendorDTO.getLinkmanName() == null || vendorDTO.getLinkmanName().length() <= 80, "联系人长度不能超过80");
            vendorDTO.setPhone(StringUtils.trimToNull(vendorDTO.getPhone()));
            AssertUtils.isTrue(vendorDTO.getPhone() == null || vendorDTO.getPhone().length() <= 30, "电话长度不能超过30");
            vendorDTO.setEmail(StringUtils.trimToNull(vendorDTO.getEmail()));
            AssertUtils.isTrue(vendorDTO.getEmail() == null || vendorDTO.getEmail().length() <= 100, "邮箱长度不能超过100");
            // 2: 判断是否已存在
            if (existVendorList.containsKey(vendorDTO.getVendorId())) { continue; }
            // 3: 数据处理
            if (latestRecordMap.containsKey(vendorDTO.getVendorId())) {
                // 现有的数据
                SouVendorRecord record = latestRecordMap.get(vendorDTO.getVendorId());
                po.getUpdateRecordList().add(record);
                // 联系人
                record.setLinkmanName(vendorDTO.getLinkmanName());
                // 电话
                record.setPhone(vendorDTO.getPhone());
                // 邮箱
                record.setEmail(vendorDTO.getEmail());
                // 供应商信息
                record.getVendorInfo().setLinkmanName(record.getLinkmanName());
                record.getVendorInfo().setPhone(record.getPhone());
                record.getVendorInfo().setEmail(record.getEmail());
            } else {
                // 新增数据
                SouVendorRecord record = new SouVendorRecord();
                po.getSaveRecordList().add(record);
                // ID
                record.setRecordId(IdGenrator.generate());
                // 寻源单ID
                record.setProjectId(param.getProjectId());
                // 供应商ID
                record.setVendorId(vendorDTO.getVendorId());
                CompanyInfo companyInfo = companyMap.get(record.getVendorId());
                AssertUtils.notNull(companyInfo, LocaleHandler.getLocaleMsg("供应商[{0}]不存在"), record.getVendorId());
                // 联系人
                record.setLinkmanName(vendorDTO.getLinkmanName());
                // 电话
                record.setPhone(vendorDTO.getPhone());
                // 邮箱
                record.setEmail(vendorDTO.getEmail());
                if (record.getLinkmanName() == null && record.getPhone() == null && record.getEmail() == null) {
                    ContactInfo contactInfo = null; {
                        List<ContactInfo> contactInfoList = contactInfoMap.get(record.getVendorId());
                        if (CollectionUtils.isNotEmpty(contactInfoList)) {
                            for (ContactInfo info : contactInfoList) {
                                if (Enable.Y.name().equals(info.getCeeaDefaultContact())) {
                                    contactInfo = info;
                                    break;
                                }
                            }
                            if (contactInfo == null) {
                                contactInfo = contactInfoList.get(0);
                            }
                        }
                    }
                    record.setLinkmanName(contactInfo != null ? contactInfo.getContactName() : null);
                    record.setPhone(contactInfo != null ? contactInfo.getCeeaContactMethod() : null);
                    record.setEmail(contactInfo != null ? contactInfo.getEmail() : null);
                }
                // 追加状态
                record.setAddStatus(SouVendorAddStatusEnum.DRAFT);
                // 供应商信息
                record.setVendorInfo(new SouVendor()); {
                    BeanUtils.copyProperties(record, record.getVendorInfo());
                    record.getVendorInfo().setSouVendorId(IdGenrator.generate());
                    // 报名状态/时间
                    if (Enable.Y.equals(processConfig.getSignUpManagement())) {
                        // 有报名节点
                        record.getVendorInfo().setSignUpStatus(SouSignUpStatusEnum.SIGN_UP_DONE);
                        record.getVendorInfo().setSignUpTime(new Date());
                    } else {
                        record.getVendorInfo().setSignUpStatus(SouSignUpStatusEnum.NO_SIGN_UP);
                        record.getVendorInfo().setSignUpTime(null);
                    }
                    record.getVendorInfo().setVendorCode(companyInfo.getCompanyCode());
                    record.getVendorInfo().setVendorName(companyInfo.getCompanyName());
                }
            }
        }
        // 4: 如果有供应商已被添加到寻源单中，则这部分要修改状态
        latestRecordMap.values().forEach(record -> {
            if (existVendorList.containsKey(record.getVendorId())) {
                po.getUpdateRecordList().add(record);
                record.setAddStatus(SouVendorAddStatusEnum.CANCEL);
            }
        });

        return po;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
