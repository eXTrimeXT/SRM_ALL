package com.midea.cloud.srm.biz.pj.sou.comp.init.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.init.domain.CompInitDomainService;
import com.midea.cloud.srm.biz.pj.sou.comp.init.service.CompSouInitEventWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitEventService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.cooperate.SccNpmPrRequireHead;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouProjectInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouScoreInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouVendorInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import com.midea.cloud.srm.model.pm.pr.requirement.dto.RequirementManageDTO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 竞价 - 立项业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouInitEventWebServiceImpl implements CompSouInitEventWebService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouInitEventService souInitEventService;
    @Autowired
    private CompInitDomainService compInitDomainService;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private BaseClient baseClient;

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    com.midea.cloud.srm.feign.pj.sou.SouSignClient souSignClient;



    /**
     * 暂存/提交项目信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public long/* projectId */ editProjectInfo(ApiCompSouProjectInfoDTO param, boolean isCopy) {
        /* 1: 保存数据 */
        ApiSouProjectInfoDTO projectInfoDTO = SouObjectXUtil.convertTargetObj(param, ApiSouProjectInfoDTO.class);
        souInitEventService.editProject(projectInfoDTO, isCopy, SouTypeEnum.comp.name());

        return projectInfoDTO.getProject().getProjectId();
    }

    /**
     * 需求池创建寻源单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SouProject tempSaveCompFromReq(List<RequirementManageDTO> reqParams) {
        /* 1: 保存立项数据 */
        ApiSouProjectInfoDTO projectInfo = new ApiSouProjectInfoDTO();
        {
            projectInfo.setProject(new ApiSouProjectEditDTO());
            {
                /* 寻源单名称 */
                String souName = "需求池转寻源_";
                {
                    int index = 0;
                    while (true) {
                        index++;
                        long count = souProjectDao.lambdaQuery()
                                .eq(SouProject::getSouType, SouTypeEnum.comp.name())
                                .eq(SouProject::getSouName, souName + index)
                                .count();
                        if (count <= 0) {
                            break;
                        }
                    }
                    souName = souName + index;
                }
                projectInfo.getProject().setSouName(souName);
                /* 是否同步到价格库 */
                projectInfo.getProject().setIsSyncToPriceLibrary(Enable.Y);
                /* 来源类型 */
                projectInfo.getProject().setSourceFromType(SouSourceFromTypeEnum.PURCHASE_REQ.name());
                /* 来源单据ID */
                projectInfo.getProject().setSourceFromId(reqParams.get(0).getRequirementHeadId());
                /* 来源单据号 */
                projectInfo.getProject().setSourceFromNo(reqParams.get(0).getRequirementHeadNum());
                /* 报价类型 */
                projectInfo.getProject().setOrderType(SouOrderTypeEnum.SIMPLE);
            }
            projectInfo.setTempSave(true);
            projectInfo.setSequenceCode(SequenceCodeConstant.SOU.SEQ_COMP_NO);
        }
        souInitEventService.editProject(projectInfo, true, SouTypeEnum.comp.name());
        long projectId = projectInfo.getProject().getProjectId();
        /* 2: 保存项目需求 */
        ApiSouRequireInfoDTO requireInfo = compInitDomainService.handlerSouItemsFromReq(projectId, reqParams);
        souInitEventService.editRequires(requireInfo, true, null, SouTypeEnum.comp.name());

        return souProjectDao.getById(projectId);
    }

    /**
     * 暂存/提交物料需求
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editRequireInfo(ApiCompSouRequireInfoDTO param, boolean isCopy, @Nullable Long currentUserId) {
        /* 1: 保存数据 */
        souInitEventService.editRequires(SouObjectXUtil.convertTargetObj(param, ApiSouRequireInfoDTO.class), isCopy, currentUserId, SouTypeEnum.comp.name());
    }

    /**
     * 暂存/提交邀请供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editInviteSupplier(ApiCompSouVendorInfoDTO param, boolean isCopy) {
        /* 1: 保存邀请供应商 */
        souInitEventService.editVendors(SouObjectXUtil.convertTargetObj(param, ApiSouVendorInfoDTO.class), isCopy, SouTypeEnum.comp.name());
    }

    /**
     * 暂存/提交评分规则
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void editScoreRule(ApiCompSouScoreInfoDTO param) {
        /* 1: 保存评分规则信息 */
        souInitEventService.editScoreRule(SouObjectXUtil.convertTargetObj(param, ApiSouInitScoreInfoDTO.class), false, SouTypeEnum.comp.name());
    }

    /**
     * 删除询价单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeComp(long projectId) {
        /* 1: 删除寻源数据 */
        souInitEventService.removeSou(projectId, SouTypeEnum.comp.name());
    }

    /**
     * 作废寻源单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelComp(ApiSouCancelDTO param) {
        /* 1: 作废寻源单 */
        souInitEventService.cancelSou(param, SouTypeEnum.comp.name());
    }

    /**
     * 需求池创建寻源单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SouProject editSouBidInfo(Map<String, Object> params) {
        /* 1: 保存立项数据 */
        JSONObject jsonObject = (JSONObject) params.get("projectInfo");
        SouProject projectInfo = jsonObject.toJavaObject(SouProject.class);
        projectInfo.setSouNo(baseClient.seqGen("SEQ_SOU_COMP_NO"));
        projectInfo.setCreateApprovalStatus(SouApprovalStatusEnum.DRAFT);
        projectInfo.setProjectStatus(SouProjectStatusEnum.DRAFT);
        projectInfo.setSouType("comp");
        projectInfo.setStandardCurrency("RMB");
        projectInfo.setPublicRules("公开规则");

        /* 取招标计划池项目名称  bidding_number */
        SccNpmPrRequireHead sccNpmPrRequireHead = qlOpenClient.read(ContextPath.SUP_CE,"ExtPrSouRequirementHead",projectInfo.getSourceFromId(), SccNpmPrRequireHead.class);
        projectInfo.setSouName(sccNpmPrRequireHead.getProjectName());

        //所属板块编码
        String orgBuCode = sccNpmPrRequireHead.getOrgBuCode();
        //招标编号
        String extProjectNo =  souSignClient.generateSeq(orgBuCode);
        projectInfo.setExtProjectNo(extProjectNo);

        souProjectDao.saveOrUpdate(projectInfo);


        /* 1.1: 保存保证金信息 */
        CompSouProject compSouProject = new CompSouProject();
        BeanUtils.copyProperties(projectInfo, compSouProject);
        compSouProjectDao.saveOrUpdate(compSouProject);

        /* 2: 保存项目需求 */
        JSONArray jsonArray = (JSONArray) params.get("souItemInfo");
        if (CollectionUtils.isNotEmpty(jsonArray)) {
            List<SouItem> listSouItemInfo = JSONObject.parseArray(jsonArray.toJSONString(), SouItem.class);
            souItemDao.saveOrUpdateBatch(listSouItemInfo);
        }

        /* 3: 保存供应商信息 */
        JSONArray vendorJsonArray = (JSONArray) params.get("vendorInfo");
        if (CollectionUtils.isNotEmpty(jsonArray)) {
            List<SouVendor> listSouVendorInfo = JSONObject.parseArray(vendorJsonArray.toJSONString(), SouVendor.class);
            souVendorDao.saveOrUpdateBatch(listSouVendorInfo);
        }

        return projectInfo;
    }

}
