
package com.midea.cloud.srm.sou.sourcing.spi.init.doneproject;

import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtSouInitProjectDoneHandler implements ISouSpiBean {

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


    @ApiOperation("项目归档后的额外处理")
    public void doHandlerAfterEndProject(ExtSouProjectDto projectDto, String souType) {
        if (CollectionUtils.isNotEmpty(projectDto.getDemandList())) {
            Set<Long> requirementHeadIds = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementHead")
                    .in(RequirementHead::getRequirementHeadNum, projectDto.getDemandList().stream().map(ExtSouDemand::getApplicantNo).distinct().collect(Collectors.toList())), RequirementHead.class)
                    .stream().map(RequirementHead::getRequirementHeadId).collect(Collectors.toSet());

            if (!requirementHeadIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update(ExtPrSouRequirementHead.class)
                        .set(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.FINISH)
                        .eq(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.PROJECT)
                        .in(ExtPrSouRequirementHead::getRequirementHeadId, new ArrayList<>(requirementHeadIds)));
            }
        }
    }

}
