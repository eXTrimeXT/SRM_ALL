package com.midea.cloud.srm.sou.purinq.plugin.event.init;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouProjectInfoDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouRequireInfoDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouVendorDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouVendorInfoDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.*;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDesignPlan;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProcessNodeEnum;
import com.midea.cloud.srm.sou.designplans.service.DesignPlanService;
import com.midea.cloud.srm.sou.purinq.dao.*;
import com.midea.cloud.srm.sou.purinq.plugin.event.init.domain.ExtPurInqSouInitDomainService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.SouProcessEventService;
import com.midea.cloud.srm.sou.sourcing.spi.init.ApiSouInitEventHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.editproject.SouProjectEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editrequrie.SouRequireEditPO;
import com.midea.cloud.srm.sou.sourcing.spi.init.editvendor.SouVendorEditPO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouInitEventHandler extends ApiSouInitEventHandler {

    @Autowired
    private ExtPurInqSouInitDomainService extPurInqSouInitDomainService;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouProcessEventService souProcessEventService;
    @Autowired
    private ExtPurInqSouVendorDAO extPurInqSouVendorDAO;
    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private ExtPurInqSouCurrencyDAO extPurInqSouCurrencyDAO;
    @Autowired
    private ExtPurInqSouVendorDelDAO extPurInqSouVendorDelDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private ExtPurInqSouVendorRoundDAO extPurInqSouVendorRoundDAO;
    @Autowired
    private ExtPurInqSouItemRoundDAO extPurInqSouItemRoundDAO;
    @Autowired
    private DesignPlanService designPlanService;

    @Override
    @ApiOperation("项目信息保存前的额外处理")
    public void doHandlerBeforeEditProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType) {
        param.setSequenceCode(ExtPurInqSouProject.EXT_SEQ_SOU_PURINQ_NO);
        param.getProject().setStandardCurrency("RMB");
    }

    @Override
    @ApiOperation("项目信息保存后的额外处理")
    public void doHandlerAfterEditProject(ApiSouProjectInfoDTO param, boolean isCopy, String souType, SouProjectEditPO po) {
        super.doHandlerAfterEditProject(param, isCopy, souType, po);
        ApiPurInqSouProjectInfoDTO inqParam = SouObjectXUtil.convertTargetObj(param, ApiPurInqSouProjectInfoDTO.class);

        // 1: 保存基本信息
        extPurInqSouInitDomainService.editProjectInfo(inqParam.getProject().getProjectId(), inqParam.getProject(), param.isTempSave());
        // 2: 保存可用币种s
        extPurInqSouInitDomainService.editCurrency(inqParam.getProject(), inqParam.getCurrencyList());
    }

    @Override
    @ApiOperation("项目需求保存后的额外处理")
    public void doHandlerAfterEditRequires(ApiSouRequireInfoDTO param, boolean isCopy, @Nullable Long userId, String souType, SouRequireEditPO po) {
        super.doHandlerAfterEditRequires(param, isCopy, userId, souType, po);
        ApiPurInqSouRequireInfoDTO inqParam = SouObjectXUtil.convertTargetObj(param, ApiPurInqSouRequireInfoDTO.class);
        SouProject souProject = souProjectDAO.getById(inqParam.getProjectId());

        // 1: 保存物料需求
        extPurInqSouInitDomainService.editRequireInfo(souProject, inqParam.getItemList(), userId, inqParam.isTempSave());
    }

    @Override
    @ApiOperation("邀请供应商保存后的额外处理")
    public void doHandlerAfterEditVendors(ApiSouVendorInfoDTO param, boolean isCopy, String souType, SouVendorEditPO po) {
        super.doHandlerAfterEditVendors(param, isCopy, souType, po);
        ApiPurInqSouVendorInfoDTO inqParam = SouObjectXUtil.convertTargetObj(param, ApiPurInqSouVendorInfoDTO.class);
        // 1: 处理额外的询价供应商信息
        if (CollectionUtils.isNotEmpty(inqParam.getVendorList())) {
            List<ExtPurInqSouVendor> inqVendorList = new ArrayList<>(inqParam.getVendorList().size());
            for (ApiPurInqSouVendorDTO vendor : inqParam.getVendorList()) {
                ExtPurInqSouVendor entity = new ExtPurInqSouVendor();
                inqVendorList.add(entity);

                entity.setSouVendorId(vendor.getSouVendorId());
                entity.setProjectId(param.getProjectId());
                AssertUtils.notNull(vendor.getSourceFromType(), "缺少sourceFromType参数");
                entity.setNewVendorTag(Enable.N);
            }
            extPurInqSouVendorDAO.saveOrUpdate(param.getProjectId(), inqVendorList, ExtPurInqSouVendor::getProjectId);
        }
        // 2: 更新节点信息
        if (!isCopy && !param.isTempSave()) {
            souProcessEventService.updateProcessNodeStatusForInit(param.getProjectId(), SouProcessNodeEnum.scoreRule.name(), param.isTempSave() ? Enable.N : Enable.Y, souType);
        }
    }

    @Override
    @ApiOperation("删除寻源单后的额外处理")
    public void doHandlerAfterRemoveSou(long projectId, String souType, ApiSouInitDetailVO initInfo) {
        super.doHandlerAfterRemoveSou(projectId, souType, initInfo);

        ExtPurInqSouProject inqProject = extPurInqSouProjectDAO.getById(projectId);

        extPurInqSouProjectDAO.removeById(projectId);
        extPurInqSouItemDAO.lambdaUpdate().eq(ExtPurInqSouItem::getProjectId, projectId).remove();
        extPurInqSouCurrencyDAO.lambdaUpdate().eq(ExtPurInqSouCurrency::getProjectId, projectId).remove();
        extPurInqSouVendorDAO.lambdaUpdate().eq(ExtPurInqSouVendor::getProjectId, projectId).remove();
        extPurInqSouVendorDelDAO.lambdaUpdate().eq(ExtPurInqSouVendorDel::getProjectId, projectId).remove();

        // 回溯上游
        designPlanService.lambdaUpdate()
                .set(SccSouChDesignPlan::getHasCreatePurInq, Enable.N)
                .set(SccSouChDesignPlan::getSouNo, null)
                .eq(SccSouChDesignPlan::getDesignId, inqProject.getDesignId())
                .update();
    }

    @Override
    @ApiOperation("作废寻源单后的额外处理")
    public void doHandlerAfterCancelSou(ApiSouCancelDTO param, String souType) {
        super.doHandlerAfterCancelSou(param, souType);
        // 回溯上游
        ExtPurInqSouProject inqProject = extPurInqSouProjectDAO.getById(param.getProjectId());
        designPlanService.lambdaUpdate()
                .set(SccSouChDesignPlan::getHasCreatePurInq, Enable.N)
                .set(SccSouChDesignPlan::getSouNo, null)
                .eq(SccSouChDesignPlan::getDesignId, inqProject.getDesignId())
                .update();
    }

    @Override
    @ApiOperation("复制寻源单--构造立项基本信息")
    public ApiSouProjectInfoDTO doHandlerForCopyProjectInfo(long projectId, String souType) {
        throw new IllegalArgumentException("集采询比价暂不支持单据复制功能");
    }

    @Override
    @ApiOperation("复制寻源单--构造立项物料需求")
    public ApiSouRequireInfoDTO doHandlerForCopyRequireInfo(long newProjectId, long oldProjectId, String souType) {
        throw new IllegalArgumentException("集采询比价暂不支持单据复制功能");
    }

    @Override
    @ApiOperation("复制寻源单--构造立项邀请供应商")
    public ApiSouVendorInfoDTO doHandlerForCopyVendorInfo(long oldProjectId, String souType, long newProjectId,
                                                          Map<Long/* oldSouItemId */, Long/* newSouItemId */> souItemIdMap) {
        throw new IllegalArgumentException("集采询比价暂不支持单据复制功能");
    }

    @Override
    @ApiOperation("复制寻源单--构造立项评分规则")
    public ApiSouInitScoreInfoDTO doHandlerForCopyScoreInfo(long oldProjectId, String souType, long newProjectId) {
        throw new IllegalArgumentException("集采询比价暂不支持单据复制功能");
    }

    @Override
    @ApiOperation("立项审批通过后的额外处理")
    public void doHandlerAfterApprovalPass(long projectId, String souType) {
        super.doHandlerAfterApprovalPass(projectId, souType);
        SouProject souProject = souProjectDAO.getById(projectId);
        // 1: 更新简易询价冗余状态
        extPurInqSouProjectDAO.lambdaUpdate()
                .set(ExtPurInqSouProject::getExtProjectStatus, ExtPurInqSouProjectStatusEnum.valueOf(souProject.getProjectStatus().name()))
                .eq(ExtPurInqSouProject::getProjectId, projectId)
                .update();
        // 2: 构造物料轮次信息
        List<SouItem> souItemList = souItemDAO.list(SouItem::getProjectId, projectId);
        List<ExtPurInqSouItemRound> itemRoundList = new ArrayList<>(souItemList.size()); {
            souItemList.forEach(souItem -> {
                ExtPurInqSouItemRound itemRound = new ExtPurInqSouItemRound();
                itemRoundList.add(itemRound);

                itemRound.setInqSouItemRoundId(IdGenrator.generate());
                itemRound.setProjectId(projectId);
                itemRound.setSouItemId(souItem.getSouItemId());
                itemRound.setRound(1);
                itemRound.setCanOrder(Enable.Y);
            });
        }
        // 3: 构造供应商轮次信息
        List<SouVendor> souVendorList = souVendorDAO.list(SouVendor::getProjectId, projectId);
        List<ExtPurInqSouVendorRound> vendorRoundList = new ArrayList<>(souVendorList.size()); {
            souVendorList.forEach(souVendor -> {
                ExtPurInqSouVendorRound vendorRound = new ExtPurInqSouVendorRound();
                vendorRoundList.add(vendorRound);

                vendorRound.setInqSouVendorRoundId(IdGenrator.generate());
                vendorRound.setProjectId(projectId);
                vendorRound.setVendorId(souVendor.getVendorId());
                vendorRound.setRound(1);
                vendorRound.setCanOrder(Enable.Y);
            });
        }

        extPurInqSouItemRoundDAO.saveBatch(itemRoundList);
        extPurInqSouVendorRoundDAO.saveBatch(vendorRoundList);
    }

    @Override
    @ApiOperation("立项审批未通过后的额外处理")
    public void doHandlerAfterApprovalUnPass(ApiSouCreateApprovalUnPassDTO param, String souType) {
        if (SouApprovalStatusEnum.ABANDONED.equals(param.getCreateApprovalStatus())) {
            // 废弃单据

            // 回溯上游
            ExtPurInqSouProject inqProject = extPurInqSouProjectDAO.getById(param.getProjectId());
            designPlanService.lambdaUpdate()
                    .set(SccSouChDesignPlan::getHasCreatePurInq, Enable.N)
                    .set(SccSouChDesignPlan::getSouNo, null)
                    .eq(SccSouChDesignPlan::getDesignId, inqProject.getDesignId())
                    .update();
        }
    }

    @Override
    public String matchModule() {
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
