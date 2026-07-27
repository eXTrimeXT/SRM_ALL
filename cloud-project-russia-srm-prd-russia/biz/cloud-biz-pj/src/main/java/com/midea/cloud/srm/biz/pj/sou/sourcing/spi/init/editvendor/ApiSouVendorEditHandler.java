package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.init.editvendor;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.utils.RegexUtil;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
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
 * 寻源openAPI - 立项邀请供应商保存校验转换服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouVendorEditHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SupplierClient supplierClient;

    public SouVendorEditPO formatValidateAndConvert(ApiSouVendorInfoDTO param, boolean isCopy, String souType) {
        /* 1: 数据格式化及校验 */
        this.formatAndValidate(param.getProjectId(), param.getVendorList(), param.isTempSave(), isCopy);
        /* 2: 数据转换 */
        SouVendorEditPO po = this.convert(param.getProjectId(), param.getVendorList(), param.isTempSave(), isCopy);
        /* 3: 校验供应商风险 */
        this.checkVendorRiskForInit(param.getProjectId(), po.getVendorList(), param.isTempSave());

        return po;
    }

    /** 数据格式化及校验 */
    protected void formatAndValidate(long projectId, List<ApiSouVendorDTO> params, boolean isTempSave, boolean isCopy) {
        this.formatAndValidateVendors(projectId, params, isTempSave, isCopy);
        this.formatAndValidateAuths(projectId, params, isTempSave, isCopy);
    }

    private void formatAndValidateVendors(long projectId, @Nullable List<ApiSouVendorDTO> params, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isEmpty(params)) {
            AssertUtils.isTrue(isTempSave || isCopy, "缺少供应商信息");
            return;
        }
        int index = 0;
        Set<Long> vendorIds = new HashSet<>(params.size());
        boolean isProjectInit;
        {
            SouProject project = souProjectDao.getById(projectId);
            isProjectInit = SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus());
        }
        for (ApiSouVendorDTO vendor : params) {
            index++;
            /* 1: ID */
            /* 3: 供应商ID */
            AssertUtils.notNull(vendor.getVendorId(), "供应商列表第{0}行请选择供应商", index);
            AssertUtils.isTrue(vendorIds.add(vendor.getVendorId()), "供应商列表第{0}行供应商重复", index);
            /* 10: 联系人名称 */
            vendor.setLinkmanName(StringUtils.trimToNull(vendor.getLinkmanName()));
            if (vendor.getLinkmanName() != null) {
                AssertUtils.isTrue(vendor.getLinkmanName().length() <= 50, "供应商列表第{0}行联系人名称的长度不能超过50", index);
            }
            /* 11: 电话 */
            vendor.setPhone(StringUtils.trimToNull(vendor.getPhone()));
            if (vendor.getPhone() != null) {
                AssertUtils.isTrue(vendor.getPhone().length() <= 50, "供应商列表第{0}行电话的长度不能超过50", index);
            }
            /* 12: 邮箱 */
            vendor.setEmail(StringUtils.trimToNull(vendor.getEmail()));
            if (vendor.getEmail() != null) {
                AssertUtils.isTrue(RegexUtil.REGEX_EMAIL.matcher(vendor.getEmail()).matches(), "供应商列表第{0}行邮箱格式错误", index);
                AssertUtils.isTrue(vendor.getEmail().length() <= 100, "供应商列表第{0}行邮箱的长度不能超过100", index);
            }
        }

        /* 如果是非立项时添加的供应商，还需要确保添加的供应商与现有的不重复 */
        if (!isProjectInit) {
            Set<Long> existVendorIds = souVendorDao.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    .list()
                    .stream().map(SouVendor::getVendorId).collect(Collectors.toSet());
            if (!existVendorIds.isEmpty()) {
                vendorIds.forEach(vendorId ->
                        AssertUtils.isFalse(existVendorIds.contains(vendorId), "供应商已存在，请勿重复添加"));
            }
        }
    }

    private void formatAndValidateAuths(long projectId, @Nullable List<ApiSouVendorDTO> params, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isEmpty(params)) {
            AssertUtils.isTrue(isTempSave || isCopy, "缺少供应商信息");
            return;
        }
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDao.lambdaQuery()
                .eq(SouItem::getProjectId, projectId)
                .list()
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        SouProject project = souProjectDao.getById(projectId);

        for (ApiSouVendorDTO vendor : params) {
            if (vendor.getAuthList() == null) {
                vendor.setAuthList(new ArrayList<>(souItemMap.size()));
            }
            boolean allHasSouItemId = vendor.getAuthList().stream().map(ApiSouVendorAuthEditDTO::getSouItemId).allMatch(Objects::nonNull);
            AssertUtils.isTrue(allHasSouItemId, "报价权限数据缺少souItemId参数");

            Map<Long/* souItemId */, ApiSouVendorAuthEditDTO> authMap = vendor.getAuthList().stream()
                    .collect(Collectors.toMap(ApiSouVendorAuthEditDTO::getSouItemId, Function.identity()));
            List<ApiSouVendorAuthEditDTO> resultList = new ArrayList<>(souItemMap.size());

            boolean isGroup = SouOrderWayEnum.COMBINED.equals(project.getOrderWay());
            Set<String> forbidItemGroups = new HashSet<>(souItemMap.size());
            souItemMap.forEach((souItemId, item) -> {
                ApiSouVendorAuthEditDTO auth = authMap.get(souItemId);
                if (auth == null) {
                    auth = new ApiSouVendorAuthEditDTO();
                    auth.setSouItemId(souItemId);
                    auth.setForbidPrice(Enable.N);
                } else {
                    if (auth.getForbidPrice() == null) {
                        auth.setForbidPrice(Enable.N);
                    }
                }
                if (isGroup) {
                    if (forbidItemGroups.contains(item.getItemGroup())) {
                        AssertUtils.isTrue(Enable.Y.equals(auth.getForbidPrice()), "同组合下，必须都禁止报价或都不禁止报价");
                    }
                    if (Enable.Y.equals(auth.getForbidPrice())) {
                        forbidItemGroups.add(item.getItemGroup());
                    }
                }
                resultList.add(auth);
            });
            vendor.setAuthList(resultList);
        }
    }

    /**
     * 数据转换
     * @param projectId
     * @param params
     * @param isTempSave
     * @param isCopy
     * @return
     */
    protected SouVendorEditPO convert(long projectId, List<ApiSouVendorDTO> params, boolean isTempSave, boolean isCopy) {
        SouVendorEditPO po = new SouVendorEditPO();
        po.setVendorList(this.doConvertVendors(projectId, params, isTempSave, isCopy));
        po.setAuthList(this.doConvertAuths(projectId, params));
        return po;
    }

    protected List<SouVendor> doConvertVendors(long projectId, List<ApiSouVendorDTO> params, boolean isTempSave, boolean isCopy) {
        if (CollectionUtils.isEmpty(params)) {
            return new ArrayList<>();
        }
        Map<Long/* vendorId */, CompanyInfo> companyMap = supplierClient
                .getComponyByIds(params.stream().map(ApiSouVendorDTO::getVendorId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(CompanyInfo::getCompanyId, Function.identity()));
        SouProject project = souProjectDao.getById(projectId);

        List<SouVendor> vendorList = new ArrayList<>(params.size());
        int index = 0;
        for (ApiSouVendorDTO param : params) {
            index++;
            SouVendor vendor = SouObjectXUtil.convertTargetObj(param, SouVendor.class);
            vendorList.add(vendor);

            //ID
            if (vendor.getSouVendorId() == null) {
                vendor.setSouVendorId(IdGenrator.generate());
            }
            //寻源单ID
            vendor.setProjectId(projectId);
            //供应商ID
            vendor.setVendorId(vendor.getVendorId());
            CompanyInfo companyInfo = companyMap.get(vendor.getVendorId());
            AssertUtils.notNull(companyInfo, LocaleHandler.getLocaleMsg("供应商") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), vendor.getVendorId());
            //供应商编码
            vendor.setVendorCode(companyInfo.getCompanyCode());
            //供应商名称
            vendor.setVendorName(companyInfo.getCompanyName());
            //加入轮次
            vendor.setJoinRound(1);
            //报名状态/时间
            if (SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()) || SouProjectStatusEnum.ACCEPT_SIGN_UP.equals(project.getProjectStatus())) {
                //立项
                vendor.setSignUpStatus(SouSignUpStatusEnum.NO_SIGN_UP);
            } else {
                //发起新一轮时添加供应商
                vendor.setSignUpStatus(SouSignUpStatusEnum.SIGN_UP_DONE);
                vendor.setSignUpTime(new Date());
            }
            //联系信息
            vendor.setLinkmanName(vendor.getLinkmanName());
            vendor.setPhone(vendor.getPhone());
            vendor.setEmail(vendor.getEmail());
            //排序
            vendor.setSortIndex(index);

            SouObjectXUtil.mergeProperties(vendor, param);
        }
        return vendorList;
    }

    protected List<SouVendorAuth> doConvertAuths(long projectId, @Nullable List<ApiSouVendorDTO> params) {
        if (CollectionUtils.isEmpty(params)) {
            return new ArrayList<>();
        }
        List<SouVendorAuth> entityList = new ArrayList<>(params.size() << 3);

        for (ApiSouVendorDTO vendor : params) {
            Long vendorId = vendor.getVendorId();
            for (ApiSouVendorAuthEditDTO auth : vendor.getAuthList()) {
                SouVendorAuth entity = SouObjectXUtil.convertTargetObj(auth, SouVendorAuth.class);
                entityList.add(entity);

                /* ID */
                if (entity.getVendorAuthId() == null) {
                    entity.setVendorAuthId(IdGenrator.generate());
                }
                /* 供应商表ID */
                entity.setSouVendorId(vendor.getSouVendorId());
                /* 寻源单ID */
                entity.setProjectId(projectId);
                entity.setVendorId(vendorId);

                SouObjectXUtil.mergeProperties(entity, auth);
            }
        }
        return entityList;
    }

    /**
     * 供应商风险不会反馈到报价权限上，报价权限属于采购商的强制限制，而供应商风险属于临时性动态限制。
     * 公开招标：无操作
     * 邀请招标：
     * 1. 需校验供应商风险
     * 2. 如果供应商维度限制(不能投标)，不能邀请
     * 3. 如果是供应商维度限制(不能中标)，可以邀请
     * 4. 如果是供应商+品类维度(不能投标)，可以邀请
     * 5. 如果是供应商+品类维度(不能中标)，可以邀请
     */
    public void checkVendorRiskForInit(long projectId, List<SouVendor> vendorList, boolean isTempSave) {
        if (vendorList.isEmpty()) {
            return;
        }
        SouProject project = souProjectDao.getById(projectId);
        if (SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope())) {
            return;
        }

        Map<Long/* vendorId */, List<MonitoringDTO>> monitorMap = supplierClient.listMonitoringByCompanyIds(
                vendorList.stream().map(SouVendor::getVendorId).collect(Collectors.toSet()));
        for (SouVendor vendor : vendorList) {
            List<MonitoringDTO> monitorList = monitorMap.get(vendor.getVendorId());
            if (CollectionUtils.isEmpty(monitorList)) {
                continue;
            }

            /* 判断供应商维度 */
            List<MonitoringDTO> globals = monitorList.stream()
                    .filter(e -> e.getCategoryId() == null)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(globals)) {
                globals.forEach(global ->
                        AssertUtils.isFalse(Enable.Y.equals(global.getNoBid()), "供应商[{0}]被限制禁止投标(供应商风险)，不能邀请", vendor.getVendorName())
                );
            }
            /* 不用判断供应商+品类维度 */
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
