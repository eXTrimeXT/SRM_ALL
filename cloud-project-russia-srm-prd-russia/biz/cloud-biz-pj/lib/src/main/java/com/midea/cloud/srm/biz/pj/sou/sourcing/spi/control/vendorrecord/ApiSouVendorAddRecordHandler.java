package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendorrecord;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorRecordDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.ApiSouVendorRecordDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorEditDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorRecord;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouVendorAddStatusEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 追加供应商记录处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouVendorAddRecordHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouVendorRecordDAOImpl souVendorRecordDao;
    @Autowired
    private SupplierClient supplierClient;

    public SouVendorRecordPO formatValidateAndConvert(ApiSouVendorRecordDTO param, String souType) {
        SouVendorRecordPO po = new SouVendorRecordPO(); {
            po.setSaveRecordList(new ArrayList<>(param.getVendorList().size()));
            po.setUpdateRecordList(new ArrayList<>(param.getVendorList().size()));
        }

        SouProject project = souProjectDao.getById(param.getProjectId());
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        // 1: 查询寻源单现有的供应商信息
        Map<Long/* vendorId */, SouVendor> existVendorList = souVendorDao.list(SouVendor::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 2: 查询现有(未执行、执行失败的记录)
        Map<Long/* vendorId */, SouVendorRecord> latestRecordMap = souVendorRecordDao.lambdaQuery()
                .eq(SouVendorRecord::getProjectId, param.getProjectId())
                .in(SouVendorRecord::getAddStatus, SouVendorAddStatusEnum.DRAFT, SouVendorAddStatusEnum.FAIL)
                .list().stream().collect(Collectors.toMap(SouVendorRecord::getVendorId, Function.identity()));
        // 3: 查询公司信息
        Map<Long/* vendorId */, CompanyInfo> companyMap = supplierClient.listAllCompanyInfo()
                .stream().collect(Collectors.toMap(CompanyInfo::getCompanyId, Function.identity()));
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
                AssertUtils.notNull(companyInfo, LocaleHandler.getLocaleMsg("供应商")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), record.getVendorId());
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
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
