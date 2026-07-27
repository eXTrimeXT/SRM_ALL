package com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.impl;

import com.midea.cloud.common.enums.contract.ContractStatus;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.constants.CancleCacheConstants;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.context.RequirementCancleContext;
import com.midea.cloud.srm.supcooperate.ext.requirementcancles.service.ReuirementCancleCommonService;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.cancle.event.canclerequirements.IReuirementCancleEventPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.IRequirementInitEditPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.RequirementInitEditContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm 采购申请取消引导类
 *
 * @author srm
 * @date 2024-05-20
 */
@Service
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class ReuirementCancleCommonServiceImpl implements ReuirementCancleCommonService {
    @Autowired
    private QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Override
    public RequirementCancleContext cancleReuirement(List<Long> requirementHeadIdList, Map<Long, String> requirementHeadNumMap) {
        // 1: 初始化上下文
        RequirementCancleContext context = new RequirementCancleContext();
        context.setRequirementHeadIdList(requirementHeadIdList);
        context.setRequirementHeadNumMap(requirementHeadNumMap);
        context.setLocalCache(new HashMap<>(15));
        //初始化数据
        List<Record> extPrSouRequirementHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.EXT_PR_SOU_REQUIREMENT_HEAD).in(ExtPrSouRequirementHead::getRequirementHeadId, requirementHeadIdList), Record.class);
        if(CollectionUtils.isNotEmpty(extPrSouRequirementHeadList)) {
            context.getLocalCache().put(CancleCacheConstants.EXT_CANCLE_REQUIRMENTHEAD_LIST, extPrSouRequirementHeadList);
        }
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IReuirementCancleEventPlugin.class, context).judgeCancleRequirementAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IReuirementCancleEventPlugin .class, context).beforeCancleRequirementAuth(context);
        // 4: 数据保存
        context = SdkPluginProxy.proxy(IReuirementCancleEventPlugin .class, context).executeCancleRequirementAuth(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IReuirementCancleEventPlugin .class, context).afterCancleRequirementAuth(context);

        return context;

    }

    @Override
    public List<RecordDTO> queryReuiremnetAsWithContract(String requirementHeadNum) {
        List<RecordDTO> recordDTOList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.NPM_SOU_DEMAND).eq(ExtSouDemand::getApplicantNo, requirementHeadNum));
        if(CollectionUtils.isEmpty(recordDTOList)) {
            return new ArrayList<>();
        }
        List<RecordDTO> souProjectList = qlOpenClient.query(ContextPath.SOU, QlOpenWrappers.query(MqlType.NPM_SOU_PROJECT).in(ExtSouProject::getProjectId, recordDTOList.stream().map(r -> r.get(ExtSouDemand::getProjectId)).distinct().collect(Collectors.toList())).eq(ExtSouProject::getSouType, SouTypeEnum.bid.name()).ne(ExtSouProject::getProjectStatus, SouBiddingProStatusEnum.ABANDON.getCode()));
        if(CollectionUtils.isEmpty(souProjectList)) {
            return new ArrayList<>();
        }
        List<RecordDTO> contractList = qlOpenClient.query(ContextPath.CM, QlOpenWrappers.query(MqlType.CONTRACT_HEAD).in("sourceNumber", souProjectList.stream().map(r -> r.get(ExtSouProject::getExtProjectNo)).distinct().collect(Collectors.toList())).notIn("contractStatus", Arrays.asList(ContractStatus.ABANDONED.name(), ContractStatus.TERMINATED.name())));

        return contractList;
    }
}
