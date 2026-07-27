package com.midea.cloud.srm.sou.designplans.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.SouCommonUtil;
import com.midea.cloud.srm.feign.SouExtRbacClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.*;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouVendorSourceFromTypeEnum;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandSup;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDesignPlan;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChPaaAdjust;
import com.midea.cloud.srm.model.sou.designplans.enums.DesignPlanEnums;
import com.midea.cloud.srm.model.sou.designplans.excel.ImportExcelReqInfoDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitScoreInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.sou.designplans.mapper.DesignPlanMapper;
import com.midea.cloud.srm.sou.designplans.service.DemandSupService;
import com.midea.cloud.srm.sou.designplans.service.DemandYearDataService;
import com.midea.cloud.srm.sou.designplans.service.DesignPlanService;
import com.midea.cloud.srm.sou.designplans.service.PaaAdjustService;
import com.midea.cloud.srm.sou.sourcing.init.service.SouInitEventService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DesignPlanServiceImpl extends BaseServiceImpl<DesignPlanMapper, SccSouChDesignPlan> implements DesignPlanService {

    @Resource
    private DemandYearDataService demandYearDataService;
    @Autowired
    private SouExtRbacClient souExtRbacClient;
    @Autowired
    private SouInitEventService souInitEventService;
    @Autowired
    private DemandSupService demandSupService;
    @Autowired
    private SupplierClient supplierClient;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private PaaAdjustService paaAdjustService;

    private final static int NUM_ONE_THOUSAND=1000;

    /**
     * 导入
     *
     * @param dataList     附件
     * @param designId 提报策划方案id
     * @return 返回
     */
    @Override
    public List<ImportExcelReqInfoDto> importReqInfo(List<ImportExcelReqInfoDto> dataList, Long designId) {
        List<SccSouChDemandYearData> reList = new ArrayList<>();
        if (dataList.size() > NUM_ONE_THOUSAND) {
            throw new BaseException("数据行超过了1000行");
        }
        //获取区域字典
        List<DictItem> dictItems = baseClient.listDictItemByDictCode("REGION");
        //获取税率
        List<PurchaseTax> purchaseTaxList = baseClient.listTaxAll();
        //物料编码
        List<String> materialCodeList = new ArrayList<>();

        dataList.forEach(e -> {
            materialCodeList.add(e.getMaterialCode());
        });
        boolean err = false;
        List<SccSouChDemandYearData> ydList = demandYearDataService.list(new LambdaQueryWrapper<SccSouChDemandYearData>().eq(SccSouChDemandYearData::getDesignId, designId));
        //查询物料信息
        List<MaterialItem> list = baseClient.listMaterialByCodeBatch(materialCodeList);
        Map<String, MaterialItem> materialItemMap = list.stream().collect(Collectors.toMap(MaterialItem::getMaterialCode, item -> item));

        Map<String, SccSouChDemandYearData> demandYearMap = new HashMap<>(15);
        for (SccSouChDemandYearData a : ydList) {
            String str = String.join("-", a.getAreaName(), a.getBrand(), a.getMaterialCode());
            demandYearMap.put(str, a);
        }
        for (ImportExcelReqInfoDto e : dataList) {
            StringBuilder errInfo = new StringBuilder();
            SccSouChDemandYearData yearData = new SccSouChDemandYearData();
            BeanUtils.copyProperties(e, yearData);
            String str = String.join("-", e.getAreaName(), e.getBrand(), e.getMaterialCode());
            if(demandYearMap.containsKey(str)) {
                yearData.setYearId(demandYearMap.get(str).getYearId());
            }

            yearData.setDesignId(designId);
            yearData.setType(3);
            Optional<DictItem> dictItem = dictItems.stream()
                    .filter(item -> yearData.getAreaName().equals(item.getDictItemName()))
                    .findFirst();
            if (!dictItem.isPresent()) {
                errInfo.append("区域无法匹配到数据;");
            } else {
                //区域编码
                yearData.setAreaCode(dictItem.get().getDictItemCode());
                //区域ID
                yearData.setAreaId(dictItem.get().getDictItemId());
            }
            //获取税率
            PurchaseTax purchaseTax = SouCommonUtil.getTax(yearData, purchaseTaxList);
            yearData.setTaxRate(purchaseTax.getTaxCode());
            //含税单价=未税单价x(1+税率)
            yearData.setRatePrice(NumberUtil.mul(yearData.getPriceTax(), NumberUtil.add(1, NumberUtil.div(yearData.getTaxRate(), 100))));
            //金额=未税单价*数量
            yearData.setMoneyAmount(NumberUtil.mul(yearData.getPriceTax(), yearData.getOrderNum()));
            //价税合计=含税单价*数量
            yearData.setPriceTotal(NumberUtil.mul(yearData.getRatePrice(), yearData.getOrderNum()));
            //来源
            yearData.setDataSource("手工");
            MaterialItem materialItem = materialItemMap.get(yearData.getMaterialCode());
            if (materialItem == null) {
                errInfo.append("物料编码系统数据为空;");
            } else {
                yearData.setMaterialId(materialItem.getMaterialId());
                yearData.setMaterialCode(materialItem.getMaterialCode());
                yearData.setMaterialName(materialItem.getMaterialName());
                //规格型号
                yearData.setModel(materialItem.getMaterialType());
                yearData.setUnit(materialItem.getUnitName());
                yearData.setUnitCode(materialItem.getUnit());
            }
            if (StringUtils.isNotBlank(errInfo)) {
                err = true;
                e.setErrInfo(errInfo.toString());
            }
            reList.add(yearData);
        }
        if (err) {
            return dataList;
        }
        demandYearDataService.saveOrUpdateBatch(reList);
        return new ArrayList<>();
    }

    /**
     * 创建集采询比价
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ApiSouInitDTO createPurInq(long designId) {
        // 1: 校验
        SccSouChDesignPlan designPlan = this.getById(designId);
        AssertUtils.notNull(designPlan, "提报策划信息[{0}]不存在", designId);
        AssertUtils.isTrue(DesignPlanEnums.APPROVED.name().equals(designPlan.getStatus()), "未审批通过，不能创建集采询比价");
//        AssertUtils.isTrue(Enable.N.equals(designPlan.getHasCreatePurInq()), "已创建询比价[{0}]", designPlan.getSouNo());
        SccSouChPaaAdjust adjust = paaAdjustService.lambdaQuery()
                .eq(SccSouChPaaAdjust::getJcId, designId)
                .list().stream().findFirst().orElse(null);
        // 2: 构造集采询比价数据
        ExtPurInqSouInitDTO inqParam = new ExtPurInqSouInitDTO(); {
            // 2.1: 项目信息
            ApiPurInqSouProjectInfoDTO projectInfo = new ApiPurInqSouProjectInfoDTO();
            inqParam.setProjectInfo(projectInfo); {
                // 2.1.1: 基本信息
                ApiPurInqSouProjectEditDTO project = new ApiPurInqSouProjectEditDTO();
                projectInfo.setProject(project); {
                    // 2.1.1.1: ID(略)
                    // 2.1.1.2: 寻源单号(略)
                    // 2.1.1.3: 寻源单名称
                    project.setSouName("提报策划转询比价_" + System.currentTimeMillis());
                    // 2.1.1.4: 流程配置ID(略)
                    // 2.1.1.5: 评选方式
                    project.setScoreRuleType(SouScoreRuleTypeEnum.MIN_PRICE);
                    // 2.1.1.6: 评分模板ID(略)
                    // 2.1.1.7: 评分模板名称(略)
                    // 2.1.1.8: 本位币
                    project.setStandardCurrency("RMB");
                    // 2.1.1.9: 本位币价格精度
                    project.setPricePrecision(4);
                    // 2.1.1.10: 投标控制-是否密封报价
                    project.setNeedEncryptPrice(Enable.Y);
                    // 2.1.1.11: 预计报价地点(略)
                    // 2.1.1.12: 是否同步至价格库
                    project.setIsSyncToPriceLibrary(Enable.N);
                    // 2.1.1.13: 生成价格审批单方式(略)
                    // 2.1.1.14: 是否允许物料变更
                    project.setAllowItemChange(Enable.N);
                    // 2.1.1.15: 是否允许中途追加供应商
                    project.setAllowNewVendors(Enable.Y);
                    // 2.1.1.16: 是否允许代理报价
                    project.setAllowProxyOrder(Enable.Y);
                    // 2.1.1.17: 价格有效期范围(略)
                    // 2.1.1.18: 报名时间范围(略)
                    // 2.1.1.19: 报价时间范围(略)
                    // 2.1.1.20: 最早开标时间(略)
                    // 2.1.1.21: 发布范围
                    project.setPublishScope(SouPublishScopeEnum.INVITE_TENDER);
                    // 2.1.1.22: 报价方式
                    project.setOrderWay(SouOrderWayEnum.SINGLE);
                    // 2.1.1.23: 报价类型
                    project.setOrderType(SouOrderTypeEnum.SIMPLE);
                    // 2.1.1.24: 投标控制-是否允许供应商撤回报价
                    project.setAllowWithdraw(Enable.Y);
                    // 2.1.1.25: 投标控制-是否允许供应商只对部分物料报价
                    project.setAllowPartPrice(Enable.Y);
                    // 2.1.1.26: 是否使用未税报价
                    project.setIsPriceNotax(Enable.Y);
                    // 2.1.1.27: 供应商信息
                    ExtUser user = souExtRbacClient.getByUserId(AppUserUtil.getLoginAppUser().getUserId());
                    AssertUtils.notNull(user, "找不到当前登录人的user信息:{0}", AppUserUtil.getLoginAppUser().getUserId());
                    project.setLinkman(user.getNickname());
                    project.setTel(user.getPhone());
                    project.setEmail(user.getEmail());
                    // 2.1.1.28: 关联上游信息
                    project.setSourceFromType("DESIGN_PLAN");
                    project.setSourceFromId(designPlan.getDesignId());
                    project.setSourceFromNo(designPlan.getProjectCode());
                    // 2.1.1.29: 采购申请部门
                    project.setOrgDeptId(user.getCeeaDeptId() != null ? Long.valueOf(user.getCeeaDeptId()) : null);
                    project.setOrgDeptName(user.getDepartment());
                    // 2.1.1.30: 项目策划方案ID
                    project.setDesignId(designPlan.getDesignId());
                    // 2.1.1.31: 项目策划方案编码
                    project.setDesignProjectCode(designPlan.getProjectCode());
                    // 2.1.1.32: 项目策划方案名称
                    project.setDesignProjectName(designPlan.getProjectName());
                    // 2.1.1.33: 项目策划轮数
                    project.setDesignNum(designPlan.getNum());
                    // 2.1.1.34: 项目策划创建人账号
                    project.setDesignCreateUsername(designPlan.getCreatedBy());
                    // 2.1.1.35: 项目策划创建人昵称
                    project.setDesignCreateNickName(designPlan.getCreatedFullName());
                    // 2.1.1.36: 项目策划创建人联系方式
                    project.setDesignCreatePhone(designPlan.getPhone());
                    // 2.1.1.37: 项目策划部门
                    project.setDesignOrgDeptId(designPlan.getDepId());
                    project.setDesignOrgDeptCode(designPlan.getDepCode());
                    project.setDesignOrgDeptName(designPlan.getDepName());
                    // 2.1.1.38: 项目策划项目金额(万元)
                    project.setDesignProjMoney(designPlan.getProjMoney());
                    // 2.1.1.39: 项目策划供货范围
                    project.setDesignArea(designPlan.getArea());
                    // 2.1.1.40: 调价申请单号
                    project.setAdjustCode(adjust != null ? adjust.getAdjustCode() : null);
                    // 2.1.1.41: 调价申请单名称
                    project.setAdjustName(adjust != null ? adjust.getAdjustName() : null);
                    // 2.1.1.42: 项目介绍
                    project.setDesignProjIntroduce(designPlan.getProjIntroduce());
                    // 2.1.1.43: 定价思路
                    project.setDesignPricingIdeas(designPlan.getPricingIdeas());
                }
            }
            // 2.2: 需求信息
            ApiPurInqSouRequireInfoDTO requireInfo = new ApiPurInqSouRequireInfoDTO();
            inqParam.setRequireInfo(requireInfo); {
                requireInfo.setItemList(new ArrayList<>(10));

                List<SccSouChDemandYearData> dataList = demandYearDataService.lambdaQuery()
                        .eq(SccSouChDemandYearData::getDesignId, designId)
                        .eq(SccSouChDemandYearData::getType, 3)
                        .list();
                AssertUtils.notEmpty(dataList, "项目策划缺少物料信息");
                for (SccSouChDemandYearData data : dataList) {
                    ApiPurInqSouItemDTO inqItem = new ApiPurInqSouItemDTO();
                    requireInfo.getItemList().add(inqItem);

                    // 2.2.1: ID(略)
                    // 2.2.2: 业务实体(略)
                    // 2.2.3: 库存组织(略)
                    // 2.2.4: 物料组合(略)
                    // 2.2.5: 是否无料号物料
                    AssertUtils.notNull(data.getMaterialId(), "转集采询比价时不支持无编码物料");
                    inqItem.setNoCodeItem(data.getMaterialId() != null ? Enable.N : Enable.Y);
                    // 2.2.6: 物料
                    inqItem.setItemId(data.getMaterialId());
                    inqItem.setItemDesc(data.getMaterialName());
                    // 2.2.7: 单位
                    inqItem.setUnit(data.getUnitCode());
                    // 2.2.8: 品类
                    inqItem.setCategoryId(data.getCategoryId());
                    // 2.2.9: 需求数量
                    inqItem.setRequireQuantity(data.getOrderNum());
                    // 2.2.10: 需求时间(略)
                    // 2.2.11: 预计采购金额(略)
                    // 2.2.12: 价格有效期范围(略)
                    // 2.2.13: 来源信息
                    inqItem.setSourceFromId(designPlan.getDesignId());
                    inqItem.setSourceFromNo(designPlan.getProjectCode());
                    inqItem.setSourceFromLineId(data.getYearId());
                    inqItem.setSourceFromType("DESIGN_PLAN");
                    // 2.2.14: 是否阶梯报价
                    inqItem.setIsLadder(Enable.N);
                    // 2.2.15: 供货范围
                    inqItem.setArea(data.getAreaCode());
                    // 2.2.16: 规格型号
                    inqItem.setModel(data.getModel());
                    // 2.2.17: 品牌
                    inqItem.setBrand(data.getBrand());
                    // 2.2.18: 历史供应商
                    inqItem.setDesignVendorId(data.getSupId());
                    inqItem.setDesignVendorCode(data.getSupCode());
                    inqItem.setDesignVendorName(data.getSupName());
                    // 2.2.19: 历史未税价格
                    inqItem.setDesignNotaxPrice(data.getPriceTax());
                }
            }
            // 2.3: 邀请供应商信息
            ApiPurInqSouVendorInfoDTO vendorInfo = new ApiPurInqSouVendorInfoDTO();
            inqParam.setVendorInfo(vendorInfo); {
                vendorInfo.setVendorList(new ArrayList<>(10));

                List<SccSouChDemandSup> supList = demandSupService.lambdaQuery().eq(SccSouChDemandSup::getDesignId, designId).list();
                for (SccSouChDemandSup sup : supList) {
                    ApiPurInqSouVendorDTO vendor = new ApiPurInqSouVendorDTO();
                    vendorInfo.getVendorList().add(vendor);

                    // 2.3.1: ID(略)
                    // 2.3.2: 供应商ID
                    vendor.setVendorId(Long.valueOf(sup.getSupId()));
                    // 2.3.3: 数据来源
                    vendor.setSourceFromType(ExtPurInqSouVendorSourceFromTypeEnum.DESIGN);
                    vendor.setNewVendorTag(Enable.N);
                }

                if (CollectionUtils.isNotEmpty(vendorInfo.getVendorList())) {
                    Map<Long/* vendorId */, List<ContactInfo>> contactInfoMap = Collections.emptyMap(); {
                        Set<Long> vendorIds = vendorInfo.getVendorList().stream()
                                .map(ApiPurInqSouVendorDTO::getVendorId).filter(Objects::nonNull).collect(Collectors.toSet());
                        if (!vendorIds.isEmpty()) {
                            contactInfoMap =  supplierClient.listContactInfoByParam(new ArrayList<>(vendorIds))
                                    .stream().collect(Collectors.groupingBy(ContactInfo::getCompanyId));
                        }
                    }
                    for (ApiPurInqSouVendorDTO vendor : vendorInfo.getVendorList()) {
                        ContactInfo  contactInfo = null; {
                            List<ContactInfo> contactInfoList = contactInfoMap.get(vendor.getVendorId());
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
                        vendor.setLinkmanName(contactInfo != null ? contactInfo.getContactName() : null);
                        vendor.setPhone(contactInfo != null ? contactInfo.getCeeaContactMethod() : null);
                        vendor.setEmail(contactInfo != null ? contactInfo.getEmail() : null);
                    }
                }

                if (CollectionUtils.isEmpty(vendorInfo.getVendorList())) {
                    inqParam.setVendorInfo(null);
                }
            }
            // 2.4: 评分规则
            ApiSouInitScoreInfoDTO scoreInfo = new ApiSouInitScoreInfoDTO(); {
                inqParam.setScoreInfo(scoreInfo);
                scoreInfo.setScoreRuleType(SouScoreRuleTypeEnum.MIN_PRICE);
            }
            // 2.4: 保存步骤
            inqParam.setCreateStep(ApiSouInitDTO.CreateStep.inviteVendor);
            inqParam.setTempSave(true);
            inqParam.setCopy(true);
        }
        // 3: 创建询比价
        ApiSouInitDTO param = SouObjectXUtil.convertTargetObj(inqParam, ApiSouInitDTO.class);
        souInitEventService.editInitInfo(param, ExtPurInqSouTypeEnum.ext_pur_inq.name());
        // 4: 回写上游
        this.lambdaUpdate()
                .set(SccSouChDesignPlan::getHasCreatePurInq, Enable.Y)
                .set(SccSouChDesignPlan::getSouNo, param.getProjectInfo().getProject().getSouNo())
                .eq(SccSouChDesignPlan::getDesignId, designId)
                .update();

        return param;
    }

}
