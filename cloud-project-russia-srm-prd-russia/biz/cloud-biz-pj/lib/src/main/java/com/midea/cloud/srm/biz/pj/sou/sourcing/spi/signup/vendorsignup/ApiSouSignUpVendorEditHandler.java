package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.signup.vendorsignup;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouFileEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpFileDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSignUpFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSignUpStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.risk.dto.MonitoringDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 供应商报名校验转换处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/02
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouSignUpVendorEditHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouFileDAOImpl souFileDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SupplierClient supplierClient;

    /**
     * 供应商报价数据处理
     *
     * @param param   供应商报名信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    public SouVendorSignUpPO formatValidateAndConvert(ApiSouSignUpVendorDTO param, String souType) {
        // 1: 数据校验处理
        this.formatAndValidateSignUpFiles(param, param.isTempSave());
        // 2: 数据转换
        SouVendorSignUpPO po = this.convert(param, param.isTempSave());
        // 3: 校验供应商风险
        this.checkVendorRiskForSignUp(param.getProjectId(), param.getVendorId());

        return po;
    }

    protected void formatAndValidateSignUpFiles(ApiSouSignUpVendorDTO dto, boolean isTempSave) {
        if (CollectionUtils.isEmpty(dto.getSignUpFileList())) {
            AssertUtils.isTrue(isTempSave, "缺少报名附件信息");
            return;
        }
        int index = 0;
        for (ApiSouSignUpFileDTO file : dto.getSignUpFileList()) {
            index++;
            AssertUtils.notNull(file.getSignUpDocId(), LocaleHandler.getLocaleMsg("报价附件第") + "{0}" + LocaleHandler.getLocaleMsg("行请上传附件"), index);
            file.setSignUpFileName(StringUtils.trimToNull(file.getSignUpFileName()));
            AssertUtils.isTrue(file.getSignUpFileName().length() <= 150, LocaleHandler.getLocaleMsg("报价附件第") + "{0}" + LocaleHandler.getLocaleMsg("行附件名称长度不能超过150"), index);
            file.setSignUpRemark(StringUtils.trimToNull(file.getSignUpRemark()));
            if (file.getSignUpRemark() != null) {
                AssertUtils.isTrue(file.getSignUpRemark().length() <= 300, LocaleHandler.getLocaleMsg("报价附件第") + "{0}" + LocaleHandler.getLocaleMsg("行备注的输入长度不能超过300"), index);
            }
        }
    }

    protected SouVendorSignUpPO convert(ApiSouSignUpVendorDTO dto, boolean isTempSave) {
        SouVendorSignUpPO po = new SouVendorSignUpPO();
        po.setVendor(this.doConvertVendor(dto.getProjectId(), dto.getVendorId(), isTempSave, dto.getDepositPayTime()));
        po.setSignUpFileList(this.doConvertFiles(dto, po.getVendor(), isTempSave));
        po.setBondPayList(this.doConvertBondFiles(dto, po.getVendor(), isTempSave));
        return po;
    }

    protected List<SouFile> doConvertBondFiles(ApiSouSignUpVendorDTO dto, SouVendor vendor, boolean isTempSave) {
        List<SouFile> entityList = new ArrayList<>();
        for (ApiSouFileEditDTO apiSouFileEditDTO : dto.getBondFileList()) {
            SouFile entity = SouObjectXUtil.convertTargetObj(apiSouFileEditDTO, SouFile.class);
            entityList.add(entity);
            entity.setProjectId(dto.getProjectId());
        }

        return entityList;
    }

    protected List<SouSignUpFile> doConvertFiles(ApiSouSignUpVendorDTO dto, SouVendor vendor, boolean isTempSave) {
        List<SouSignUpFile> entityList = new ArrayList<>();

        for (ApiSouSignUpFileDTO fileDTO : dto.getSignUpFileList()) {

            SouSignUpFile entity = SouObjectXUtil.convertTargetObj(fileDTO, SouSignUpFile.class);
            entityList.add(entity);
            // ID
            if (entity.getSignUpFileId() == null) {
                entity.setSignUpFileId(IdGenrator.generate());
            }
            entity.setSouVendorId(vendor.getSouVendorId());
            entity.setProjectId(dto.getProjectId());
            entity.setVendorId(dto.getVendorId());
        }

        return entityList;
    }

    @Nullable
    protected SouVendor doConvertVendor(long projectId, long vendorId, boolean isTempSave, Date depositPayTime) {
        SouProject project = souProjectDao.getById(projectId);
        boolean isOpen = SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope());
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        if (vendor == null) {
            // 首次公开报名

            Map<Long/* vendorId */, CompanyInfo> companyMap = supplierClient
                    .getComponyByIds(Collections.singletonList(vendorId))
                    .stream().collect(Collectors.toMap(CompanyInfo::getCompanyId, Function.identity()));

            vendor = new SouVendor();
            // 1: ID
            vendor.setSouVendorId(IdGenrator.generate());
            // 2: 寻源单ID
            vendor.setProjectId(projectId);
            // 3: 供应商ID
            vendor.setVendorId(vendorId);
            CompanyInfo companyInfo = companyMap.get(vendorId);
            AssertUtils.notNull(companyInfo, LocaleHandler.getLocaleMsg("供应商信息") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), vendorId);
            // 4: 供应商编码
            vendor.setVendorCode(companyInfo.getCompanyCode());
            // 5: 供应商名称
            vendor.setVendorName(companyInfo.getCompanyName());
            // 6: 加入轮次
            vendor.setJoinRound(project.getCurrentRound());
            // 7: 报名状态
            vendor.setSignUpStatus(SouSignUpStatusEnum.CONFIRM_ING);
            vendor.setSignUpTime(new Date());
            // 9: 报名驳回原因
            vendor.setSignUpRejectReason(null);
            // 10: 联系人名称
            vendor.setLinkmanName(null);
            // 11: 电话
            vendor.setPhone(null);
            // 12: 邮箱
            vendor.setEmail(null);
            // 13: 排序
            vendor.setSortIndex((int) (souVendorDao.lambdaQuery().eq(SouVendor::getProjectId, projectId).count() + 1));
            // 14: 保证金缴纳时间
            vendor.setDepositPayTime(depositPayTime);
        } else {
            // 非首次公开报名、邀请报名
            vendor.setSignUpStatus(SouSignUpStatusEnum.CONFIRM_ING);
            vendor.setSignUpTime(isTempSave ? null : new Date());
            vendor.setDepositPayTime(depositPayTime);
        }

        return vendor;
    }

    /**
     * 供应商风险不会反馈到报价权限上，报价权限属于采购商的强制限制，而供应商风险属于临时性动态限制。
     * 1. 供应商就算处于风险状态，也可以在寻源列表中看到寻源单据。
     * 2. 如果是供应商维度限制(不能投标)，禁止报名。
     * 3. 如果是供应商维度限制(不能中标)，可以报名。
     * 4. 如果是供应商+品类维度(不能投标)，可以报名。
     * 5. 如果是供应商+品类维度(不能中标)，可以报名。
     */
    protected void checkVendorRiskForSignUp(long projectId, long vendorId) {
        MonitoringDTO param = new MonitoringDTO();
        param.setVendorId(vendorId);
        List<MonitoringDTO> monitorList = supplierClient.listMonitoringByCompanyId(param);
        if (CollectionUtils.isNotEmpty(monitorList)) {
            // 判断供应商维度
            List<MonitoringDTO> globals = monitorList.stream()
                    .filter(e -> e.getCategoryId() == null)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(globals)) {
                globals.forEach(global ->
                        AssertUtils.isFalse(Enable.Y.equals(global.getNoBid()), "供应商被限制禁止投标(供应商风险)，不能报名")
                );
            }
            // 不用判断供应商+品类维度
        }
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
