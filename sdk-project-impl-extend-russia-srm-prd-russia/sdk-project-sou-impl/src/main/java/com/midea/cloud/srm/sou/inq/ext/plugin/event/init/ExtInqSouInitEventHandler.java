package com.midea.cloud.srm.sou.inq.ext.plugin.event.init;

import brave.internal.Nullable;
import cn.hutool.core.lang.func.LambdaUtil;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.common.enums.CategoryStatus;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendor;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPJInqSouVendorDel;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouVendorRound;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPJInqSouVendorDelDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouItemRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouVendorRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.domain.ExtInqSouDomainServiceImpl;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.spi.init.InqSouInitEventHandler;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.SouProcessEventService;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.SouProjectEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.SouVendorEditPO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouInitEventHandler extends InqSouInitEventHandler {

    @Autowired
    private ExtPJInqSouVendorDAO extPjInqSouVendorDao;
    @Autowired
    private ExtPJInqSouVendorDelDAO extPjInqSouVendorDelDao;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SouProjectDAO souProjectDao;
    @Autowired
    private InqSouItemDAO inqSouItemDao;
    @Autowired
    private SouProcessEventService souProcessEventService;
    @Autowired
    private ExtInqSouDomainServiceImpl extInqSouDomainService;
    @Autowired
    private ExtPjInqSouItemRoundDAO extPjInqSouItemRoundDao;
    @Autowired
    private ExtPjInqSouVendorRoundDAO extPjInqSouVendorRoundDao;
    @Autowired
    private SouItemDAO souItemDao;
    @Autowired
    private SouVendorDAO souVendorDao;

    @Override
    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType, SouProjectEditPO po) {
        ApiInqSouProjectInfoDTO inqParam = SouObjectXUtil.convertTargetObj(param, ApiInqSouProjectInfoDTO.class);
        extInqSouDomainService.editProjectInfo(inqParam.getProject().getProjectId(), inqParam.getProject(), param.isTempSave());
        extInqSouDomainService.editCurrency(inqParam.getProject(), inqParam.getCurrencyList());
    }

    @Override
    @ApiOperation("邀请供应商保存后的额外处理")
    public void doHandlerAfterEditVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType, SouVendorEditPO po) {
        if (!isCopy && !param.isTempSave()) {
            souProcessEventService.updateProcessNodeStatusForInit(param.getProjectId(), SouProcessNodeEnum.scoreRule.name(),
                    param.isTempSave() ? Enable.N : Enable.Y, souType);
        }
    }

    @Override
    @ApiOperation("删除寻源单后的额外处理")
    public void doHandlerAfterRemoveSou(long projectId, String souType, ApiSouInitDetailVO initInfo) {
        super.doHandlerAfterRemoveSou(projectId, souType, initInfo);
        extPjInqSouVendorDao.lambdaUpdate().eq(ExtPJInqSouVendor::getProjectId, projectId).remove();
        extPjInqSouVendorDelDao.lambdaUpdate().eq(ExtPJInqSouVendorDel::getProjectId, projectId).remove();

        if (SouSourceFromTypeEnum.PURCHASE_REQ.name().equals(initInfo.getProjectInfo().getSourceFromType())) {
            ApiInqSouInitDetailVO inqInitInfo = SouObjectXUtil.convertTargetObj(initInfo, ApiInqSouInitDetailVO.class);
            Set<Long> reqLineIds = new HashSet<>(16);
            for (ApiInqSouItemVO souItem : inqInitInfo.getRequireInfo()) {
                if (souItem.getExtSourceFromLineIds() != null) {
                    String[] arr = souItem.getExtSourceFromLineIds().split(",");
                    for (String s : arr) {
                        reqLineIds.add(Long.valueOf(s));
                    }
                }
            }
            if (!reqLineIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                        .set("ifCreateInq", Enable.N)
                        .in(RequirementLine::getRequirementLineId, new ArrayList<>(reqLineIds)));
            }
        }
    }

    @Override
    @ApiOperation("作废寻源单后的额外处理")
    public void doHandlerAfterCancelSou(ApiSouCancelDTO param, String souType) {
        super.doHandlerAfterCancelSou(param, souType);

        SouProject souProject = souProjectDao.getById(param.getProjectId());
        List<InqSouItem> inqItemList = inqSouItemDao.list(InqSouItem::getProjectId, param.getProjectId());
        if (SouSourceFromTypeEnum.PURCHASE_REQ.name().equals(souProject.getSourceFromType())) {
            Set<Long> reqLineIds = new HashSet<>(16);
            for (InqSouItem souItem : inqItemList) {
                if (souItem.getExtSourceFromLineIds() != null) {
                    String[] arr = souItem.getExtSourceFromLineIds().split(",");
                    for (String s : arr) {
                        reqLineIds.add(Long.valueOf(s));
                    }
                }
            }
            if (!reqLineIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                        .set("ifCreateInq", Enable.N)
                        .in(RequirementLine::getRequirementLineId, new ArrayList<>(reqLineIds)));
            }
        }
    }

    @Override
    @ApiOperation("复制寻源单--构造立项基本信息")
    public ApiSouProjectInfoDTO doHandlerForCopyProjectInfo(long projectId, String souType) {
        throw new IllegalArgumentException("询比价模块不支持单据复制功能");
    }

    @Override
    @ApiOperation("立项审批通过后的额外处理")
    public void doHandlerAfterApprovalPass(long projectId, String souType) {
        super.doHandlerAfterApprovalPass(projectId, souType);
        // 1: 构造物料轮次信息
        List<SouItem> souItemList = souItemDao.list(SouItem::getProjectId, projectId);
        List<ExtPjInqSouItemRound> itemRoundList = new ArrayList<>(souItemList.size()); {
            souItemList.forEach(souItem -> {
                ExtPjInqSouItemRound itemRound = new ExtPjInqSouItemRound();
                itemRoundList.add(itemRound);

                itemRound.setInqSouItemRoundId(IdGenrator.generate());
                itemRound.setProjectId(projectId);
                itemRound.setSouItemId(souItem.getSouItemId());
                itemRound.setRound(1);
                itemRound.setCanOrder(Enable.Y);
            });
        }
        // 2: 构造供应商轮次信息
        List<SouVendor> souVendorList = souVendorDao.list(SouVendor::getProjectId, projectId);
        List<ExtPjInqSouVendorRound> vendorRoundList = new ArrayList<>(souVendorList.size()); {
            souVendorList.forEach(souVendor -> {
                ExtPjInqSouVendorRound vendorRound = new ExtPjInqSouVendorRound();
                vendorRoundList.add(vendorRound);

                vendorRound.setInqSouVendorRoundId(IdGenrator.generate());
                vendorRound.setProjectId(projectId);
                vendorRound.setVendorId(souVendor.getVendorId());
                vendorRound.setRound(1);
                vendorRound.setCanOrder(Enable.Y);
            });
        }

        extPjInqSouItemRoundDao.saveBatch(itemRoundList);
        extPjInqSouVendorRoundDao.saveBatch(vendorRoundList);
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @ApiOperation("项目信息保存前的额外处理")
    @Override
    public void doHandlerBeforeEditProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType) {
        log.info("询比价保存基础信息前:" + System.currentTimeMillis());
        super.doHandlerBeforeEditProject(param, isCopy, souType);
    }

    @ApiOperation("项目需求保存前的额外处理")
    @Override
    public void doHandlerBeforeEditRequires(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType) {
        log.info("询比价保存物料需求前:" + System.currentTimeMillis());
        super.doHandlerBeforeEditRequires(param, isCopy, userId, souType);
    }

    @ApiOperation("邀请供应商保存前的额外处理")
    @Override
    public void doHandlerBeforeEditVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType) {
        log.info("询比价保存邀请供应商前:" + System.currentTimeMillis());
        log.info("询价管理-保存供应商信息-二开增强校验逻辑-"+param.getProjectId());
        //二开校验选择的供应商品类状态: 询价管理中，新增供应商和历史最低价供应商，添加时候校验 供应商在这个组织下，是否符合供货所有品类，如果不符合，给与提示
        checkEditVendorsAsExt(param);
        super.doHandlerBeforeEditVendors(param, isCopy, souType);
    }

    private void checkEditVendorsAsExt(ApiSouVendorInfoDTO param) {
        //查询头信息
        List<SouItem> souItemList = souItemDao.lambdaQuery().eq(SouItem::getProjectId, param.getProjectId()).list();
        if(CollectionUtils.isEmpty(souItemList)) {
            return;
        }

        List<Long> orgIdList = new ArrayList<>(16);
        List<Long> categoryIdList = new ArrayList<>(16);
        Map<String, String> categoryNameMap = new HashMap<>(16);

        souItemList.stream().forEach(item -> {
            addList(orgIdList, item.getOrgOuId());
            addList(categoryIdList, item.getCategoryId());
            categoryNameMap.put(StringUtils.joinWith(SrmConstant.UNDER_LINE, item.getOrgOuId(), item.getCategoryId()),
                    StringUtils.joinWith(SrmConstant.UNDER_LINE, item.getOrgOuName(), item.getCategoryName()));
        });

        Map<Long, String> vendorNameMap = new HashMap<>(16);
        List<Long> vendorIdList = param.getVendorList().stream().map(v -> v.getVendorId()).distinct().collect(Collectors.toList());

        List<RecordDTO> vendorList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER)
                .in(CompanyInfo::getCompanyId, vendorIdList));
        vendorNameMap = vendorList.stream().collect(Collectors.toMap(r -> r.get(CompanyInfo::getCompanyId), v -> v.get(CompanyInfo::getCompanyName), (k1, k2)-> k2));

        //查询供应商品类 PjOrgCategory
        List<RecordDTO> orgCategoryList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.ORG_CATEGORY)
                .in(PjOrgCategory::getOrgIdList, orgIdList)
                .in(PjOrgCategory::getCategoryId, categoryIdList)
                .in(PjOrgCategory::getCompanyId, vendorIdList));

        //校验维度 组织 + 品类 + 供应商
        Set<String> passRange = new HashSet<>(16);

        orgCategoryList.stream().forEach(record -> {
            Boolean checkOrgStatus = YesOrNo.YES.getValue().equals(Objects.toString(record.get(PjOrgCategory::getPjOrgStatus), YesOrNo.YES.getValue()));
            Boolean checkPjCategoryStatus = YesOrNo.YES.getValue().equals(Objects.toString(record.get(PjOrgCategory::getPjCategoryStatus), YesOrNo.YES.getValue()));
            Boolean checkServiceStatus = CategoryStatus.QUALIFIED.name().equals(record.getString(LambdaUtil.getFieldName(PjOrgCategory::getServiceStatus)));

            if(checkOrgStatus && checkPjCategoryStatus && checkServiceStatus) {
                passRange.add(StringUtils.joinWith(SrmConstant.UNDER_LINE,record.get(PjOrgCategory::getOrgId), record.get(PjOrgCategory::getCategoryId), record.get(PjOrgCategory::getCompanyId)));
            }
        });

        List<String> errorList = new ArrayList<>(16);

        for(String key : categoryNameMap.keySet()) {
            for(Long vendorId: vendorNameMap.keySet()) {
                String checkKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, key, vendorId);
                if(!passRange.contains(checkKey)) {
                    errorList.add(MessageFormat.format("供应商[{0}]组织_品类[{1}]无合格品类状态", vendorNameMap.get(vendorId), categoryNameMap.get(key)));
                }
            }
        }

        if(CollectionUtils.isNotEmpty(errorList)) {
            throw new BaseException(MessageFormat.format("供应商品类库校验不通过：{0}", errorList.stream().distinct().collect(Collectors.joining(SrmConstant.SIG_3))));
        }
    }

    private <T> void addList(List<T> list, T value) {
        if(Objects.isNull(value)) {
            return;
        }
        if(list.contains(value)) {
            return;
        }
        list.add(value);
    }

    @ApiOperation("评分规则保存前的额外处理")
    @Override
    public void doHandlerBeforeEditScoreRule(ApiSouInitScoreInfoDTO param, boolean isCopy, String souType) {
        log.info("询比价保存评分规则前:" + System.currentTimeMillis());
        super.doHandlerBeforeEditScoreRule(param, isCopy, souType);
    }
}
