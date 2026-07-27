package com.midea.cloud.srm.cm.contract.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.cm.contract.service.IContractPerPlanExtService;
import com.midea.cloud.srm.cm.old.perform.service.PerPlanService;
import com.midea.cloud.srm.feign.ContractPerformanceExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.contract.entity.ContractMaterial;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlanMilestone;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName;
import com.midea.cloud.srm.model.contract.constant.ContractMaterialFieldName;
import com.midea.cloud.srm.model.contract.dto.PerPlanExt;
import com.midea.cloud.srm.model.contract.dto.PerPlanMilestoneExtDto;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PjPerfTemplateHeaderQueryDTO;
import com.midea.cloud.srm.model.perf.projectscore.entity.ProjectScoreHeader;
import com.midea.cloud.srm.model.perf.template.entity.PerfTemplateHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName.CONTRACT_HEAD_ID_FIELD;

/**
 * @author 100014336 ganyh19
 */
@Service
@Slf4j
public class ContractPerPlanExtServiceImpl implements IContractPerPlanExtService {

    @Autowired
    private ContractPerformanceExtClient contractPerformanceExtClient;

    @Autowired
    private PerPlanService perPlanService;

    @Autowired
    private QlService qlService;

    @Override
    public Boolean isNeedPerfEvalByMilestoneId(Long milestoneId) {
        boolean isNeed = false;
        if (ObjectUtil.isNotNull(milestoneId)) {
            PerPlanMilestoneExtDto perPlanMilestoneExtDto = qlService.readByKey(ContractMqlSchemaType.PerPlanMilestone.getType(),milestoneId,PerPlanMilestoneExtDto.class);
            PerPlanExt perPlan = formateExtPerPanByMilestone(getPerPanByMilestoneId(perPlanMilestoneExtDto));
            isNeed = isNeedPerfEval(perPlan,perPlanMilestoneExtDto);
        }

        return isNeed;
    }

    /**
     * 转换实体类
     * @param perPlan
     * @return
     */
    private PerPlanExt formateExtPerPanByMilestone(PerPlan perPlan) {
        if(ObjectUtils.anyNull(perPlan)) {
            return null;
        }
        PerPlanExt perPlanExt = new PerPlanExt();
        BeanCopyUtil.copyProperties(perPlanExt, perPlan);
        return perPlanExt;
    }

    @Override
    public PerPlan getPerPanByMilestoneId(PerPlanMilestoneExtDto perPlanMilestone){
        LambdaQueryWrapper<PerPlan> perPlanLambdaQueryWrapper = Wrappers.lambdaQuery();
        PerPlan perPlan = null;
        if(ObjectUtil.isNotNull(perPlanMilestone)) {
            Long perPanId = perPlanMilestone.getPerPlanId();
            perPlan = perPlanService.selectFirst(perPlanLambdaQueryWrapper.eq(PerPlan::getPerPlanId, perPanId));
        }
        return perPlan;
    }

