
package com.midea.cloud.srm.sou.sourcing.spi.init.delprojects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouEndTimeDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouRound;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.meiql.recommvendor.service.SouRecommvendorRiskService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editendtimes.ExtSouEndTimePo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouInitProjectDelHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private SouRecommvendorRiskService recommvendorRiskService;
    @Autowired
    private QlOpenClient qlOpenClient;


    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }


    @ApiOperation("删除招标单前的额外处理")
    public void doHandlerBeforeDelProject(Long projectId, String souType) {
        ExtSouProject project = projectService.getById(projectId);
        AssertUtils.notNull(project, "项目信息不存在！");

        if(!project.getSouType().equals(souType)) {
            throw new BaseException("项目类型不匹配！");
        }

        if(!SouBiddingProStatusEnum.DRAW_UP.getCode().equals(project.getProjectStatus())) {
            throw new BaseException("项目非拟定状态不允许删除！");
        }

        //还原标书拟定状态
        recommvendorRiskService.rollbackPlanPoolForBid(Collections.singletonList(project));
    }



    @ApiOperation("调整投标截止时间后的额外处理")
    public void doHandlerAfterDelProject(ExtSouProjectDto projectDto, String souType) {
        if (CollectionUtils.isNotEmpty(projectDto.getDemandList())) {
            Set<Long> requirementHeadIds = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementHead")
                    .in(RequirementHead::getRequirementHeadNum, projectDto.getDemandList().stream().map(d -> d.getApplicantNo()).distinct().collect(Collectors.toList())), RequirementHead.class)
                    .stream().map(RequirementHead::getRequirementHeadId).collect(Collectors.toSet());

            if (!requirementHeadIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update(ExtPrSouRequirementHead.class)
                        .set(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.EXECUTING)
                        .set(ExtPrSouRequirementHead::getHasCreateVendorRecommend, Enable.Y)
                        .set(ExtPrSouRequirementHead::getRecommendVendorBillId, projectDto.getProjectId())
                        .set(ExtPrSouRequirementHead::getRecommendVendorBillNo, projectDto.getExtRecommendNo())
                        .in(ExtPrSouRequirementHead::getRequirementHeadId, new ArrayList<>(requirementHeadIds)));
            }
        }
    }

}