    @Override
    public List<Serializable> setHasCreatePerf(String contractNo, String milestoneType, Enable enable) {
        List<Record> perPlans = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.PerPlan.getType()).eq(PerPlan::getContractNo,contractNo),Record.class);
        List<Serializable> ids = new ArrayList<>();
        if(CollUtil.isNotEmpty(perPlans)){
           ids = qlService.updateByWrapper(QlWrappers.update(ContractMqlSchemaType.PerPlanMilestone.getType())
                    .eq(PerPlanMilestone::getPerPlanId,perPlans.get(0).get(PerPlan::getPerPlanId))
                    .eq(PerPlanMilestone::getMilestoneType,milestoneType).set("extCreatePerformFlag",enable.name()));
        }

        return ids;
    }


    @Override
    public Boolean isNeedPerfEval(PerPlanExt perPlan, PerPlanMilestoneExtDto milestone) {
        Boolean isNeed = false;
        /*获取履约计划*/
        if (ObjectUtil.isAllNotEmpty(perPlan,milestone)) {
            /*通过合同获得物料里的品类*/
            Long contractHeadId = perPlan.getContractHeadId();
            //现在时间大于计划开始时间且已创建评分项目
            if(ObjectUtils.allNotNull(milestone, milestone.getPlanStartDate()) && milestone.getPlanStartDate().before(new Date())
                    &&!Enable.Y.name().equalsIgnoreCase(milestone.getExtCreatePerformFlag())){
                //是否免评分
                if(checkIfNoEval(contractHeadId)){
                    //是否项目化模型配置
                    List<Record> mts = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractMaterial.getType()).select(ContractMaterial::getMaterialId,ContractMaterial::getCategoryId,ContractMaterial::getCategoryCode).eq(ContractMaterial::getContractHeadId,contractHeadId),Record.class);
                    if(CollUtil.isNotEmpty(mts)){
                        Record mt = mts.get(0);

                        //查询是否有绩效配置
                        PjPerfTemplateHeaderQueryDTO templateQueryDto = new PjPerfTemplateHeaderQueryDTO();
                        //项目化
                        templateQueryDto.setAttribute1("PROJECT");
                        templateQueryDto.setCategoryId(mt.getLong(ContractMaterialFieldName.CATEGORY_ID));
                        templateQueryDto.setTemplateStatus("VALID");

                        PageInfo<PerfTemplateHeader> perfTemplateHeaderPageInfo = contractPerformanceExtClient.listPefTemplateHeaderPage(templateQueryDto);
                        if (CollUtil.isNotEmpty(perfTemplateHeaderPageInfo.getList())) {
                            // 判断是否取消状态，取消状态不显示
                            String extCancelBy = perPlan.getExtCancelBy();
                            if(StringUtils.isEmpty(extCancelBy)){
                                isNeed = true;
                            }
                        }
                    }
                }
            }
        }
        return isNeed;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(PerPlanExt perPlanExt) {
        String contractNo = perPlanExt.getContractNo();
        Long perPlanId = perPlanExt.getPerPlanId();
        final String userName = AppUserUtil.getUserName();
        if(StringUtils.isEmpty(contractNo) || perPlanId == null){
            throw new BaseException("合同编号和合同履约计划ID不能为空");
        }
        Record perPlan = new Record();
        perPlan.put(PerPlanExt::getPerPlanId, perPlanId);
        perPlan.put(PerPlanExt::getStatus, "CANCEL");
        perPlan.put(PerPlanExt::getContractHeadId, perPlanExt.getContractHeadId());
        perPlan.put(PerPlanExt::getContractNo, contractNo);
        perPlan.put(PerPlanExt::getExtCancelBy, userName);
        perPlan.put(PerPlanExt::getExtCancelDesc, perPlanExt.getExtCancelDesc());
        perPlan.put(PerPlanExt::getExtCancelFileId, perPlanExt.getExtCancelFileId());
        perPlan.put(PerPlanExt::getExtCancelFileName, perPlanExt.getExtCancelFileName());
        perPlan.put(PerPlanExt::getExtCancelTime, new Date());

        qlService.update(ContractMqlSchemaType.PerPlan.getType(), Collections.singletonList(perPlan));
        // 更新相关单据状态为【取消】
        contractPerformanceExtClient.updateProjectScoreHeader(contractNo);
        contractPerformanceExtClient.updateProjectScoreItems(contractNo);
        contractPerformanceExtClient.updateProjectScoreMan(contractNo);

    }

    private boolean checkIfNoEval(Long contractHeadId) {
        List<Record> contracts = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractHead.name()).select(ContractHeadFieldName.EXT_EVALUATE_FLAG_FIELD).eq(CONTRACT_HEAD_ID_FIELD, contractHeadId), Record.class);
        if (CollUtil.isNotEmpty(contracts)) {
            Record ct = contracts.get(0);
            //需要评分判断
            return !Enable.Y.name().equalsIgnoreCase(ct.getString(ContractHeadFieldName.EXT_EVALUATE_FLAG_FIELD));
        }
        return false;
    }

}
